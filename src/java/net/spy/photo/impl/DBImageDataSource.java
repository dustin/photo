// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: CE76E858-5D6C-11D9-98EB-000A957659CC

package net.spy.photo.impl;

import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;
import java.io.ObjectStreamException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.spy.SpyObject;

import net.spy.photo.PhotoImageDataSource;
import net.spy.photo.PhotoConfig;
import net.spy.photo.Keyword;
import net.spy.photo.AnnotatedRegion;
import net.spy.photo.Format;
import net.spy.photo.Persistent;
import net.spy.photo.UserFactory;
import net.spy.photo.sp.GetPhotoInfo;
import net.spy.photo.sp.GetAlbumKeywords;
import net.spy.photo.sp.GetAllRegions;
import net.spy.photo.sp.GetAllRegionKeywords;

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
	public Collection getImages() {
		Collection rv=null;
		try {
			rv=getFromDB();
		} catch(Exception e) {
			throw new RuntimeException("Can't load images from DB", e);
		}
		return(rv);
	}

	private Collection getFromDB() throws Exception {
		HashMap<Integer, ImgData> rv=new HashMap();

		// Load the images
		GetPhotoInfo db=new GetPhotoInfo(PhotoConfig.getInstance());
		ResultSet rs=db.executeQuery();
		while(rs.next()) {
			ImgData pidi=new ImgData(rs);
			rv.put(pidi.getId(), pidi);
		}
		rs.close();
		db.close();

		loadKeywords(rv);
		loadAnnotations(rv);

		// Add all of the image annotation keywords to the image keywords
		for(ImgData imgd : rv.values()) {
			for(AnnotatedRegion ar : imgd.getAnnotations()) {
				for(Keyword kw : ar.getKeywords()) {
					imgd.addKeyword(kw);
				}
			}
		}

		return(rv.values());
	}

	private void loadKeywords(Map imgs) throws Exception {
		// Load the keywords for the images
		GetAlbumKeywords gkdb=new GetAlbumKeywords(PhotoConfig.getInstance());
		ResultSet rs=gkdb.executeQuery();
		while(rs.next()) {
			int photoid=rs.getInt("album_id");
			int keywordid=rs.getInt("word_id");
			ImgData pidi=(ImgData)imgs.get(photoid);
			if(pidi == null) {
				throw new Exception("Invalid keymap entry to " + photoid);
			}
			pidi.addKeyword(Keyword.getKeyword(keywordid));
		}
		rs.close();
		gkdb.close();
	}

	private void loadAnnotations(Map imgs) throws Exception {
		Map<Integer, AnnotationData> annotations=new HashMap();

		GetAllRegions gar=new GetAllRegions(PhotoConfig.getInstance());
		ResultSet rs=gar.executeQuery();
		while(rs.next()) {
			int annotationId=rs.getInt("region_id");
			int photoid=rs.getInt("album_id");
			ImgData pidi=(ImgData)imgs.get(photoid);
			if(pidi == null) {
				throw new Exception("Invalid region map entry to " + photoid);
			}
			AnnotationData ad=new AnnotationData(rs);
			pidi.addAnnotation(ad);
			annotations.put(annotationId, ad);
		}
		rs.close();
		gar.close();

		// We now need to load all of the keywords for all of the annotations
		GetAllRegionKeywords gark=
			new GetAllRegionKeywords(PhotoConfig.getInstance());
		rs=gark.executeQuery();
		while(rs.next()) {
			int aid=rs.getInt("region_id");
			int kid=rs.getInt("word_id");

			AnnotationData ad=annotations.get(aid);
			if(ad == null) {
				throw new Exception("Invalid annotation/keymap entry to "
					+ aid);
			}
			ad.addKeyword(Keyword.getKeyword(kid));
		}
		gark.close();
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

		public void addAnnotation(AnnotatedRegion r) {
			super.addAnnotation(r);
		}

		public void addKeyword(Keyword k) {
			super.addKeyword(k);
		}

		protected Object writeReplace() throws ObjectStreamException {
			return(new PhotoImageDataImpl.SerializedForm(getId()));
		}
	}

	// AnnotationData implementation (as read from the DB)
	private static final class AnnotationData extends AnnotatedRegionImpl {

		public AnnotationData(ResultSet rs) throws Exception {
			super();
			setId(rs.getInt("region_id"));
			setX(rs.getInt("x"));
			setY(rs.getInt("y"));
			setWidth(rs.getInt("width"));
			setHeight(rs.getInt("height"));
			setTitle(rs.getString("title"));
			UserFactory uf=UserFactory.getInstance();
			setUser(uf.getUser(rs.getInt("user_id")));
			setTimestamp(rs.getTimestamp("ts"));
		}

		public void addKeyword(Keyword k) {
			super.addKeyword(k);
		}

	}

}
