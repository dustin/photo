/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoStorerThread.java,v 1.14 2002/06/26 21:12:50 dustin Exp $
 */

package net.spy.photo.util;

import java.sql.*;
import java.util.*;
import java.io.*;

import net.spy.*;
import net.spy.photo.*;
import net.spy.util.*;

/**
 * Store images in the DB.	Uploaded images go directly into the cache and
 * are referenced in the album table.  They're usable without being stored,
 * and may in fact never be pulled from the DB for display unless the cache
 * is cleared.	It's quite important to make sure the images make it into
 * the database for long-term storage, however.
 */
public class PhotoStorerThread extends Thread {

	// chunks should be divisible by 57
	private final static int CHUNK_SIZE=2052;

	/**
	 * Get a PhotoStorerThread.
	 */
	public PhotoStorerThread() {
		super("storer_thread");
		this.setDaemon(true);
	}

	// Takes an image_id, pulls in the image from cache, and goes about
	// encoding it to put it into the database in a transaction.  The last
	// query in the transaction records the image having been stored.
	private void storeImage(int image_id) throws Exception {
		PhotoImageHelper p = new PhotoImageHelper(image_id);
		SpyDB pdb = new SpyDB(new PhotoConfig());
		PhotoImage pi = p.getImage();
		System.err.println("Storer: Got image for " + image_id + " " 
			+ pi.size() + " bytes of data to store.");
		// This is an awkward way of doing this.
		Vector v=new Vector();
		byte data[]=pi.getData();

		// i will be incremented inside the loop
		for(int i=0; i<data.length; ) {
			// How much we have left
			int max=data.length-i;
			
			// Make sure we don't get more than 2k at a time.
			if(max>CHUNK_SIZE) {
				max=CHUNK_SIZE;
			}

			// Get the thing to store.
			byte b[]=new byte[max];
			for(int j=0; j<max; j++) {
				b[j]=data[i++];
			}

			v.addElement(b);
		}

		Connection db=null;
		try {
			int i=0, n=0;
			db = pdb.getConn();
			db.setAutoCommit(false);
			PreparedStatement pst=db.prepareStatement(
				"insert into image_store (id, line, data) values(?,?,?)");

			Base64 base64=new Base64();
			StringBuffer sdata = new StringBuffer();

			pst.setInt(1, image_id);

			for(; i<v.size(); i++) {
				String tmp = base64.encode((byte[])v.elementAt(i));
				tmp=tmp.trim();

				if(sdata.length() < CHUNK_SIZE) {
					sdata.append(tmp);
					sdata.append("\n");
				} else {
					pst.setInt(2, n++);
					pst.setString(3, sdata.toString());
					pst.executeUpdate();
					sdata=new StringBuffer(tmp);
				}
			}
			// OK, this is sick, but another one right now for the spare.
			if(sdata.length() > 0) {
				System.err.println("Storer:  Storing spare.");
				pst.setInt(2, n++);
				pst.setString(3, sdata.toString());
				pst.executeUpdate();
			}
			System.err.println("Storer:  Stored " + n + " lines of data for "
				+ image_id + ".");
			pst.close();
			pst=null;
			PreparedStatement pst2=db.prepareStatement(
				"update photo_logs set extra_info=text(now())\n"
					+ "  where photo_id = ?\n"
					+ "  and log_type = get_log_type('Upload')");
			pst2.setInt(1, image_id);
			int rows=pst2.executeUpdate();
			// We should update exactly one row.  We can live with 0, but
			// more than one could be bad.
			switch(rows) {
				case 0:
					System.err.println("WARNING:  No upload log entry was "
						+ "found for " + image_id);
					break;
				case 1:
					// Expected
					break;
				default:
					throw new Exception(
						"Expected to update 1 upload log entry , updated "
						+ rows);
			}
			db.commit();
			// Go ahead and generate a thumbnail.
			p.getThumbnail();
		} catch(Exception e) {
			// If anything happens, roll it back.
			e.printStackTrace();
			try {
				db.rollback();
			} catch(Exception e3) {
				e3.printStackTrace();
			}
		} finally {
			if(db!=null) {
				try {
					db.setAutoCommit(true);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			pdb.close();
		}
	}

	// Get a list of images that have been added, but not yet added into
	// the database.
	// Returns the number of things found to flush
	private int doFlush() {
		SpyDB db = new SpyDB(new PhotoConfig());
		int rv=0;
		Vector v = new Vector();
		try {
			// See what's not been stored.
			String query="select distinct s.id as sid, a.id as aid\n"
				+ " from album a left outer join image_store s using (id)\n"
				+ " where s.id is null";
			ResultSet rs=db.executeQuery(query);
			while(rs.next()) {
				v.addElement(rs.getString("aid"));
			}
			rs.close();
			db.close();
		} catch(Exception e) {
			// Do nothing, we'll try again later.
			e.printStackTrace();
		}

		// Got the vector, now store the actual images.  This is done so
		// that we don't hold the database connection open whlie we're
		// making the list *and* getting another database connection to act
		// on it.
		if(v.size() > 0) {
			rv=v.size();
			for(int i = 0; i<v.size(); i++) {
				String stmp = (String)v.elementAt(i);
				try {
					storeImage(Integer.valueOf(stmp).intValue());
				} catch(Exception e) {
					e.printStackTrace();
					// Return 0 so we won't try again *right now*, but we will
					// in a bit.
					rv=0;
				}
			}

		}

		// Return the number we found.
		return(rv);
	}

	/**
	 * Sit around and flush.
	 */
	public void run() {
		// Do a flush at the beginning, just in case stuff has been
		// building up.
		try {
				doFlush();
		} catch(Exception e1) {
			// Don't care, all these can fail, we'll just keep trying.
		}
		for(;;) {
			try {
				PhotoConfig p = new PhotoConfig();

				// Wait up to 1 day to perform a scan.
				synchronized(this) {
					wait(86400*1000);
				}
				// After a wait finishes, sleep another five minutes, just
				sleep(300000);
			} catch(Exception e) {
				e.printStackTrace();
			}
			// Loop immediately as often as it flushes.
			while(doFlush()>0) {
				System.err.println(
					"doFlush() returned > 0, flushing again.");
			} // Flush loop
		} // Forever loop
	}
}
