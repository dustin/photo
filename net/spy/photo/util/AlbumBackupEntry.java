/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: AlbumBackupEntry.java,v 1.12 2002/06/23 07:34:27 dustin Exp $
 */

package net.spy.photo.util;

import java.sql.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import net.spy.*;
import net.spy.db.*;
import net.spy.photo.*;

/**
 * Backup entry that represents an entry in the album table (a photo).
 */
public class AlbumBackupEntry extends BackupEntry {

	/**
	 * Get a new backup entry for the given image ID.
	 */
	public AlbumBackupEntry(int id) throws Exception {
		super();
		setNodeType("photo_album_object");
		init(id);
	}

	/**
	 * Get a new backup entry and attach it to the given node.
	 */
	public AlbumBackupEntry(Node n) throws Exception {
		super(n);
		setNodeType("photo_album_object");
	}

	/**
	 * Restore this backup entry.
	 */
	public void restore() throws Exception {

		Connection conn=null;
		try {
			SpyDB db=new SpyDB(new PhotoConfig());
			conn=db.getConn();
			conn.setAutoCommit(false);

			String query = "insert into album(keywords, descr, cat, taken,\n"
				+ " size, addedby, ts, width, height)\n"
				+ "   values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pst=conn.prepareStatement(query);
			int i=1;
			pst.setString(i++, getData("keywords"));
			pst.setString(i++, getData("descr"));
			pst.setInt(i++, getCat());
			pst.setString(i++, getData("taken"));
			pst.setString(i++, getData("size"));
			pst.setInt(i++, getUserid());
			pst.setString(i++, getData("ts"));
			pst.setString(i++, getData("width"));
			pst.setString(i++, getData("height"));

			pst.executeUpdate();

			ResultSet rs = pst.executeQuery("select currval('album_id_seq')");
			rs.next();
			int id=rs.getInt(1);
			System.out.println("ID is " + id);

			// OK, now store the image.
			long start_ts=System.currentTimeMillis();
			Element image_data=
				(Element)myData.getElementsByTagName("image_data").item(0);

			PreparedStatement ipst=conn.prepareStatement(
				"insert into image_store values(?, ?, ?)"
				);

			NodeList data_nodes=image_data.getElementsByTagName("image_row");
			for(int line=0; line<data_nodes.getLength(); line++) {

				ipst.setInt(1, id);
				ipst.setInt(2, line);

				// Find the info
				Element el=(Element)data_nodes.item(line);
				Text tdata=(Text)el.getFirstChild();
				String data=cleanData(tdata.getData());
				ipst.setString(3, data);

				ipst.executeUpdate();
			}

			PreparedStatement pst2=conn.prepareStatement(
				"insert into photo_logs "
				+ "(log_type, photo_id, wwwuser_id, remote_addr, user_agent)\n"
				+ "  values(get_log_type('Upload'), ?, ?, ?, get_agent(?))\n"
				);
			pst2.setInt(1, id);
			pst2.setInt(2, getUserid());
			pst2.setString(3, "127.0.0.1");
			pst2.setString(4, "PhotoRestore");
			pst2.setTimestamp(3,
				new java.sql.Timestamp(start_ts));
			pst2.executeUpdate();

			conn.commit();

			System.out.println("Restored!");
		} catch(Exception dbex) {
			System.err.println("Error restoring:  " + dbex);
			dbex.printStackTrace();
			if(conn!=null) {
				conn.rollback();
			}
		} finally {
			if(conn!=null) {
				conn.setAutoCommit(false);
			}
		}
	}

	private String cleanData(String data) {
		StringBuffer sb=new StringBuffer();

		String s[]=SpyUtil.split("\n ", data);
		for(int i=0; i<s.length; i++) {
			sb.append(s[i].trim());
			sb.append("\n");
		}

		return(sb.toString().trim());
	}

	private int getCat() throws Exception {
		SpyCacheDB db=new SpyCacheDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"select id from cat where name=?", 3600
			);
		pst.setString(1, getData("cat"));
		ResultSet rs=pst.executeQuery();
		if(!rs.next()) {
			throw new Error("Category " + getData("cat")
				+ " does not exist, cannot restore.");
		}
		int r=rs.getInt("id");
		return(r);
	}

	private int getUserid() throws Exception {
		SpyCacheDB db=new SpyCacheDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"select id from wwwusers where username=?", 3600
			);
		pst.setString(1, getData("addedby"));
		ResultSet rs=pst.executeQuery();
		if(!rs.next()) {
			throw new Error("User " + getData("addedby")
				+ " does not exist, cannot restore.");
		}
		int r=rs.getInt("id");
		return(r);
	}

	private String getData(String key) {
		StringBuffer rv=new StringBuffer();
		NodeList nl=myData.getElementsByTagName(key);
		if(nl.getLength()!=1) {
			throw new Error("Incorrect number of elements named " + key);
		}
		Node data=nl.item(0).getFirstChild();
		while(data!=null) {

			Text t=(Text)data;
			rv.append(t.getData());

			data=data.getNextSibling();
		}

		return(rv.toString());
	}

	private void init(int id) throws Exception {
		// Get an element for storing the data
		Element root = doc.createElement(getNodeType());

		// Get the data
		SpyDB db=new SpyDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"select keywords, descr, cat.name as cat, taken, size,\n"
				+ " wwwusers.username as addedby,\n"
				+ " ts, album.id, width, height\n"
				+ " from album, cat, wwwusers\n"
				+ " where album.id = ?\n"
				+ " and wwwusers.id=album.addedby "
				+ " and cat.id=album.cat"
			);
		pst.setInt(1, id);
		ResultSet rs=pst.executeQuery();
		ResultSetMetaData md=rs.getMetaData();
		int cols=md.getColumnCount();
		rs.next();

		// Add the column nodes from the album.
		for(int i=1; i<=cols; i++) {
			Element el=doc.createElement(md.getColumnName(i));
			el.appendChild( doc.createTextNode(rs.getString(i)) );
			root.appendChild(el);
		}


		// Go fetch the image data.
		PreparedStatement pst2=db.prepareStatement(
			"select data from image_store where id = ? order by line"
			);
		pst2.setInt(1, id);
		rs=pst2.executeQuery();

		// Add the image data.
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
