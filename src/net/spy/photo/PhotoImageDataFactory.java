// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 10856BB1-5D6D-11D9-A5A4-000A957659CC

package net.spy.photo;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import net.spy.factory.GenFactory;
import net.spy.db.Saver;
import net.spy.db.Savable;
import net.spy.db.SaveException;

import net.spy.photo.impl.DBImageDataSource;
import net.spy.photo.search.SearchIndex;

/**
 * ImageData factory.
 */
public class PhotoImageDataFactory extends GenFactory {

	private static final String CACHE_KEY="image_data";
	private static final long CACHE_TIME=86400000;

	private static PhotoImageDataFactory instance=null;

	private PhotoImageDataSource source=null;

	/**
	 * Get an instance of PhotoImageDataFactory.
	 */
	private PhotoImageDataFactory() {
		super(CACHE_KEY, CACHE_TIME);
		source=new DBImageDataSource();
	}

	/** 
	 * Get an instance of the PhotoImageDataFactory.
	 */
	public static synchronized PhotoImageDataFactory getInstance() {
		if(instance == null) {
			instance=new PhotoImageDataFactory();
		}
		return(instance);
	}

	/** 
	 * Get the instances of PhotoImageData.
	 */
	protected Collection getInstances() {
		// Get the source images
		getLogger().info("Fetching images.");
		Collection images=source.getImages();
		getLogger().info("Loaded " + images.size() + " images from "
			+ source.getClass().getName());
		// Update the index cache
		SearchIndex si=SearchIndex.getInstance();
		si.update(images);
		return(images);
	}

	/** 
	 * Handle a missing image.
	 */
	protected Object handleNullLookup(int id) {
		throw new RuntimeException("No such PhotoImage:  " + id);
	}

	/** 
	 * Get the PhotoImageData for the given ID.
	 * 
	 * @param id the given ID
	 * @return a PhotoImageData instance
	 * @throws Exception if we cannot load the data
	 */
	public PhotoImageData getData(int id) throws Exception {
		return((PhotoImageData)getObject(id));
	}

	/** 
	 * Store a savable and recache the instances.
	 */
	public void store(Savable ob) throws Exception {
		Saver s=new Saver(PhotoConfig.getInstance());
		s.save(ob);
		recache();
	}

}
