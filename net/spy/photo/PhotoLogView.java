/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogView.java,v 1.7 2002/06/12 07:01:06 dustin Exp $
 */

package net.spy.photo;

import java.io.*;
import java.sql.*;
import java.util.*;

import net.spy.*;

// The class
public class PhotoLogView extends PhotoHelper
{ 
	PhotoSession photosession;

	public PhotoLogView(PhotoSession p) throws Exception {
		super();
		photosession=p;
	}

	public String getViewersOf(int photo_id) throws Exception {
		Statement st;
		String query, out="";

		query = "select log.log_id as id, wwwusers.username, log.remote_addr,\n"
			  + "   user_agent.user_agent, log.extra_info as img_size, log.ts\n"
			  + "  from wwwusers, photo_logs log, user_agent\n"
			  + "  where log.wwwuser_id = wwwusers.id\n"
			  + "    and log.photo_id = " + photo_id + "\n"
			  + "    and user_agent.user_agent_id = log.user_agent\n"
			  + "    and log.log_type = get_log_type('ImgView')\n"
			  + "  order by log.ts desc\n"
			  + "  limit 100\n";
		Hashtable htmp = new Hashtable();

		SpyDB db=new SpyDB(new PhotoConfig());
		ResultSet rs=db.executeQuery(
			"select count(*) from photo_logs\n"
			+ " where photo_id = " + photo_id + "\n"
			+ " and log_type = get_log_type('ImgView')");
		if(!rs.next()) {
			throw new Exception("No results?");
		}
		int views=rs.getInt(1);
		htmp.put("TOTAL_VIEWS", "" + views);
		if(views>100) {
			htmp.put("DISPLAYED_VIEWS", "100");
		} else {
			htmp.put("DISPLAYED_VIEWS", "" + views);
		}

		rs.close();

		rs = db.executeQuery(query);

		htmp.put("PHOTO_ID", "" + photo_id);
		out=PhotoUtil.tokenize(photosession, "log/viewers_top.inc", htmp);

		while(rs.next()) {
			try {
				Hashtable h = new Hashtable();
				h.put("USERNAME", rs.getString("username"));
				h.put("REMOTE_ADDR", rs.getString("remote_addr"));
				h.put("USER_AGENT", ns(rs.getString("user_agent")));
				h.put("IMG_SIZE", ns(rs.getString("img_size")));
				h.put("TS", rs.getString("ts"));
				out+=PhotoUtil.tokenize(photosession,
					"log/viewers_match.inc", h);
			} catch(Exception e) {
				log("Error reporting log entry for " + rs.getInt("id"));
				e.printStackTrace();
			}
		}

		out+=PhotoUtil.tokenize(photosession, "log/viewers_bottom.inc",
			new Hashtable());

		db.close();

		return(out);
	}

	// Nullable string
	private String ns(String col) {
		String rv=col;
		if(rv==null) {
			rv="<i>unknown</i>";
		}
		return(rv);
	}
}
