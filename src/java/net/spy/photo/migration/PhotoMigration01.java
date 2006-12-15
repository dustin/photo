// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import net.spy.db.SpyDB;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageHelper;

/** 
 * Migration for dimensions.
 */
public class PhotoMigration01 extends PhotoMigration {

	private void addColumns() throws Exception {
		SpyDB db=new SpyDB(PhotoConfig.getInstance());
		try {
			db.executeUpdate("alter table album add column width integer");
		} catch(Exception e) {
			System.err.println("Error adding column:  " + e);
		}
		try {
			db.executeUpdate("alter table album add column height integer");
		} catch(Exception e) {
			System.err.println("Error adding column:  " + e);
		}
		try {
			db.executeUpdate("alter table album add column tn_width integer");
		} catch(Exception e) {
			System.err.println("Error adding column:  " + e);
		}
		try {
			db.executeUpdate("alter table album add column tn_height integer");
		} catch(Exception e) {
			System.err.println("Error adding column:  " + e);
		}
	}

	private void getImages() throws Exception {
		int n=1;
		SpyDB db=new SpyDB(PhotoConfig.getInstance());

		while(n>0) {
			ArrayList<int[]> al=new ArrayList<int[]>();
			// Get a list of all of the images we haven't set width and height
			// on.
			ResultSet rs=db.executeQuery("select id from album\n"
				+ " where width is null or height is null\n"
				+ "  order by ts desc\n"
				+ "  limit 20");
			while(rs.next()) {
				int id=rs.getInt(1);
				getLogger().info("Doing image #" + id);
				PhotoImageHelper helper=new PhotoImageHelper(id);
				PhotoImage image=helper.getImage(null);

				getLogger().info("Image " + id + " is "
					+ image.getWidth() + "x" + image.getHeight());

				int dim[]=new int[3];
				dim[0]=id;
				dim[1]=image.getWidth();
				dim[2]=image.getHeight();

				al.add(dim);
			}

			n=al.size();
			getLogger().info("Updating " + n + " images.");

			// OK, now store them.
			for(int dim[] : al) {
				PreparedStatement st=db.prepareStatement(
					"update album set width=?, height=? where id=?"
					);
				st.setInt(1, dim[1]);
				st.setInt(2, dim[2]);
				st.setInt(3, dim[0]);
				getLogger().info("Saving image " + dim[0]);
				st.executeUpdate();
			}
		}
	}

	@Override
	protected boolean checkMigration() throws Exception {
		return((hasColumn("album", "tn_width"))
				&& (hasColumn("album", "tn_height"))
				&& (hasColumn("album", "width"))
				&& (hasColumn("album", "height")));
	}

	@Override
	protected void performMigration() throws Exception {
		// Add the new columns.
		addColumns();

		// This gets the images, and sets the width and height in the DB
		getImages();

		// Fetch the thumbnails, just so everything is precached for the
		// imageserver.
		fetchThumbnails();
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration01 mig=new PhotoMigration01();
		mig.migrate();
	}
}
