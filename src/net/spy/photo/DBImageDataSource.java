// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.Collection;
import java.util.HashMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.spy.SpyObject;

import net.spy.photo.sp.GetPhotoInfo;
import net.spy.photo.sp.GetAlbumKeywords;

/**
 * A PhotoImageDataSource implementation that gets images from the DB.
 */
public class DBImageDataSource extends SpyObject
	implements PhotoImageDataSource {

	/**
	 * Get an instance of DBImageDataSource.
	 */
	public DBImageDataSource() {
		super();
	}

	/** 
	 * Get the images.
	 */
	public Collection getImages() throws Exception {
		HashMap rv=new HashMap();

		// Load the images
		GetPhotoInfo db=new GetPhotoInfo(PhotoConfig.getInstance());
		ResultSet rs=db.executeQuery();
		while(rs.next()) {
			ImgData pidi=new ImgData(rs);
			rv.put(new Integer(pidi.getId()), pidi);
		}
		rs.close();
		db.close();

		// Load the keywords for the images
		GetAlbumKeywords gkdb=new GetAlbumKeywords(PhotoConfig.getInstance());
		rs=gkdb.executeQuery();
		while(rs.next()) {
			int photoid=rs.getInt("album_id");
			int keywordid=rs.getInt("word_id");
			ImgData pidi=(ImgData)rv.get(new Integer(photoid));
			if(pidi == null) {
				throw new Exception("Invalid keymap entry to " + photoid);
			}
			pidi.addKeyword(Keyword.getKeyword(keywordid));
		}
		rs.close();
		gkdb.close();

		return(rv.values());
	}

	private static final class ImgData extends PhotoImageDataImpl {
		public ImgData(ResultSet rs) throws Exception {
			super();
			setId(rs.getInt("id"));
			setCatId(rs.getInt("catid"));
			setSize(rs.getInt("size"));
			int w=rs.getInt("width");
			if(rs.wasNull()) {
				w=-1;
			}
			int h=rs.getInt("height");
			if(rs.wasNull()) {
				h=-1;
			}
			setDescr(rs.getString("descr"));
			setCatName(rs.getString("catname"));
			setTimestamp(rs.getTimestamp("ts"));
			setTaken(rs.getDate("taken"));

			// Look up the format
			setFormat(Format.getFormat(rs.getInt("format_id")));

			// Look up the user
			setAddedBy(Persistent.getSecurity().getUser(rs.getInt("addedby")));

			if(w>=0 && h>=0) {
				setDimensions(new PhotoDimensionsImpl(w, h));
			}

			calculateThumbnail();
		}

		public void addKeyword(Keyword k) {
			super.addKeyword(k);
		}
	}

}
