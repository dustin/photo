// Copyright (c) 1999  Dustin Sallings
//
// $Id: PhotoUser.java,v 1.2 2000/06/25 09:08:30 dustin Exp $

// This class stores an entry from the wwwusers table.

package net.spy.photo;

import net.spy.*;

public class PhotoUser extends Object {
	public Integer id=null;
	public String username=null;
	public String password=null;
	public String email=null;
	public String realname=null;
	public boolean canadd=false;
	public long cachetime=0;

	public PhotoUser() {
		super();

		this.cachetime=System.currentTimeMillis();
	}
}
