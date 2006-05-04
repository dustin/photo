// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// arch-tag: CCEA9C68-5D6C-11D9-A739-000A957659CC

package net.spy.photo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.spy.SpyObject;

/**
 * An object that will be cursor through other objects.
 */
public class Cursor<T> extends SpyObject
	implements Cloneable, Serializable, Iterable<T> {

    private int pageSize=10;
    private List<T> results=null;
    private int pageStart=0;
    private boolean overflowed=false;

    protected Cursor() {
        this(0);
    }

    /**
     * Get an instance of Cursor to hold the given number of elements.
     */
    public Cursor(int size) {
        super();
        results=new ArrayList<T>(size);
    }

    /**
     * Get an instance of Cursor over the given objects.
     */
    public Cursor(Collection<T> c) {
        super();
        results=new ArrayList<T>(c);
    }

	/**
	 * Clone this result set.
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
     * Add an object to these results.
     */
    public boolean add(T o) {
    	return results.add(o);
    }

    /** 
     * Get the number of objects.
     */
    public int getSize() {
        return(results.size());
    }

    /**
     * Get the object at the given offset in the search results.
     * 
     * @param which the offset object to get
     * @return the object at the given index
     */
    public T get(int which) {
    	return(results.get(which));
    }

    /** 
     * Get the full list of objects (mutable).
     * 
     * @return an unmodifiable list of results.
     */
    public List<T> getAllObjects() {
        return(results);
    }

    /**
     * Get an iterator over the current page of objects in this cursor.
     */
    public Iterator<T> iterator() {
    	return getPage().iterator();
    }

    /** 
     * Get the current page of objects.
     * 
     * @return a sublist of just the entries for the current page.
     */
    public List<T> getPage() {
        int pageEnd=getPageEnd();
        getLogger().debug("Getting from %d to %d", pageStart, pageEnd);
        List<T> rv=results.subList(pageStart, pageEnd);
        return(rv);
    }

    /** 
     * Set the page start to the given value.
     * 
     * @param to the given value
     */
    public void setPageStart(int to) {
        pageStart=to;
    }

    /** 
     * Get the current starting page.
     */
    public int getPageStart() {
        return(pageStart);
    }

    /** 
     * Set the current page number.
     */
    public void setPageNumber(int pageNumber) {
        pageStart = pageSize * pageNumber;
    }

    /** 
     * Get the current page number.
     */
    public int getPageNumber() {
        int rv=pageStart / pageSize;
        return(rv);
    }

    // get the end of the current page
    private int getPageEnd() {
        int rv=getNextPageStart();
        if(rv == -1) {
            rv = results.size();
        }
        return(rv);
    }

    /** 
     * Get the starting position for the next page.
     * 
     * @return the starting value for the next page...-1 if there are no
     * more pages.
     */
    public int getNextPageStart() {
        int rv=-1;
        
        // If the current page does not exceed the size, we have another page
        // test:
        //  size() == 11
        //  pageSize == 10
        //    pageStart == 0 (pageStart + pageSize == 10), next page is 11
        //    pageStart == 10 (pageStart + pageSize == 20), no next page
        int tmp=pageStart + pageSize;
        if( tmp <= results.size()) {
            rv = tmp;
        }
        
        return(rv);
    }
    
    /** 
     * Get the number of pages of results.
     */
    public int getNumPages() {
        int rv=results.size() / pageSize;
        if(results.size() % pageSize != 0) {
            rv++;
        }
        return(rv);
    }
    
    /** 
     * Find out if there are more pages of results.
     * 
     * @return true if there are more pages
     */
    public boolean hasMorePages() {
        return(getNextPageStart() != -1);
    }
    
    /** 
     * Get the number of results remaining.
     */
    public int getNumRemaining() {
        int rv=0; 
        int tmp = getNextPageStart();
        if(tmp > 0) {
            rv = results.size() - getNextPageStart();
        }
        getLogger().debug("Number remaining is %d", rv);
        return(rv);
    }

    /** 
     * Get the page size.
     */
    public int getPageSize() {
        return(pageSize);
    }

    /** 
     * Set the page size.
     */
    public void setPageSize(int to) {
        this.pageSize=to;
    }

	/** 
	 * Get the size of the next page (if applicable).
	 */
	public int getNextPageSize() {
		assert hasMorePages() : "No more pages.";
		int remaining=getNumRemaining();
		return remaining > pageSize ? pageSize : remaining;
	}

    /** 
     * Find out if this search has overflowed (returned too many results).
     *
     * I don't like having boolean gets use the word ``get'' but jstl does.
     * 
     * @return true if the results represent an overflowed search
     */
    public boolean getOverflowed() {
        return(overflowed);
    }

    /** 
     * Mark this result set as overflowed.
     */
    public void setOverflowed(boolean to) {
        this.overflowed=to;
    }

}
