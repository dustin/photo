/*
 * Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoReporting.java,v 1.6 2002/02/25 03:08:43 dustin Exp $
 */

package net.spy.photo;

import java.security.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;

public class PhotoReporting extends PhotoHelper {

	PhotoSession ps=null;

	public PhotoReporting(PhotoSession ps) throws Exception {
		super();
		this.ps=ps;
	}

	public String process(String func) throws ServletException {
		String out="";

		if(func.equals("reporting")) {
			out=tokenize("reports/index.inc", new Hashtable());
		} else {
			String tmp=conf.get("report." + func + ".top");

			// If we have the top, we probably have the rest...
			if(tmp!=null) {
				out+=tokenize(tmp, new Hashtable());
				tmp=conf.get("report." + func + ".row");

				String query=null;

				try {
					SpyDB photo=new SpyDB(new PhotoConfig());
					query=conf.get("report." + func + ".sql");
					ResultSet rs=photo.executeQuery(query);
					ResultSetMetaData md=rs.getMetaData();
					int cols=md.getColumnCount();
					int rowcount=0;

					while(rs.next()) {
						Hashtable h = new Hashtable();
						rowcount++;
						h.put("ROWNUMBER", "" + rowcount);
						for(int i=1; i<=cols; i++) {
							String val=rs.getString(i);
							if(val==null) {
								val="<i>n/a</i>";
							}
							h.put(md.getColumnName(i).toUpperCase(), val);
						}
						out+=tokenize(tmp, h);
					}

					tmp=conf.get("report."+ func +".bottom");
					out+=tokenize(tmp, new Hashtable());

					photo.close();
				} catch(Exception e) {
					log("Error processing query:\n" + query);
					e.printStackTrace();
				}

			} else {
				// Nothing specific, and nothing in the config
				throw new ServletException("Not a reporting function:  "+func);
			}
		}
		return(out);
	}

	private String tokenize(String what, Hashtable h) {
		return(PhotoUtil.tokenize(ps, what, h));
	}
}
