// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

import java.util.ArrayList;
import java.util.Iterator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.spy.db.Saver;
import net.spy.db.Savable;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;
import net.spy.db.AbstractSavable;

import net.spy.util.ProgressStats;

import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImageDataImpl;
import net.spy.photo.SavablePhotoImageData;

import net.spy.photo.sp.migration.GetAllImgIdsAndKws;

/**
 * Add the format table.
 */
public class PhotoMigration11 extends PhotoMigration {

	/**
	 * Get an instance of PhotoMigration11.
	 */
	public PhotoMigration11() {
		super();
	}

	private void updateKeywords() throws Exception {
		// Get the IDs
		GetAllImgIdsAndKws db=new GetAllImgIdsAndKws(PhotoConfig.getInstance());
		ArrayList imgs=new ArrayList();
		ResultSet rs=db.executeQuery();
		while(rs.next()) {
			imgs.add(new IdKw(rs.getInt("id"), rs.getString("keywords")));
		}
		rs.close();
		db.close();

		// The saver for below
		Saver saver=new Saver(PhotoConfig.getInstance());

		ProgressStats stat=new ProgressStats(imgs.size());

		// Now, flip through them and set the correct value.
		for(Iterator i=imgs.iterator(); i.hasNext(); ) {
			IdKw idkw=(IdKw)i.next();

			stat.start();

			SavablePhotoImageData savable=new SavablePhotoImageData(
				PhotoImageDataImpl.getData(idkw.id));
			savable.setKeywords(idkw.kw);

			saver.save(savable);

			stat.stop();
			System.out.println(stat);
		}
	}

	/** 
	 * Perform the migration.
	 */
	public void migrate() throws Exception {
		if(getRowCount("album_keywords_map") > 0) {
			System.err.println("Looks like you've already run this kit.");
		} else {
			updateKeywords();
		}
	}

	/** 
	 * Run the 9th migration script.
	 */
	public static void main(String args[]) throws Exception {
		PhotoMigration11 mig=new PhotoMigration11();
		mig.migrate();
	}

	private static class IdKw {
		private int id=0;
		private String kw=null;

		public IdKw(int i, String k) {
			super();
			this.id=i;
			this.kw=k;
		}
	}

}