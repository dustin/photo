/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: AlbumBackupEntry.java,v 1.5 2000/11/29 07:02:19 dustin Exp $
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
		nodeType="photo_album_object";
		init();
	}

	public AlbumBackupEntry(Node n) throws Exception {
		super(n);
		nodeType="photo_album_object";
	}

	protected void init() throws Exception {
		// Get an element for storing the data
		Element root = doc.createElement(nodeType);

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
			Element el=doc.createElement(md.getColumnName(i));
			el.appendChild( doc.createTextNode(rs.getString(i)) );
			root.appendChild(el);
		}


		PreparedStatement pst2=db.prepareStatement(
			"select data from image_store where id = ? order by line"
			);
		pst2.setInt(1, id);
		rs=pst2.executeQuery();

		Element image_data=doc.createElement("image_data");
		while(rs.next()) {
			Element el=doc.createElement("image_row");
			el.appendChild( doc.createTextNode(rs.getString("data")) );
			image_data.appendChild(el);
		}

		root.appendChild(image_data);
		doc.appendChild(root);
	}
}
