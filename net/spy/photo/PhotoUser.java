// Copyright (c) 1999  Dustin Sallings
//
// $Id: PhotoUser.java,v 1.4 2000/12/26 00:43:57 dustin Exp $

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

	public PhotoUser() {
		super();
	}

	public String toXML() {
		StringBuffer sb=new StringBuffer();

		sb.append("<photo_user>\n");

		sb.append("\t<id>\n");
		sb.append("\t\t" + id + "\n");
		sb.append("\t</id>\n");

		sb.append("\t<username>\n");
		sb.append("\t\t" + username + "\n");
		sb.append("\t</username>\n");

		sb.append("\t<email>\n");
		sb.append("\t\t" + email + "\n");
		sb.append("\t</email>\n");

		sb.append("\t<realname>\n");
		sb.append("\t\t" + realname + "\n");
		sb.append("\t</realname>\n");

		if(canadd) {
			sb.append("\t<canadd/>\n");
		}

		sb.append("</photo_user>\n");

		return(sb.toString());
	}
}
