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
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageHelper;

import net.spy.photo.sp.migration.GetAllImgIds;
import net.spy.photo.sp.migration.UpdateFormat;

/**
 * Add the format table.
 */
public class PhotoMigration10 extends PhotoMigration {

	/**
	 * Get an instance of PhotoMigration10.
	 */
	public PhotoMigration10() {
		super();
	}

	private void updateFormats() throws Exception {
		// Get the IDs
		GetAllImgIds db=new GetAllImgIds(PhotoConfig.getInstance());
		ArrayList ids=new ArrayList();
		ResultSet rs=db.executeQuery();
		while(rs.next()) {
			ids.add(new Integer(rs.getInt("id")));
		}
		rs.close();
		db.close();

		// The saver for below
		Saver saver=new Saver(PhotoConfig.getInstance());

		ProgressStats stat=new ProgressStats(ids.size());

		// Now, flip through them and set the correct value.
		for(Iterator i=ids.iterator(); i.hasNext(); ) {
			Integer id=(Integer)i.next();

			stat.start();
			PhotoImageHelper pih=new PhotoImageHelper(id.intValue());
			PhotoImage image=pih.getImage();

			saver.save(new FormatUpdate(id.intValue(),
				image.getFormat().getId()));
			stat.stop();
			System.out.println(stat);
		}
	}

	/** 
	 * Perform the migration.
	 */
	public void migrate() throws Exception {
		if(hasColumn("format", "format_id")
			|| hasColumn("album", "format_id")) {
			System.err.println("Looks like you've already run this kit.");
		} else {
			runSqlScript("net/spy/photo/migration/migration10.sql");
			updateFormats();
		}
	}

	/** 
	 * Run the 9th migration script.
	 */
	public static void main(String args[]) throws Exception {
		PhotoMigration10 mig=new PhotoMigration10();
		mig.migrate();
	}

	private static class FormatUpdate extends AbstractSavable {
		private int photoId=0;
		private int formatId=0;

		public FormatUpdate(int id, int format) {
			super();
			this.photoId=id;
			this.formatId=format;
		}

		public void save(Connection conn, SaveContext context) throws
			SaveException, SQLException {

			UpdateFormat uf=new UpdateFormat(conn);
			uf.setId(photoId);
			uf.setFormatId(formatId);
			uf.executeUpdate();
			setSaved();
		}
	}

}
