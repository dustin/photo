package net.spy.photo;

/**
 * Interface for any shutdown hooks that may need to be registered.
 */
public interface ShutdownHook {

	/**
	 * Invoked when shutting down.
	 */
	void onShutdown() throws Exception;
}
