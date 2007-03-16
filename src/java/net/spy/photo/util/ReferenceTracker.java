package net.spy.photo.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.spy.SpyObject;
import net.spy.photo.Persistent;
import net.spy.photo.ShutdownHook;
import net.spy.stat.CounterStat;
import net.spy.stat.Stats;

/**
 * Track references to objects and report on them.
 */
public class ReferenceTracker extends SpyObject
	implements ShutdownHook, Runnable {

	private static ReferenceTracker instance=null;

	private ReferenceQueue<Object> queue=new ReferenceQueue<Object>();
	private Map<Reference<?>,Object> refs=
		new ConcurrentHashMap<Reference<?>,Object>();

	private CounterStat loops=Stats.getCounterStat("ref.pollLoops");
	private CounterStat added=Stats.getCounterStat("ref.add");
	private CounterStat removed=Stats.getCounterStat("ref.remove");
	private CounterStat addedbytes=Stats.getCounterStat("ref.addbytes");
	private CounterStat removedbytes=Stats.getCounterStat("ref.removebytes");

	public static synchronized ReferenceTracker getInstance() {
		if(instance == null) {
			instance=new ReferenceTracker();
			Persistent.addShutdownHook(instance);
			Persistent.getExecutor().scheduleAtFixedRate(
					instance, 5, 5, TimeUnit.SECONDS);
		}
		return instance;
	}

	private void add(Reference<?> r) {
		refs.put(r, r);
	}

	/**
	 * Mark an object added.
	 */
	public void addObject(Object o) {
		if(o.getClass().isArray()
				&& o.getClass().getComponentType() == Byte.TYPE) {
			byte[] b=(byte[])o;
			add(new ByteArrayReference(o, queue));
			addedbytes.increment(b.length);
		} else {
			add(new WeakReference<Object>(o, queue));
		}
		added.increment();
	}

	public void onShutdown() throws Exception {
		instance=null;
	}

	public void run() {
		loops.increment();
		Reference<? extends Object> ref=null;
		while((ref = queue.poll()) != null) {
			removed.increment();
			if(ref instanceof ByteArrayReference) {
				@SuppressWarnings("unchecked")
				ByteArrayReference bar=(ByteArrayReference)ref;
				removedbytes.increment(bar.size);
			}
			refs.remove(ref);
		}
	}

	private static class ByteArrayReference extends WeakReference<Object> {
		public int size=0;
		public ByteArrayReference(Object b, ReferenceQueue<? super Object> q) {
			super(b, q);
			assert b.getClass().isArray();
			assert b.getClass().getComponentType() == Byte.TYPE;
			size=((byte[])b).length;
		}

	}
}
