// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 10856BB1-5D6D-11D9-A5A4-000A957659CC

package net.spy.photo;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import net.spy.db.Savable;
import net.spy.db.Saver;
import net.spy.factory.GenFactory;
import net.spy.photo.impl.DBImageDataSource;
import net.spy.photo.search.SearchIndex;

/**
 * ImageData factory.
 */
public class PhotoImageDataFactory extends GenFactory<PhotoImageData> {

	private static final String CACHE_KEY="image_data";
	private static final long CACHE_TIME=86400000;

	private static final long RECACHE_DELAY=15000;

	private static PhotoImageDataFactory instance=null;

	private PhotoImageDataSource source=null;
	private Timer refreshTimer=null;
	private TimerTask nextRefresh=null;
	private long lastRefresh=0l;

	/**
	 * Get an instance of PhotoImageDataFactory.
	 */
	private PhotoImageDataFactory() {
		super(CACHE_KEY, CACHE_TIME);
		source=new DBImageDataSource();
		refreshTimer=new Timer("PhotoImageDataFactory refresher", true);
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
	protected Collection<PhotoImageData> getInstances() {
		// Get the source images
		getLogger().info("Fetching images.");
		Collection<PhotoImageData> images=source.getImages();
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
	protected PhotoImageData handleNullLookup(int id) {
		throw new NoSuchPhotoException(id);
	}

	/** 
	 * Store a savable and optionally recache the instances.
	 */
	public void store(Savable ob, boolean recache, long recacheDelay)
		throws Exception {
		Saver s=new Saver(PhotoConfig.getInstance());
		s.save(ob);
		if(recache) {
			recache(System.currentTimeMillis(), recacheDelay);
		}
	}

	/** 
	 * Store a savable and recache the instances.
	 */
	public void store(Savable ob, boolean recache) throws Exception {
		store(ob, true, RECACHE_DELAY);
	}

	/** 
	 * Store a savable and recache the instances.
	 */
	public void store(Savable ob) throws Exception {
		store(ob, true);
	}


	private synchronized void performRecache(long when) {
		if(when > lastRefresh) {
			lastRefresh=System.currentTimeMillis();
			recache();
		} else {
			getLogger().info("Avoiding unncecessary recache.");
		}
	}

	/** 
	 * Request a recache to get data as of this date.
	 */
	public synchronized void recache(final long when, long delay) {
		if(nextRefresh != null) {
			boolean canceled=nextRefresh.cancel();
			if(getLogger().isDebugEnabled()) {
				getLogger().debug(canceled?"Cancelled":"Did not cancel"
					+ " next refresh, scheduling a future one.");
			}
			nextRefresh=null;
		}
		nextRefresh=new TimerTask() {
			public void run() { performRecache(when); }
		};
		refreshTimer.schedule(nextRefresh, delay);
	}

	/** 
	 * Recache with the default delay.
	 */
	public void recache(final long when) {
		recache(when, RECACHE_DELAY);
	}

	public static class NoSuchPhotoException extends RuntimeException {
		public NoSuchPhotoException(int id) {
			super("No such photo:  " + id);
		}
	}

}
