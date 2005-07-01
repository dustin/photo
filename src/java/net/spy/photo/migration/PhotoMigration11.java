// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 76E3824A-5D6D-11D9-AEB3-000A957659CC

package net.spy.photo.migration;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import net.spy.db.Saver;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.impl.SavablePhotoImageData;
import net.spy.photo.sp.migration.GetAllImgIdsAndKws;
import net.spy.util.ProgressStats;

/**
 * Migrate to the new keywords mechanism.
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
		ArrayList<IdKw> imgs=new ArrayList<IdKw>();
		ResultSet rs=db.executeQuery();
		while(rs.next()) {
			imgs.add(new IdKw(rs.getInt("id"), rs.getString("keywords")));
		}
		rs.close();
		db.close();

		// The saver for below
		Saver saver=new Saver(PhotoConfig.getInstance());

		ProgressStats stat=new ProgressStats(imgs.size());

		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();

		// Now, flip through them and set the correct value.
		for(Iterator i=imgs.iterator(); i.hasNext(); ) {
			IdKw idkw=(IdKw)i.next();

			stat.start();

			SavablePhotoImageData savable=new SavablePhotoImageData(
				pidf.getObject(idkw.id));
			savable.setKeywords(idkw.kw);

			saver.save(savable);

			stat.stop();
			System.out.println(stat);
		}
	}

	protected boolean checkMigration() throws Exception {
		return(getRowCount("album_keywords_map") > 0);
	}

	protected void performMigration() throws Exception {
		updateKeywords();
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