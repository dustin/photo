/*
 * Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoReporting.java,v 1.3 2001/12/29 08:50:05 dustin Exp $
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

				Connection photo=null;
				String query=null;

				try {
					photo=getDBConn();
					query=conf.get("report." + func + ".sql");
					Statement st=photo.createStatement();
					ResultSet rs=st.executeQuery(query);
					ResultSetMetaData md=rs.getMetaData();
					int cols=md.getColumnCount();
					int rowcount=0;

					while(rs.next()) {
						Hashtable h = new Hashtable();
						rowcount++;
						h.put("ROWNUMBER", "" + rowcount);
						for(int i=1; i<=cols; i++) {
							h.put(md.getColumnName(i).toUpperCase(),
								rs.getString(i));
						}
						out+=tokenize(tmp, h);
					}

					tmp=conf.get("report."+ func +".bottom");
					out+=tokenize(tmp, new Hashtable());

				} catch(Exception e) {
					log("Error processing query:\n" + query);
					e.printStackTrace();
				} finally {
					if(photo!=null) {
						freeDBConn(photo);
					}
				}

			} else {
				// Nothing specific, and nothing in the config
				throw new ServletException("Not a reporting function:  "+func);
			}
		}
		return(out);
	}

	protected String tokenize(String what, Hashtable h) {
		return(PhotoUtil.tokenize(ps, what, h));
	}
}
