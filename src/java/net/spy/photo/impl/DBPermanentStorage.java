package net.spy.photo.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import net.spy.SpyObject;
import net.spy.db.SpyDB;
import net.spy.photo.PermanentStorage;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImage;
import net.spy.photo.sp.GetImagesToFlush;
import net.spy.photo.util.ByteChunker;
import net.spy.stat.ComputingStat;
import net.spy.stat.Stats;
import net.spy.util.Base64;
import net.spy.util.CloseUtil;

/**
 * Permanent storage where images are stored in the database.
 */
public class DBPermanentStorage extends SpyObject implements PermanentStorage {

	// chunks should be divisible by 57
	public static final int CHUNK_SIZE=2052;

	private ComputingStat fetchStat=Stats.getComputingStat(
			"permstorage.db.fetch");
	private ComputingStat storeStat=Stats.getComputingStat(
			"permstorage.db.store");

	public void init() throws Exception {
		// nothing
	}

	public byte[] fetchImage(PhotoImage pi) throws Exception {
		long start=System.currentTimeMillis();
		getLogger().info("Fetching %s from DB", pi);
		// Average image is 512k. Create a buffer of that size to start.
		StringBuffer sdata = new StringBuffer(512 * 1024);

		SpyDB db = null;
		try {
			db = new SpyDB(PhotoConfig.getInstance());
			String query = "select data from image_store where id = ?\n"
					+ " order by line";
			PreparedStatement st = db.prepareStatement(query);
			st.setInt(1, pi.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				sdata.append(rs.getString("data"));
			}
			rs.close();
		} finally {
			CloseUtil.close(db);
		}

		Base64 base64 = new Base64();
		byte data[] = base64.decode(sdata.toString());

		long end=System.currentTimeMillis();
		fetchStat.add(end-start);
		return (data);
	}

	public void storeImage(PhotoImage pi, byte[] data) throws Exception {
		long start=System.currentTimeMillis();
		getLogger().info("Storer: Got image for %s, %d bytes of data", pi,
				pi.getSize());
		int imageId=pi.getId();

		SpyDB pdb = new SpyDB(PhotoConfig.getInstance());
		Connection db=null;
		try {
			db = pdb.getConn();
			db.setAutoCommit(false);
			PreparedStatement pst=db.prepareStatement(
					"delete from image_store where id = ?");
			pst.setInt(1, imageId);
			int rv=pst.executeUpdate();
			if(rv != 0) {
				getLogger().warn(
					"Removed current image data for %s when storing", imageId);
			}
			pst.close();

			// Get ready to store the new encoded data.
			pst=db.prepareStatement(
				"insert into image_store (id, line, data) values(?,?,?)");

			Base64 base64=new Base64();

			pst.setInt(1, imageId);

			// Get the encoded data
			int n=0;
			for(byte b[] : new ByteChunker(data, CHUNK_SIZE)) {
				pst.setInt(2, n++);
				pst.setString(3, base64.encode(b));
				int aff=pst.executeUpdate();
				assert aff == 1 : "Expected to update 1 record, updated " + aff;
			}

			getLogger().debug("Storer:  Stored %d lines of data for %d.",
					n, imageId);
			pst.close();
			pst=null;
			db.commit();
		} catch(Exception e) {
			// If anything happens, roll it back.
			getLogger().warn("Problem saving image", e);
			try {
				if(db!=null) {
					db.rollback();
				}
			} catch(Exception e3) {
				getLogger().warn("Problem rolling back transaction", e3);
			}
		} finally {
			if(db!=null) {
				try {
					db.setAutoCommit(true);
				} catch(Exception e) {
					getLogger().warn("Problem restoring autocommit", e);
				}
			}
			pdb.close();
		}
		long end=System.currentTimeMillis();
		storeStat.add(end-start);
	}

	public Collection<Integer> getMissingIds() throws Exception {
		GetImagesToFlush db = null;
		Collection<Integer> ids = new ArrayList<Integer>();
		try {
			db=new GetImagesToFlush(PhotoConfig.getInstance());
			ResultSet rs=db.executeQuery();
			while(rs.next()) {
				ids.add(rs.getInt("image_id"));
			}
			rs.close();
		} finally {
			CloseUtil.close(db);
		}
		return ids;
	}

}
