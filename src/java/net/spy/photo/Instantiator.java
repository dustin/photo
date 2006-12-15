// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import net.spy.SpyObject;

/**
 * Instantiate instances of a class by configuration name.
 * 
 * @param <C> The class that should be instantiated.
 */
public class Instantiator<C> extends SpyObject {

	private C inst=null;

	/**
	 * Create an instantiator for the given configuration name and default.
	 * 
	 * @param name the name of the parameter defining this class
	 * @param def the name of the default implementation
	 * @throws Exception if the instance can't be instantiated
	 */
	public Instantiator(String name, String def) throws Exception {
		super();
		inst=createInstance(name, def);
	}

	private C createInstance(String name, String def) throws Exception {
		PhotoConfig conf=PhotoConfig.getInstance();
		String className=conf.get(name, def);
		getLogger().info("Initializing %s", className);
		@SuppressWarnings("unchecked")
		Class<C> c=(Class<C>) Class.forName(className);
		C rv=c.newInstance();
		getLogger().info("Initialization complete.");
		return rv;
	}

	/**
	 * Get the instantiated instance.
	 */
	public C getInstance() throws Exception {
		return inst;
	}

}
