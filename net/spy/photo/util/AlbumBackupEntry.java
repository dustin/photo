/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: AlbumBackupEntry.java,v 1.2 2000/11/17 10:13:06 dustin Exp $
 */

package net.spy.photo.util;

import java.sql.*;
import java.io.Serializable;
import net.spy.*;
import net.spy.photo.*;

public class AlbumBackupEntry extends BackupEntry {

	protected transient int id=-1;

	public AlbumBackupEntry(int id) throws Exception {
		super();
		this.id=id;
		init();
	}

	protected void init() throws Exception {
		// Get the data
		SpyDB db=new SpyDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"select * from album where id = ?"
			);
		pst.setInt(1, id);
		ResultSet rs=pst.executeQuery();
		ResultSetMetaData md=rs.getMetaData();
		int cols=md.getColumnCount();
		rs.next();

		for(int i=1; i<=cols; i++) {
			ht.put(md.getColumnName(i), rs.getString(i));
		}

		PreparedStatement pst2=db.prepareStatement(
			"select data from image_store where id = ? order by line"
			);
		pst2.setInt(1, id);
		rs=pst2.executeQuery();

		StringBuffer sb=new StringBuffer("\n");
		while(rs.next()) {
			sb.append(rs.getString("data"));
		}

		ht.put("image_data", sb.toString());
	}
}
