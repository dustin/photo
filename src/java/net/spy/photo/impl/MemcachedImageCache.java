package net.spy.photo.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import net.spy.SpyObject;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.SerializingTranscoder;
import net.spy.photo.ImageCache;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoException;
import net.spy.photo.ShutdownHook;

/**
 * ImageCache implementation that uses Memcached.
 */
public class MemcachedImageCache extends SpyObject
	implements ImageCache, ShutdownHook {

	private MemcachedClient memcached=null;
	private int expirationTime=3600;
	private String prefix=null;

	public MemcachedImageCache() {
		super();
		try {
			prefix=Persistent.getContextPath();
			PhotoConfig conf=PhotoConfig.getInstance();
			int gzipsize=conf.getInt("memcached.transcoder.gzipsize",
					Integer.MAX_VALUE);
			expirationTime=conf.getInt("memcached.cachetime", 3600);
			SerializingTranscoder transcoder = new SerializingTranscoder();
			transcoder.setCompressionThreshold(gzipsize);
			getLogger().info("Compression threshold:  %d", gzipsize);

			ArrayList<InetSocketAddress> addrs=
				new ArrayList<InetSocketAddress>();

			for(String hoststuff : conf.get("memcached.servers").split(" ")) {
				String[] parts=hoststuff.split(":");
				assert parts.length == 2 : "Invalid memcached.servers: "
					+ conf.get("memcached.servers");
				addrs.add(new InetSocketAddress(parts[0],
						Integer.parseInt(parts[1])));
			}

			memcached=new MemcachedClient(
					addrs.toArray(new InetSocketAddress[0]));
			memcached.setTranscoder(transcoder);

			Persistent.addShutdownHook(this);
		} catch(Exception e) {
			getLogger().info("Error initializing memcached cache", e);
		}
	}

	public byte[] getImage(String key) throws PhotoException {
		byte[] rv=null;
		if(memcached != null) {
			rv=(byte[])memcached.get(prefix + "/" + key);
			getLogger().debug("Got %s from memcached (%d bytes)", key,
					rv == null ? 0 : rv.length);
		}
		return rv;
	}

	public void putImage(String key, byte[] image) throws PhotoException {
		if(memcached != null) {
			String k=prefix + "/" + key;
			memcached.add(k, expirationTime, image);
			getLogger().debug("Memcached stored %s", k);
		} else {
			getLogger().debug("No memcached, can't store %s", key);
		}
	}

	public void onShutdown() {
		if(memcached != null) {
			memcached.shutdown();
		}
	}

}
