/*
 * Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoAdmin.java,v 1.27 2002/03/12 09:48:24 dustin Exp $
 */

package net.spy.photo;

import java.security.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;
import net.spy.db.*;
import net.spy.cache.*;

public class PhotoAdmin extends PhotoHelper {

	private boolean debug=true;

	private PhotoSession ps=null;

	public PhotoAdmin(PhotoSession ps) throws Exception {
		super();
		this.ps=ps;
	}

	public String process(String func) throws Exception {
		String out="";

		if(func.equals("admcat")) {
			out=admShowCategories();
		} else if(func.equals("admcatedit")) {
			out=admEditCategoryForm();
		} else if(func.equals("admsavecat")) {
			out=admSaveCategory();
		} else if(func.equals("admuser")) {
			out=admShowUsers();
		} else if(func.equals("admuseredit")) {
			out=admEditUserForm();
		} else if(func.equals("admsaveuser")) {
			out=admSaveUser();
		} else if(func.equals("admnewprofileform")) {
			out=admNewProfileForm();
		} else if(func.equals("admnewprofile")) {
			out=admNewProfile();
		} else if(func.equals("admedittext")) {
			saveImageInfo();
			out=ps.doDisplay();
		} else {
			throw new ServletException("Not an Admin function:  "+func);
		}
		return(out);
	}

	// Show the user edit form
	private String admShowUsers() throws Exception {
		if(!ps.isAdmin()) {
			throw new ServletException("Not admin");
		}
		String out="";
		SpyDB photo=new SpyDB(new PhotoConfig());
		String users="";
		Hashtable h = new Hashtable();

		ResultSet rs=photo.executeQuery(
			"select id, username from wwwusers\n"
				+ " order by lower(username)"
			);
		while(rs.next()) {
			users+="\t\t<option value=\""
				+ rs.getInt("id") + "\">"
				+ rs.getString("username") + "</option>\n";
		}
		rs.close();
		h.put("USERS", users);
		out=tokenize("admin/admuser.inc", h);

		photo.close();

		return(out);
	}

	// Show a user to be edited
	private String admEditUserForm() throws Exception {
		String output="";
		if(!ps.isAdmin()) {
			throw new ServletException("Not admin");
		}
		String s_user_id=ps.request.getParameter("userid");
		int userid=Integer.parseInt(s_user_id);
		Hashtable h=new Hashtable();
		String acltable="";

		// Defaults -- Basically for a new uesr
		h.put("USERID", s_user_id);
		h.put("USER", "New User");
		h.put("REALNAME", "New User");
		h.put("EMAIL", "");
		h.put("PASS", "");
		h.put("CANADD", "");
		h.put("CANNOTADD", "checked");

		SpyDB photo=new SpyDB(new PhotoConfig());

		// First DB query is to get the user info
		PreparedStatement st=photo.prepareStatement(
			"select * from wwwusers where id=?"
			);
		st.setInt(1, userid);
		ResultSet rs=st.executeQuery();
		while(rs.next()) {
			h.put("USERID", rs.getString("id"));
			h.put("USER", rs.getString("username"));
			h.put("PASS", rs.getString("password"));
			h.put("EMAIL", rs.getString("email"));
			h.put("REALNAME", rs.getString("realname"));

			if(rs.getBoolean("canadd")) {
				h.put("CANADD", "checked");
				h.put("CANNOTADD", "");
			} else {
				h.put("CANADD", "");
				h.put("CANNOTADD", "checked");
			}
		}

		// Second DB query gets a list of the categories the user can see
		st=photo.prepareStatement(
			"select cat, canview, canadd from wwwacl where userid=?");
		st.setInt(1, userid);
		rs=st.executeQuery();
		Hashtable cats_add=new Hashtable();
		Hashtable cats_view=new Hashtable();
		while(rs.next()) {
			String cat=rs.getString("cat");
			if(rs.getBoolean("canview")) {
				cats_view.put(cat, "1");
			}
			if(rs.getBoolean("canadd")) {
				cats_add.put(cat, "1");
			}
		}
		// Third DB query gets all of the categories
		st=photo.prepareStatement(
			"select * from cat order by name");
		rs=st.executeQuery();
		while(rs.next()) {
			String catname=rs.getString("name");
			String cat_id_s=rs.getString("id");
			int cat_id=rs.getInt("id");
			if(cats_add.containsKey(cat_id_s)
				|| cats_view.containsKey(cat_id_s)) {
				acltable+="<tr>\n\t<td><font color=\"green\">"
					+ rs.getString("name")
					+ "</font></td>";
					acltable+="<td><input type=\"checkbox\" "
						+ "name=\"catacl_view\" ";
					// If they're allowed to see it, check it
					if(cats_view.containsKey(cat_id_s)) {
						acltable+= "checked=\"1\" ";
					}
					acltable+= "value=\"" + cat_id
							+ "\"></td>\n";
					acltable+="<td><input type=\"checkbox\" "
						+ "name=\"catacl_add\" ";
					// If they're allowed to add to it, check it
					if(cats_add.containsKey(cat_id_s)) {
						acltable+= "checked=\"1\" ";
					}
					acltable+= "value=\"" + cat_id
							+ "\"></td></tr>\n";
			} else {
				acltable+="<tr>\n\t<td><font color=\"red\">"
					+ rs.getString("name")
					+ "</font></td>";
				acltable+="<td><input type=\"checkbox\" "
					+ "name=\"catacl_view\" "
					+ "value=\"" + cat_id + "\"></td>\n";
				acltable+="<td><input type=\"checkbox\" "
					+ "name=\"catacl_add\" "
					+ "value=\"" + cat_id + "\"></td></tr>\n";
			}
		}
		h.put("ACLTABLE", acltable);
		output=tokenize("admin/userform.inc", h);

		photo.close();

		return(output);
	}

