/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: BackupEntry.java,v 1.1 2000/11/17 10:13:06 dustin Exp $
 */

package net.spy.photo.util;

import java.sql.*;
import java.util.*;
import java.io.*;
import net.spy.*;
import net.spy.photo.*;

public class BackupEntry extends Object implements Serializable {

	protected Hashtable ht=null;

	public BackupEntry() throws Exception {
		super();
		ht=new Hashtable();
	}

	public void writeTo(OutputStream o) throws Exception {
		StringBuffer sb=new StringBuffer("<photo_album_object>\n");

		for(Enumeration e=ht.keys(); e.hasMoreElements(); ) {
			String key=(String)e.nextElement();
			String data=(String)ht.get(key);

			sb.append("\t<" + key + ">" + data + "</" + key + ">\n");
		}

		sb.append("</photo_album_object>\n");
		String tmp=sb.toString();
		o.write(tmp.getBytes());
	}
}
