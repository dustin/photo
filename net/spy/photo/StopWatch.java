/*
 * Copyright (c) 2001 Dustin Sallings <dustin@spy.net>
 *
 * $Id: StopWatch.java,v 1.1 2001/01/31 18:50:02 dustin Exp $
 */

package net.spy.photo;

public class StopWatch extends Object {

	private long start=0;
	private long stop=0;

	public StopWatch() {
		super();
	}

	public void start() {
		start=System.currentTimeMillis();
		stop=0;
	}

	public void stop() {
		stop=System.currentTimeMillis();
	}

	public String toString() {
		if(stop==0) {
			stop();
		}
		double diff=(double)(stop-start)/1000.0;
		return("" + diff + "s");
	}

	public static void main(String args[]) throws Exception {
		StopWatch sw=new StopWatch();

		sw.start();
		System.out.println("Printing something.");
		Thread.sleep(324);
		System.out.println("Took " + sw);
	}

}
