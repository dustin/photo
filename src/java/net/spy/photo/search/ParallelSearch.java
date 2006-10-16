// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: 7961D054-5B01-4D3C-99DD-65DB149D4D79

package net.spy.photo.search;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import net.spy.SpyObject;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.User;
import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.struts.SearchForm;

/**
 * Parallel searching interface.
 */
public class ParallelSearch extends SpyObject {

	private static AtomicReference<ParallelSearch> instanceRef=
		new AtomicReference<ParallelSearch>(null);

	private ThreadPoolExecutor pool=null;
	private PhotoDimensions optimalDims=null;

	private ParallelSearch() {
		super();
		pool=new ThreadPoolExecutor(1, 2, 120, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(50));
		optimalDims=new PhotoDimensionsImpl(
				PhotoConfig.getInstance().get(
						"optimal_image_size", "800x600"));
	}

	/**
	 * Get the singleton ParallelSearch instance.
	 */
	public static ParallelSearch getInstance() {
		ParallelSearch rv=instanceRef.get();
		if(rv == null) {
			synchronized(ParallelSearch.class) {
				rv=instanceRef.get();
				if(rv == null) {
					rv=new ParallelSearch();
					instanceRef.set(rv);
				}
			}
		}
		return rv;
	}

	/**
	 * Set the singleton ParallelSearch instance.  Also shut down an existing
	 * instance if there is one.
	 */
	public static void setInstance(ParallelSearch to) {
		ParallelSearch old=instanceRef.getAndSet(to);
		if(old != null) {
			old.pool.shutdownNow();
		}
	}

	/**
	 * Get a Future for results from a search with the given specification.
	 * 
	 * @param form the search form
	 * @param user the user on behalf of whom the search is being performed
	 * @param dims the maximum dimensions of the search results
	 * @return a future of search results
	 */
	public Future<SearchResults> futureSearch(final SearchForm form,
			final User user, final PhotoDimensions dims) {
		return pool.submit(new Callable<SearchResults>() {
			public SearchResults call() throws Exception {
				return Search.getInstance().performSearch(form, user, dims);
			}});
	}

	/**
	 * Get a Future for results from a search with the given specification.
	 * 
	 * @param form the search form
	 * @param user the user on behalf of whom the search is being performed
	 * @return a future of search results
	 */
	public Future<SearchResults> futureSearch(SearchForm form, User user) {
		return futureSearch(form, user, optimalDims);
	}

	/**
	 * Perform a search in the pool and wait for results.
	 * 
	 * @param form the search form
	 * @param user the user on behalf of wom the search will be executed
	 * @param dims the requested dimensions
	 * @return the search results
	 * @throws InterruptedException 
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public SearchResults performSearch(SearchForm form, User user,
			PhotoDimensions dims)
		throws InterruptedException, ExecutionException, TimeoutException {
		return futureSearch(form, user, dims).get(10, TimeUnit.SECONDS);
	}

	/**
	 * Perform a search in the pool and wait for results.
	 * 
	 * @param form the search form
	 * @param user the user on behalf of wom the search will be executed
	 * @return the search results
	 * @throws InterruptedException 
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public SearchResults performSearch(SearchForm form, User user)
		throws InterruptedException, ExecutionException, TimeoutException {
		return performSearch(form, user, optimalDims);
	}
}