	private String admNewProfileForm() throws Exception {

		SpyCacheDB db=new SpyCacheDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"select id, name from cat order by name", 3600);
		ResultSet rs=pst.executeQuery();

		StringBuffer categories=new StringBuffer();
		categories.append("<categories>\n");
		while(rs.next()) {
			categories.append("\t<category id=\"");
			categories.append(rs.getInt("id"));
			categories.append("\" name=\"");
			categories.append(rs.getString("name"));
			categories.append("\"/>\n");
		}
		categories.append("</categories>\n");

		PhotoXML xml=new PhotoXML();
		xml.setTitle("Create a New Profile");
		xml.addBodyPart(ps.getGlobalMeta());
		xml.addBodyPart("<adm_new_profile_form>"
			+ categories.toString()
			+ "</adm_new_profile_form>");
		ps.sendXML(xml.toString());
		return(null);
	}

	private String admNewProfile() throws Exception {

		// Make the new Profile.
		Profile profile=new Profile();
		String name=ps.request.getParameter("name");
		if(name==null || name.length()<1) {
			throw new ServletException("name parameter not given.");
		}
		profile.setDescription(name);

		// Add the new ACLs for the user
		String acls[]=ps.request.getParameterValues("catacl_view");
		if(acls!=null) {
			for(int i=0; i<acls.length; i++) {
				int cat_id=Integer.parseInt(acls[i]);
				profile.addACLEntry(cat_id);
			}
		}

		// Save it.
		profile.save();

		PhotoXML xml=new PhotoXML();
		xml.setTitle("Saved New Profile");
		xml.addBodyPart(ps.getGlobalMeta());
		xml.addBodyPart("<adm_new_profile>"
			+ profile.getName()
			+ "</adm_new_profile>");
		ps.sendXML(xml.toString());
		return(null);
	}

	// Save a user
	private String admSaveUser() throws Exception {
		PhotoSecurity security=new PhotoSecurity();
		String user_id_s=ps.request.getParameter("userid");
		int user_id=Integer.parseInt(user_id_s);

		// Try to see if'n we can get one of these already done
		PhotoUser user = security.getUser(user_id);

		// If we didn't get one, we will this time.
		if(user==null) {
			user=new PhotoUser();
		}

		// Username
		String tmp=ps.request.getParameter("username");
		user.setUsername(tmp);

		// Password
		tmp=ps.request.getParameter("password");
		user.setPassword(tmp);

		// Real name
		tmp=ps.request.getParameter("realname");
		user.setRealname(tmp);

		// Email
		tmp=ps.request.getParameter("email");
		user.setEmail(tmp);

		// CanAdd
		tmp=ps.request.getParameter("canadd");
		if(tmp!=null) {
			boolean tmpb=false;
			if(tmp.startsWith("t")) {
				tmpb=true;
			}
			user.canAdd(tmpb);
		}

		// Add the new ACLs for the user
		String acls[]=ps.request.getParameterValues("catacl_view");
		if(acls!=null) {
			for(int i=0; i<acls.length; i++) {
				int cat_id=Integer.parseInt(acls[i]);
				user.addViewACLEntry(cat_id);
			}
		}
		// Add the new ACLs for the user
		acls=ps.request.getParameterValues("catacl_add");
		if(acls!=null) {
			for(int i=0; i<acls.length; i++) {
				int cat_id=Integer.parseInt(acls[i]);
				user.addAddACLEntry(cat_id);
			}
		}

		// Save the settings.
		user.save();

		return(admShowUsers());
	}

	/**
	 * List all categories.
	 */
	private String getCatList() throws Exception {
		StringBuffer sb=new StringBuffer();

		SpyDB db=new SpyDB(conf);
		String query="select * from cat order by name";
		ResultSet rs=db.executeQuery("select * from cat");
		while(rs.next()) {
			sb.append("    <option value=\"");
			sb.append(rs.getInt("id"));
			sb.append("\">");
			sb.append(rs.getString("name"));
			sb.append("</option>\n");
		}
		rs.close();
		db.close();
		return(sb.toString());
	}

	// Show the category edit form
	private String admShowCategories() throws Exception {
		if(!ps.isAdmin()) {
			throw new ServletException("Not admin");
		}
		Hashtable h = new Hashtable();
		h.put("CATS", getCatList());
		return(tokenize("admin/admcat.inc", h));
	}

	// Show the form to edit categories.
	private String admEditCategoryForm() throws Exception {
		String output="";
		ServletException se=null;
		if(!ps.isAdmin()) {
			throw new ServletException("Not admin");
		}

		String s_cat_id=ps.request.getParameter("cat");
		int cat_id=Integer.parseInt(s_cat_id);
		Hashtable h=new Hashtable();
		h.put("CATID", "-1");
		h.put("CATNAME", "");
		SpyDB photo=new SpyDB(new PhotoConfig());
		PreparedStatement st=photo.prepareStatement(
			"select name from cat where id=?"
			);
		st.setInt(1, cat_id);
		ResultSet rs=st.executeQuery();
		while(rs.next()) {
			h.put("CATID", s_cat_id);
			h.put("CATNAME", rs.getString("name"));
		}
		rs.close();

		// Second DB query gets a list of the categories the user can
		// see
		st=photo.prepareStatement(
			"select userid, canview, canadd from wwwacl where cat=?"
			);
		st.setInt(1, cat_id);
		rs=st.executeQuery();
		Hashtable user_add=new Hashtable();
		Hashtable user_view=new Hashtable();
		while(rs.next()) {
			String user=rs.getString("userid");
			if(rs.getBoolean("canview")) {
				user_view.put(user, "1");
			}
			if(rs.getBoolean("canadd")) {
				user_add.put(user, "1");
			}
		}
		rs.close();
		// Third DB query gets all of the users
		st=photo.prepareStatement(
			"select * from wwwusers order by username"
			);
		rs=st.executeQuery();
		StringBuffer acltable=new StringBuffer();
		while(rs.next()) {
			String username=rs.getString("username");
			String user_id_s=rs.getString("id");
			int user_id=rs.getInt("id");
			if(user_add.containsKey(user_id_s)
				|| user_view.containsKey(user_id_s)) {
				acltable.append("<tr>\n\t<td><font color=\"green\">");
				acltable.append(rs.getString("username"));
				acltable.append(" (");
				acltable.append(rs.getString("realname"));
				acltable.append(")");
				acltable.append("</font></td>");
				acltable.append("<td><input type=\"checkbox\" ");
				acltable.append("name=\"useracl_view\" ");
				// If they're allowed to see it, check it
				if(user_view.containsKey(user_id_s)) {
					acltable.append("checked=\"1\" ");
				}
				acltable.append("value=\"");
				acltable.append(user_id);
				acltable.append("\"></td>\n");
				acltable.append("<td><input type=\"checkbox\" ");
				acltable.append("name=\"useracl_add\" ");
				// If they're allowed to add to it, check it
				if(user_add.containsKey(user_id_s)) {
					acltable.append("checked=\"1\" ");
				}
				acltable.append("value=\"");
				acltable.append(user_id);
				acltable.append("\"></td></tr>\n");
			} else {
				acltable.append("<tr>\n\t<td><font color=\"red\">");
				acltable.append(rs.getString("username"));
				acltable.append(" (");
				acltable.append(rs.getString("realname"));
				acltable.append(")");
				acltable.append("</font></td>");
				acltable.append("<td><input type=\"checkbox\" ");
				acltable.append("name=\"useracl_view\" ");
				acltable.append("value=\"" + user_id + "\"></td>\n");
				acltable.append("<td><input type=\"checkbox\" ");
				acltable.append("name=\"useracl_add\" ");
				acltable.append("value=\"");
				acltable.append(user_id);
				acltable.append("\"></td></tr>\n");
			}
		}
		rs.close();
		st.close();
		h.put("ACLTABLE", acltable.toString());

		output=tokenize("admin/editcat.inc", h);
		photo.close();
		return(output);
	}

	// Show the category edit form
	private String admSaveCategory() throws Exception {
		if(!ps.isAdmin()) {
			throw new ServletException("Not admin");
		}
		Hashtable h = new Hashtable();

		String stmp=ps.request.getParameter("id");
		int cat_id=Integer.parseInt(stmp);
		String cat_name=ps.request.getParameter("name");

		SpyDB photo=new SpyDB(new PhotoConfig());
		if(cat_id>0) {
			PreparedStatement st=photo.prepareStatement(
				"update cat set name=? where id=?"
				);
			st.setString(1, ps.request.getParameter("name"));
			st.setInt(2, cat_id);
			st.executeUpdate();
		} else {
			PreparedStatement st=photo.prepareStatement(
				"insert into cat(name) values(?)");
			st.setString(1, cat_name);
			st.executeUpdate();
			// Find out what the ID of that was so that we can grant
			// ourselves access to it.
			st=photo.prepareStatement("select currval('cat_id_seq')");
			ResultSet rs=st.executeQuery();
			rs.next();
			cat_id=rs.getInt(1);
			// OK, grant ourselves access to it.
			st=photo.prepareStatement("insert into wwwacl values(?,?)");
			st.setInt(1, ps.getUser().getId());
			st.setInt(2, cat_id);
			st.executeUpdate();

			// Now, let's uncache any cached category lists we have.
			SpyCache cache=new SpyCache();
			// Get rid of anything that starts with catList_
			cache.uncacheLike("catList_");
		}

		ACLGroup acg=new ACLGroup(cat_id);

		// Add the new ACLs for the user
		String acls[]=ps.request.getParameterValues("useracl_view");
		if(acls!=null) {
			for(int i=0; i<acls.length; i++) {
				int user_id=Integer.parseInt(acls[i]);
				acg.addViewACLEntry(user_id);
			}
		}
		// Add the new ACLs for the user
		acls=ps.request.getParameterValues("useracl_add");
		if(acls!=null) {
			for(int i=0; i<acls.length; i++) {
				int user_id=Integer.parseInt(acls[i]);
				acg.addAddACLEntry(user_id);
			}
		}

		Connection conn=null;
		boolean success=false;
		try {
			conn=photo.getConn();
			conn.setAutoCommit(false);

			PreparedStatement pst=conn.prepareStatement(
				"delete from wwwacl where cat=?");
			pst.setInt(1, cat_id);
			pst.executeUpdate();
			pst.close();
			pst=null;

			pst=conn.prepareStatement(
				"insert into wwwacl(userid, cat, canview, canadd) "
					+ "values(?,?,?,?)");
			pst.setInt(2, cat_id);

			for(Enumeration e=acg.getACLEntries(); e.hasMoreElements();) {
				PhotoACLEntry acle=(PhotoACLEntry)e.nextElement();

				pst.setInt(1, acle.getUid());
				pst.setBoolean(3, acle.canView());
				pst.setBoolean(4, acle.canAdd());

				int nrows=pst.executeUpdate();
				if(nrows!=1) {
					throw new Exception("Failed to insert a row!?");
				}
			}

			conn.commit();
			success=true;
		} finally {
			if(!success) {
				if(conn!=null) {
					conn.rollback();
				}
			}
			// Turn autocommit back on.
			if(conn!=null) {
				conn.setAutoCommit(true);
			}
		}

		photo.close();
		// If we make it to the bottom, show the categories again
		return(admShowCategories());
	}

	// Save the image stuff
	private void saveImageInfo() throws Exception {
		if(!(ps.isAdmin() || ps.isSubAdmin())) {
			throw new ServletException("Must be an admin to do this.");
		}
		String keywords="", info="", taken="";
		String out="", stmp=null;
		int id=-1;
		int category=-1;

		// We need a short lifetime for whatever page this produces
		long l=new java.util.Date().getTime();
		l+=10000L;
		ps.response.setDateHeader("Expires", l);

		// Get the ID
		stmp=ps.request.getParameter("id");
		id=Integer.parseInt(stmp);

		// Get the category ID
		stmp=ps.request.getParameter("cat");
		category=Integer.parseInt(stmp);

		// Get the string data
		taken=ps.request.getParameter("taken");
		keywords=ps.request.getParameter("keywords");
		if (keywords == null) {
			debug ("keywords null, setting to blank string");
			keywords = "";
		}

		info=ps.request.getParameter("info");
		if (info == null) {
			debug ("info/desc null, setting to blank string");
			info = "";
		}

		// Make sure the user has access to this.
		PhotoSecurity security=new PhotoSecurity();
		security.checkAccess(ps.getUser().getId(), id);

		try {
			SpyDB photo=new SpyDB(new PhotoConfig());
			PreparedStatement st=photo.prepareStatement(
				"update album set cat=?, keywords=?, descr=?, taken=?\n"
				+ " where id=?"
				);
			st.setInt(1, category);
			st.setString(2, keywords);
			st.setString(3, info);
			st.setString(4, taken);
			st.setInt(5, id);
			st.executeUpdate();
			photo.close();
		} catch(Exception e) {
			log("Error updating information:  " + e);
		}
	}

	private void debug(String msg) {
		if (debug) {
			log ("PhotoAdmin debug: " + msg);
		}
	}

	// Shortcut to tokenize()
	private String tokenize(String what, Hashtable h) throws ServletException {

		String rv=PhotoUtil.tokenize(ps, what, h);

		if(rv==null) {
			throw new ServletException("Tokenizer returned null, perhaps the "
				+ " template " + what + " could not be found?");
		}
		return(rv);
	}

	// A private class to manage the acls for categories
	private class ACLGroup {
		private int cat=-1;
		private Vector acl=null;

		public ACLGroup(int cat) {
			super();
			this.cat=cat;
			acl=new Vector();
		}

		public Enumeration getACLEntries() {
			return(acl.elements());
		}

		private PhotoACLEntry addEntry(int user) {
			PhotoACLEntry rv=null;

			for(Enumeration e=getACLEntries(); rv==null
				&& e.hasMoreElements();) {

				PhotoACLEntry acl=(PhotoACLEntry)e.nextElement();

				if(acl.getUid() == user) {
					rv=acl;
				}
			}

			if(rv==null) {
				rv=new PhotoACLEntry(user, cat);
				acl.addElement(rv);
			}

			return(rv);
		} // add an entry (if it doesn't exist)

		public void addViewACLEntry(int user) {
			PhotoACLEntry aclEntry=addEntry(user);
			aclEntry.setCanView(true);
		}

		public void addAddACLEntry(int user) {
			PhotoACLEntry aclEntry=addEntry(user);
			aclEntry.setCanAdd(true);
		}

	} // Private class ACLGroup
}
