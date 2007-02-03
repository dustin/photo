// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import java.io.ObjectStreamException;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.spy.SpyObject;
import net.spy.photo.AnnotatedRegion;
import net.spy.photo.Format;
import net.spy.photo.Keyword;
import net.spy.photo.KeywordFactory;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoImageDataSource;
import net.spy.photo.PhotoSecurity;
import net.spy.photo.PlaceFactory;
import net.spy.photo.UserFactory;
import net.spy.photo.Vote;
import net.spy.photo.sp.GetAlbumKeywords;
import net.spy.photo.sp.GetAllRegionKeywords;
import net.spy.photo.sp.GetAllRegions;
import net.spy.photo.sp.GetAllVotes;
import net.spy.photo.sp.GetPhotoInfo;
import net.spy.photo.sp.SelectVariants;
import net.spy.util.CloseUtil;

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
	@SuppressWarnings("unchecked")
	public Collection<PhotoImageData> getImages() {
		Collection<PhotoImageData> rv = null;
		try {
			// This is an annoying cast and requires the @Suppress above.
			rv = (Collection<PhotoImageData>) getFromDB();
		} catch(Exception e) {
			throw new RuntimeException("Can't load images from DB", e);
		}
		return (rv);
	}

	private Collection<? extends PhotoImageData> getFromDB() throws Exception {
		HashMap<Integer, ImgData> rv = new HashMap<Integer, ImgData>();

		// Load the images
		GetPhotoInfo db = new GetPhotoInfo(PhotoConfig.getInstance());
		ResultSet rs = db.executeQuery();
		while(rs.next()) {
			ImgData pidi = new ImgData(rs);
			rv.put(pidi.getId(), pidi);
		}
		rs.close();
		db.close();

		loadKeywords(rv);
		loadAnnotations(rv);
		loadVotes(rv);
		loadVariants(rv);

		// Add all of the image annotation keywords to the image keywords
		for(PhotoImageData imgd : rv.values()) {
			for(AnnotatedRegion ar : imgd.getAnnotations()) {
				for(Keyword kw : ar.getKeywords()) {
					((ImgData)imgd).addKeyword(kw);
				}
			}
		}

		return (rv.values());
	}

	private void loadVariants(HashMap<Integer, ImgData> rv)
		throws Exception {
		SelectVariants sv=new SelectVariants(PhotoConfig.getInstance());
		try {
			ResultSet rs=sv.executeQuery();
			while(rs.next()) {
				rv.get(rs.getInt("original_id"))
					.addVariant(rv.get(rs.getInt("variant_id")));
			}
			rs.close();
		} finally {
			CloseUtil.close(sv);
		}
	}

	private void loadVotes(HashMap<Integer, ImgData> rv)
		throws Exception {
		PhotoSecurity security=Persistent.getSecurity();
		GetAllVotes db=new GetAllVotes(PhotoConfig.getInstance());
		ResultSet rs=db.executeQuery();

		while(rs.next()) {
			int photoId=rs.getInt("photo_id");
			Vote v=new Vote(security, rs);
			rv.get(photoId).addVote(v);
		}

		rs.close();
		db.close();
	}

	private void loadKeywords(Map<Integer, ImgData> imgs) throws Exception {
		// Load the keywords for the images
		KeywordFactory kf=KeywordFactory.getInstance();
		GetAlbumKeywords gkdb = new GetAlbumKeywords(PhotoConfig.getInstance());
		ResultSet rs = gkdb.executeQuery();
		while(rs.next()) {
			int photoid = rs.getInt("album_id");
			int keywordid = rs.getInt("word_id");
			ImgData pidi = imgs.get(photoid);
			if(pidi == null) {
				throw new Exception("Invalid keymap entry to " + photoid);
			}
			pidi.addKeyword(kf.getObject(keywordid));
		}
		rs.close();
		gkdb.close();
	}

	private void loadAnnotations(Map<Integer, ImgData> imgs) throws Exception {
		Map<Integer, AnnotationData> annotations =
				new HashMap<Integer, AnnotationData>();

		GetAllRegions gar = new GetAllRegions(PhotoConfig.getInstance());
		ResultSet rs = gar.executeQuery();
		while(rs.next()) {
			int annotationId = rs.getInt("region_id");
			int photoid = rs.getInt("album_id");
			ImgData pidi = imgs.get(photoid);
			if(pidi == null) {
				throw new Exception("Invalid region map entry to " + photoid);
			}
			AnnotationData ad = new AnnotationData(rs);
			pidi.addAnnotation(ad);
			annotations.put(annotationId, ad);
		}
		rs.close();
		gar.close();

		// We now need to load all of the keywords for all of the annotations
		KeywordFactory kf=KeywordFactory.getInstance();
		GetAllRegionKeywords gark = new GetAllRegionKeywords(PhotoConfig
			.getInstance());
		rs = gark.executeQuery();
		while(rs.next()) {
			int aid = rs.getInt("region_id");
			int kid = rs.getInt("word_id");

			AnnotationData ad = annotations.get(aid);
			if(ad == null) {
				throw new Exception("Invalid annotation/keymap entry to " + aid);
			}
			ad.addKeyword(kf.getObject(kid));
		}
		gark.close();
	}

	private static final class ImgData extends PhotoImageDataImpl {
		private Collection<PhotoImageData> variants=null;
		public ImgData(ResultSet rs) throws Exception {
			super();
			setId(rs.getInt("id"));
			setCatId(rs.getInt("catid"));
			setSize(rs.getInt("size"));
			int w = rs.getInt("width");
			if(rs.wasNull()) {
				w = -1;
			}
			int h = rs.getInt("height");
			if(rs.wasNull()) {
				h = -1;
			}
			setDescr(rs.getString("descr"));
			setCatName(rs.getString("catname"));
			setTimestamp(rs.getTimestamp("ts"));
			setTaken(rs.getDate("taken"));
			setMd5(rs.getString("md5"));
			int p=rs.getInt("place_id");
			if(!rs.wasNull()) {
				setPlace(PlaceFactory.getInstance().getObject(p));
			}

			// Look up the format
			setFormat(Format.getFormat(rs.getInt("format_id")));

			// Look up the user
			setAddedBy(Persistent.getSecurity().getUser(rs.getInt("addedby")));

			// Add variant id storage
			variants=new LinkedList<PhotoImageData>();

			if(w >= 0 && h >= 0) {
				setDimensions(new PhotoDimensionsImpl(w, h));
			}

			calculateThumbnail();
		}

		@Override
		public void addAnnotation(AnnotatedRegion r) {
			super.addAnnotation(r);
		}

		@Override
		public void addKeyword(Keyword keyword) {
			super.addKeyword(keyword);
		}

		public void addVariant(PhotoImageData which) {
			assert which != null : "Attempted to add a null variant.";
			variants.add(which);
		}

		@Override
		public Collection<PhotoImageData> getVariants() {
			return variants;
		}

		@Override
		protected Object writeReplace() throws ObjectStreamException {
			return (new PhotoImageDataImpl.SerializedForm(getId()));
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
			UserFactory uf = UserFactory.getInstance();
			setUser(uf.getUser(rs.getInt("user_id")));
			setTimestamp(rs.getTimestamp("ts"));
		}

		@Override
		public void addKeyword(Keyword k) {
			super.addKeyword(k);
		}

	}

}
