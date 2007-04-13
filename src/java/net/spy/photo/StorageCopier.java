package net.spy.photo;

import java.util.Collection;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import net.spy.SpyObject;
import net.spy.SpyThread;
import net.spy.util.RingBuffer;
import net.spy.xml.SAXAble;
import net.spy.xml.XMLUtils;

/**
 * Copy from one permanent storage to another.
 */
public class StorageCopier extends SpyObject
	implements ShutdownHook, SAXAble {

	private static StorageCopier instance=null;

	private CopierThread thread=null;
	private int runs=0;

	public static synchronized StorageCopier getInstance() {
		if(instance == null) {
			instance=new StorageCopier();
			Persistent.addShutdownHook(instance);
		}
		return instance;
	}

	/**
	 * True if this 
	 */
	public boolean isRunning() {
		return thread != null && thread.isAlive();
	}

	/**
	 * Start processing with the 
	 */
	public synchronized void start(PermanentStorage dest) {
		assert !isRunning() : "Already running";
		thread = new CopierThread(dest);
		thread.start();
		runs++;
	}

	/**
	 * Request a stop of the copy thread (if running).
	 */
	public synchronized void requestStop() {
		if(isRunning()) {
			thread.stopRequested=true;
		}
	}

	public void onShutdown() throws Exception {
		requestStop();
		instance=null;
	}

	public void writeXml(ContentHandler h) throws SAXException {
		XMLUtils x=XMLUtils.getInstance();
		x.startElement(h, "cachevalidation");
		x.doElement(h, "runs", String.valueOf(runs));
		x.doElement(h, "running", String.valueOf(isRunning()));
		if(thread != null) {
			x.doElement(h, "todo", String.valueOf(thread.todo));
			x.doElement(h, "done", String.valueOf(thread.done));
			x.startElement(h, "errors");
			synchronized(thread.errs) {
				for(String e : thread.errs) {
					x.doElement(h, "error", e);
				}
			}
			x.endElement(h, "errors");
		}
		x.endElement(h, "cachevalidation");
	}

	private static class CopierThread extends SpyThread {
		Collection<String> errs=new RingBuffer<String>(20);

		int todo=0;
		int done=0;

		volatile boolean stopRequested=false;

		private PermanentStorage dest=null;

		public CopierThread(PermanentStorage d) {
			super("StorageCopier");
			setDaemon(true);
			dest=d;
		}

		@Override
		public void run() {
			getLogger().info("Starting permanent storage copy.");
			try {
				doCopy(Persistent.getPermanentStorage());
			} catch(Exception e) {
				getLogger().warn("Problem copying images", e);
			}
			getLogger().info("Permanent storage copy completed.");
		}

		private void doCopy(PermanentStorage src) throws Exception {
			Collection<Integer> ids=dest.getMissingIds();
			todo=ids.size();
			PhotoImageFactory pif=PhotoImageFactory.getInstance();
			for(int id : ids) {
				try {
					PhotoImage pi=pif.getObject(id);
					dest.storeImage(pi, src.fetchImage(pi));
				} catch(Exception e) {
					getLogger().warn("Error transferring %s", id, e);
					synchronized(errs) {
						errs.add("Error on " + id + ": " + e);
					} // sync
				} //exception
			} // every id
		} // copy
	} // thread
} // StorageCopier
