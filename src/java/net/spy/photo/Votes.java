// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.spy.SpyObject;

/**
 * Container for votes.
 */
public class Votes extends SpyObject {

	private Map<Integer, Vote> store=new HashMap<Integer, Vote>();
	private double total=0.0d;
	private double average=0.0d;

	/**
	 * Add a vote.
	 */
	public void add(Vote v) {
		store.put(v.getUser().getId(), v);
		total += v.getVote();
		average=total / store.size();
	}

	/** 
	 * Get the average vote for this image.
	 */
	public double getAverage() {
		return(average);
	}

	/**
	 * Get the vote for the given user.
	 * 
	 * @return a Vote, or null if this user hasn't voted within this collection
	 */
	public Vote getVote(int userId) {
		return(store.get(userId));
	}

	/** 
	 * Get the number of votes contained in this votes object.
	 */
	public int getSize() {
		return(store.size());
	}

	@Override
	public String toString() {
		return("{Votes: avg=" + getAverage() + " size: " + getSize() + "}");
	}

	/** 
	 * Iterate the votes.
	 */
	public Iterator<Vote> iterator() {
		return(store.values().iterator());
	}

}
