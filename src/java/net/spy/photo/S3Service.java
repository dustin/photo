package net.spy.photo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.naming.InitialContext;

import net.spy.SpyObject;
import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.observation.Observation;
import net.spy.photo.observation.Observer;
import net.spy.s3.AWSAuthConnection;
import net.spy.s3.CommonPrefixEntry;
import net.spy.s3.ListBucketResponse;
import net.spy.s3.ListEntry;
import net.spy.s3.QueryStringAuthGenerator;
import net.spy.s3.Response;
import net.spy.s3.S3Object;

/**
 * Interface to S3.
 */
public class S3Service extends SpyObject implements Observer<PhotoImage> {

	private static final String DELIM = "^";

	private static S3Service instance=null;

	private String s3id=null;
	private String s3pw=null;
	private AWSAuthConnection conn=null;
	private String bucket=null;
	private ConcurrentMap<String, Integer> entries=null;
	private boolean functional=false;

	private QueryStringAuthGenerator qsag;

	// Safety net for fetches
	private int numFetches=0;

	/**
	 * Get the singleton S3Service.
	 * 
	 * @return the S3Service
	 */
	public static synchronized S3Service getInstance() {
		if(instance == null) {
			instance=new S3Service();
		}
		return instance;
	}

	/**
	 * Shut down the S3Service instance.
	 */
	public static void shutdown() {
		if(instance != null) {
			instance.getLogger().info("Shutting down S3Service");
			instance=null;
		}
	}

	private S3Service() {
		super();
	}

	public void init() throws Exception {
		getLogger().info("Initializing S3Service");
		InitialContext ctx=new InitialContext();
		s3id=(String)ctx.lookup("spy/s3id");
		s3pw=(String)ctx.lookup("spy/s3pw");
		conn=new AWSAuthConnection(s3id, s3pw);
		qsag=new QueryStringAuthGenerator(s3id, s3pw, false);
		qsag.setExpiresIn(60*1000);

		bucket=PhotoConfig.getInstance().get("S3Bucket");
		assert bucket != null;
		entries=new ConcurrentHashMap<String, Integer>();
		recursiveFetch(null);
		getLogger().info("Total:  %d", entries.size());
		functional=true;
	}

	public void recursiveFetch(String prefix) throws Exception {
		getLogger().info("Fetching with prefix ``%s''", prefix);
		boolean hasMore=true;
		String marker=null;
		while(hasMore) {
			// Keep it from fetching out of control.
			assert numFetches < 1000 : "Too many fetches in a loop";
			getLogger().info("Fetching(%d) from ``%s''", ++numFetches, marker);
			ListBucketResponse lbr=conn.listBucket(bucket,
					prefix, marker, null, DELIM, null);
			getLogger().info("Got %d entries with %d prefixes",
					lbr.entries.size(), lbr.commonPrefixEntries.size());
			for(CommonPrefixEntry cpe : lbr.commonPrefixEntries) {
				recursiveFetch(cpe.prefix);
			}
			hasMore=lbr.isTruncated;
			marker=lbr.nextMarker;
			if(lbr.isTruncated) {
				assert lbr.nextMarker != null;
			}
			for(ListEntry le : lbr.entries) {
				entries.put(le.key, (int)le.size);
			}
		}
	}

	public void observe(Observation<PhotoImage> observation) {
		getLogger().info("Informed of new image: %s", observation.getData());
	}

	/**
	 * Make an S3 key for the given image stuff.
	 */
	public String makeKey(int id, PhotoDimensions dim, Format fmt) {
		String key=null;
		if(dim == null) {
			key=id + "." + fmt.getExtension();
		} else {
			key=dim.getWidth() + "x" + dim.getHeight() + "/"
				+ id + "." + fmt.getExtension();
		}
		return key;
	}

	/**
	 * Make a URL for a given image.
	 */
	public String makeURL(int id, PhotoDimensions dim, Format fmt) {
		String key=makeKey(id, dim, fmt);
		assert entries.containsKey(key);
		return qsag.get(bucket, key, null);
	}

	public String getBucket() {
		return bucket;
	}

	/**
	 * Store the given image.
	 */
	public void storeImage(PhotoImage pid, byte[] img, PhotoDimensions dim)
		throws Exception {
		if(functional) {
			String key=makeKey(pid.getId(), dim, pid.getFormat());
			S3Object obj=new S3Object(img, null);
			obj.metadata=Collections.emptyMap();
			Response r=conn.put(bucket, key, obj,
					Collections.singletonMap("Content-Type",
							Collections.singletonList(
									pid.getFormat().getMime())));
			entries.put(key, img.length);
			getLogger().info("Stored %s in bucket %s: %s", key, bucket,
					r.connection.getResponseCode());
		} else {
			getLogger().info(
					"Store request sent to a non-functional S3Service");
		}
	}

	/**
	 * Synchronize S3.
	 */
	public void sync() throws Exception {
		getLogger().info("Synchronizing with S3");
		PhotoConfig conf=PhotoConfig.getInstance();
		Collection<PhotoDimensions> sizes = new ArrayList<PhotoDimensions>();
		sizes.add(new PhotoDimensionsImpl(conf.get("thumbnail_size")));
		sizes.add(new PhotoDimensionsImpl("800x600"));

		PhotoImageFactory pidf=PhotoImageFactory.getInstance();
		for(PhotoImage pid : pidf.getObjects()) {
			for(PhotoDimensions dim : sizes) {
				send(pid, dim);
			}
		}
		getLogger().info("Finished synchronizing with S3");
	}

	/**
	 * Send the given image at the given dimensions.
	 * 
	 * @param pid the image metadata
	 * @param dim the dimensions at which to store the image
	 */
	public void send(PhotoImage pid, PhotoDimensions dim) throws Exception {
		String key=makeKey(pid.getId(), dim, pid.getFormat());
		if(!entries.containsKey(key)) {
			getLogger().info("%s was missing", key);
			ImageServer is=Persistent.getImageServer();
			byte[] img=is.getImage(pid, dim);
			storeImage(pid, img, dim);
		}
	}

	/**
	 * Return true if S3 is known to have the given image.
	 */
	public boolean contains(int id, PhotoDimensions dim, Format fmt) {
		return entries.containsKey(makeKey(id, dim, fmt));
	}

	/**
	 * Is this S3 integration functional?
	 */
	public boolean isFunctional() {
		return functional;
	}
}
