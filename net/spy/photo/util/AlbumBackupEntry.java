/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: AlbumBackupEntry.java,v 1.4 2000/11/28 09:52:11 dustin Exp $
 */

package net.spy.photo.util;

import java.sql.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import net.spy.*;
import net.spy.photo.*;

public class AlbumBackupEntry extends BackupEntry {

	protected transient int id=-1;

	public AlbumBackupEntry(int id) throws Exception {
		super();
		this.id=id;
		init();
		nodeType="photo_album_object";
	}

	public AlbumBackupEntry(Node n) throws Exception {
		super(n);
		nodeType="photo_album_object";
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
