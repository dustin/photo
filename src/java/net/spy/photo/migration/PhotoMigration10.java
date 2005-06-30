// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 761739A7-5D6D-11D9-A3D2-000A957659CC

package net.spy.photo.migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.spy.db.AbstractSavable;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;
import net.spy.db.Saver;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageHelper;
import net.spy.photo.sp.migration.GetAllImgIds;
import net.spy.photo.sp.migration.UpdateFormat;
import net.spy.util.ProgressStats;

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

	protected boolean checkMigration() throws Exception {
		return((hasColumn("format", "format_id")
			|| hasColumn("album", "format_id")));
	}

	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration10.sql");
		updateFormats();
	}

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
