/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogView.java,v 1.5 2002/02/24 22:50:29 dustin Exp $
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

		query = "select wwwusers.username, log.remote_addr,\n"
			  + "   user_agent.user_agent, log.cached, log.ts\n"
			  + "  from wwwusers, photo_logs log, user_agent\n"
			  + "  where log.wwwuser_id = wwwusers.id\n"
			  + "    and log.photo_id = " + photo_id + "\n"
			  + "    and user_agent.user_agent_id = log.user_agent\n"
			  + "    and log.log_type = get_log_type('ImgView')\n"
			  + "  order by log.ts\n";
		try {
			SpyDB db=new SpyDB(new PhotoConfig());
			ResultSet rs = db.executeQuery(query);

			Hashtable htmp = new Hashtable();
			htmp.put("PHOTO_ID", "" + photo_id);
			out=PhotoUtil.tokenize(photosession, "log/viewers_top.inc", htmp);

			while(rs.next()) {
				try {
					Hashtable h = new Hashtable();
					h.put("USERNAME", rs.getString(1));
					h.put("REMOTE_ADDR", rs.getString(2));
					h.put("USER_AGENT", rs.getString(3));
					h.put("CACHED", rs.getString(4));
					h.put("TS", rs.getString(5));
					out+=PhotoUtil.tokenize(photosession,
						"log/viewers_match.inc", h);
				} catch(Exception e) {
					log("Error reporting log entry for " +
						photo_id + " from " + rs.getString(5));
				}
			}

			out+=PhotoUtil.tokenize(photosession, "log/viewers_bottom.inc",
				new Hashtable());

			db.close();
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}

		return(out);
	}
}
