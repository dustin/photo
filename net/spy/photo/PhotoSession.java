/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoSession.java,v 1.8 2000/06/26 06:42:31 dustin Exp $
 */

package net.spy.photo;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.*;
import sun.misc.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.oreilly.servlet.*;

import net.spy.*;
import net.spy.util.*;

// The class
public class PhotoSession extends Object
{ 
	// This kinda stuff is only persistent for a single connection.
	protected Integer remote_uid=null;
	protected String remote_user=null, self_uri=null;
	protected RHash rhash=null;
	protected MultipartRequest multi=null;
	protected SpyLog logger=null;
	protected PhotoSecurity security = null;
	protected HttpSession session=null;

	protected PhotoStorerThread storer_thread = null;

	protected PhotoServlet photo_servlet = null;

	protected Hashtable groups=null;

	HttpServletRequest request=null;
	HttpServletResponse response=null;

	public PhotoSession(PhotoServlet p,
		HttpServletRequest request,
		HttpServletResponse response) {

		photo_servlet=p;
		this.request=request;
		this.response=response;
		this.session=request.getSession(false);

		logger=p.logger;

		self_uri = p.self_uri;
		rhash=p.rhash;
		security=p.security;
	}

	protected void log(String whu) {
		photo_servlet.log(whu);
	}

	// process a request
	public void process() throws ServletException, IOException {

		String func, type;

		type = request.getContentType();
		if(type != null && type.startsWith("multipart/form-data")) {
			multi = new MultipartRequest(request, "/tmp");
		} else {
			multi = null;
		}

		// Set the self_uri
		self_uri = request.getRequestURI();

		getCreds();

		// Figure out what they want, default to index.
		if(multi==null) {
			func=request.getParameter("func");
		} else {
			func=multi.getParameter("func");
		}
		log("func is " + func);
		if(func == null) {
			doIndex();
		} else if(func.equalsIgnoreCase("search")) {
			doFind();
		} else if(func.equalsIgnoreCase("nextresults")) {
			displaySearchResults();
		} else if(func.equalsIgnoreCase("addimage")) {
			doAddPhoto();
		} else if(func.equalsIgnoreCase("index")) {
			doIndex();
		} else if(func.equalsIgnoreCase("findform")) {
			doFindForm();
		} else if(func.equalsIgnoreCase("addform")) {
			doAddForm();
		} else if(func.equalsIgnoreCase("catview")) {
			doCatView();
		} else if(func.equalsIgnoreCase("setstyle")) {
			doSetStyle();
		} else if(func.equalsIgnoreCase("styleform")) {
			doStyleForm();
		} else if(func.equalsIgnoreCase("getstylesheet")) {
			doGetStylesheet();
		} else if(func.equalsIgnoreCase("display")) {
			doDisplay();
		} else if(func.equalsIgnoreCase("logview")) {
			doLogView();
		} else if(func.equalsIgnoreCase("getimage")) {
			showImage();
		} else if(func.equalsIgnoreCase("credform")) {
			showCredForm();
		} else if(func.equalsIgnoreCase("savesearch")) {
			saveSearch();
		} else if(func.equalsIgnoreCase("edittext")) {
			saveImageInfo();
			doDisplay();
		} else if(func.equalsIgnoreCase("setcred")) {
			setCreds();
			doIndex();
		} else if(func.equalsIgnoreCase("setadmin")) {
			setAdmin();
			doIndex();
		} else if(func.equalsIgnoreCase("unsetadmin")) {
			unsetAdmin();
			doIndex();
		} else if(func.equalsIgnoreCase("admcat")) {
			admShowCategories();
		} else if(func.equalsIgnoreCase("admcatedit")) {
			admEditCategoryForm();
		} else if(func.equalsIgnoreCase("admsavecat")) {
			admSaveCategory();
		} else if(func.equalsIgnoreCase("admuser")) {
			admShowUsers();
		} else if(func.equalsIgnoreCase("admuseredit")) {
			admEditUserForm();
		} else if(func.equalsIgnoreCase("admsaveuser")) {
			admSaveUser();
		} else {
			throw new ServletException("No known function.");
		}
	}

	protected void saveSearch() throws ServletException {
		PhotoSearch ps = new PhotoSearch();
		PhotoUser user = security.getUser(remote_user);
		String output="";

		try {
			ps.saveSearch(request, user);
			output=tokenize("addsearch_success.inc", new Hashtable());
		} catch(Exception e) {
			Hashtable h = new Hashtable();
			h.put("MESSAGE", e.getMessage());
			output=tokenize("addsearch_fail.inc", h);
		}
		send_response(output);
	}

	protected void getCreds() throws ServletException {
		getUid();
		log("Authenticated as " + remote_user);
	}

	// Show the user edit form
	protected void admShowUsers() throws ServletException {
		ServletException se=null;
		if(!isAdmin()) {
			throw new ServletException("Not admin");
		}
		Hashtable h = new Hashtable();
		Connection photo=null;
		try {
			photo=getDBConn();
			String users="";

			Statement st=photo.createStatement();
			ResultSet rs=st.executeQuery(
				"select id, username from wwwusers"
				);
			while(rs.next()) {
				users+="\t\t<option value=\""
					+ rs.getInt("id") + "\">"
					+ rs.getString("username") + "\n";
			}
			h.put("USERS", users);

			String output=tokenize("admin/admuser.inc", h);
			send_response(output);
		} catch(Exception e) {
			se=new ServletException("Error showing users:  " + e);
		} finally {
			freeDBConn(photo);
		}

		if(se!=null) {
			throw(se);
		}
	}

	// Show a user to be edited
	protected void admEditUserForm() throws ServletException {
		String output="";
		ServletException se=null;
		if(!isAdmin()) {
			throw new ServletException("Not admin");
		}
		Connection photo=null;
		try {
			String s_user_id=request.getParameter("userid");
			int userid=Integer.parseInt(s_user_id);
			Hashtable h=new Hashtable();
			Hashtable cats=new Hashtable();
			String acltable="";

			// Defaults -- Basically for a new uesr
			h.put("USERID", s_user_id);
			h.put("USER", "New User");
			h.put("REALNAME", "New User");
			h.put("EMAIL", "");
			h.put("PASS", "");
			h.put("CANADD", "");
			h.put("CANNOTADD", "checked");

			photo=getDBConn();

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
				"select cat from wwwacl where userid=?"
				);
			st.setInt(1, userid);
			rs=st.executeQuery();
			while(rs.next()) {
				cats.put(rs.getString("cat"), "1");
			}

			// Third DB query gets all of the categories
			st=photo.prepareStatement(
				"select * from cat order by name"
				);
			rs=st.executeQuery();
			while(rs.next()) {
				String catname=rs.getString("name");
				String cat_id_s=rs.getString("id");
				int cat_id=rs.getInt("id");

				if(cats.containsKey(cat_id_s)) {
					acltable+="<tr>\n\t<td><font color=green>"
						+ rs.getString("name")
						+ "</font></td>";
					acltable+="<td><input type=checkbox name=catacl checked "
						+ "value=" + cat_id + "></td></tr>\n";
				} else {
					acltable+="<tr>\n\t<td><font color=red>"
						+ rs.getString("name")
						+ "</font></td>";
					acltable+="<td><input type=checkbox name=catacl "
						+ "value=" + cat_id + "></td></tr>\n";
				}
			}

			h.put("ACLTABLE", acltable);

			output=tokenize("admin/userform.inc", h);
		} catch(Exception e) {
			se=new ServletException("Error displaying user:  " + e);
		} finally {
			freeDBConn(photo);
		}
		if(se!=null) {
			throw(se);
		}
		send_response(output);
	}

	protected void admSaveUser() throws ServletException {
		String output="";

		Connection photo=null;

		try {
			String pass=request.getParameter("password");
			// At 13 or more, it's probably a crypt() or other hash.
			if(pass.length()<13) {
				pass=security.getDigest(pass);
			}
			String user_id_s=request.getParameter("userid");
			int user_id=Integer.parseInt(user_id_s);

			photo=getDBConn();
			photo.setAutoCommit(false);

			PreparedStatement st=null;
			
			// Decide whether it's a new user, or a used user
			if(user_id>0) {
				// Used user
				st=photo.prepareStatement(
					"update wwwusers set username=?, realname=?, email=?, "
						+ "password=?, canadd=?\n"
						+ "\twhere id=?"
					);
				st.setString(1, request.getParameter("username"));
				st.setString(2, request.getParameter("realname"));
				st.setString(3, request.getParameter("email"));
				st.setString(4, pass);
				st.setString(5, request.getParameter("canadd"));
				st.setInt(6, user_id);
				st.executeUpdate();
			} else {
				// New user
				st=photo.prepareStatement(
					"insert into wwwusers(username, realname, email, "
						+ "password, canadd) values(?, ?, ?, ?, ?)"
					);
				st.setString(1, request.getParameter("username"));
				st.setString(2, request.getParameter("realname"));
				st.setString(3, request.getParameter("email"));
				st.setString(4, pass);
				st.setString(5, request.getParameter("canadd"));
				st.executeUpdate();

				st=photo.prepareStatement("select currval('wwwusers_id_seq')");
				ResultSet rs=st.executeQuery();
				rs.next();
				// Get the user_id we just inserted.
				user_id=rs.getInt(1);
			}

			// Delete all of the ACLs
			st=photo.prepareStatement("delete from wwwacl where userid=?");
			st.setInt(1, user_id);
			st.executeUpdate();

			// Add the new ACLs for the user
			String acls[]=request.getParameterValues("catacl");
			if(acls!=null) {
				for(int i=0; i<acls.length; i++) {
					int cat_id=Integer.parseInt(acls[i]);
					st=photo.prepareStatement(
						"insert into wwwacl(userid,cat) values(?,?)"
						);
					st.setInt(1, user_id);
					st.setInt(2, cat_id);
					st.executeUpdate();
				}
			}

			photo.commit();
		} catch(Exception e) {
			log("Error saving user:  " + e);
			e.printStackTrace();
			try {
				if(photo!=null) {
					photo.rollback();
				}
			} catch(Exception e2) {
				// Don't mind this...
			}
		} finally {
			if(photo != null) {
				try {
					photo.setAutoCommit(true);
					freeDBConn(photo);
				} catch(Exception e) {
					log(e.getMessage());
				}
			}
			freeDBConn(photo);
		}
		admShowUsers();
	}

	// Show the category edit form
	protected void admShowCategories() throws ServletException {
		String output="";
		if(!isAdmin()) {
			throw new ServletException("Not admin");
		}
		Hashtable h = new Hashtable();
		h.put("CATS", getCatList(-1));
		output=tokenize("admin/admcat.inc", h);
		send_response(output);
	}

	protected void admEditCategoryForm() throws ServletException {
		String output="";
		ServletException se=null;
		if(!isAdmin()) {
			throw new ServletException("Not admin");
		}
		Connection photo=null;
		try {
			String s_cat_id=request.getParameter("cat");
			int cat_id=Integer.parseInt(s_cat_id);
			Hashtable h=new Hashtable();
			h.put("CATID", "-1");
			h.put("CATNAME", "");
			photo=getDBConn();
			PreparedStatement st=photo.prepareStatement(
				"select name from cat where id=?"
				);
			st.setInt(1, cat_id);
			ResultSet rs=st.executeQuery();
			while(rs.next()) {
				h.put("CATID", s_cat_id);
				h.put("CATNAME", rs.getString("name"));
			}
			output=tokenize("admin/editcat.inc", h);
		} catch(Exception e) {
			se=new ServletException("Error displaying cat:  " + e);
		} finally {
			freeDBConn(photo);
		}
		if(se!=null) {
			throw(se);
		}
		send_response(output);
	}

	// Show the category edit form
	protected void admSaveCategory() throws ServletException {
		String output="";
		if(!isAdmin()) {
			throw new ServletException("Not admin");
		}
		Hashtable h = new Hashtable();
		ServletException se=null;
		Connection photo=null;
		try {
			String stmp=request.getParameter("id");
			int cat_id=Integer.parseInt(stmp);
			String cat_name=request.getParameter("name");

			photo=getDBConn();
			if(cat_id>0) {
				PreparedStatement st=photo.prepareStatement(
					"update cat set name=? where id=?"
					);
				st.setString(1, request.getParameter("name"));
				st.setInt(2, cat_id);
				st.executeUpdate();
			} else {
				PreparedStatement st=photo.prepareStatement(
					"insert into cat(name) values(?)"
					);
				st.setString(1, cat_name);
				st.executeUpdate();
			}
		} catch(Exception e) {
			se=new ServletException("Error in admEditCategoryForm: " + e);
		} finally {
			freeDBConn(photo);
		}
		if(se!=null) {
			throw(se);
		}
		// If we make it to the bottom, show the categories again
		admShowCategories();
	}

	// Show the style form
	protected void showCredForm () throws ServletException {
		String output;

		output = tokenize("authform.inc", new Hashtable());
		send_response(output);
	}

	public void setCreds () throws ServletException, IOException {
		String username=null, pass=null;

		username=request.getParameter("username");
		pass=request.getParameter("password");

		// Make sure we drop administrative privs if any
		unsetAdmin();

		log("Verifying password for " + username);
		if(security.checkPW(username, pass)) {
			if(session==null) {
				session=request.getSession(true);
			}
			session.putValue("username", username);
			// Make it valid immediately
			remote_user = "dustin";
			getUid();
		}
	}


	// We need to reinitialize if something bad happens and we can tell..
	protected void reInitialize() {
		log("Application would like to reinitialize.");
		try {
			SpyDB db=new SpyDB(new PhotoConfig(), false);
			db.init();
		} catch(Exception e) {
			// Do nothing.
		}
	}

	// Grab a connection from the pool.
	protected Connection getDBConn() throws Exception {
		SpyDB pdb=new SpyDB(new PhotoConfig(), false);
		return(pdb.getConn());
	}

	// Gotta free the connection
	protected void freeDBConn(Connection conn) {
		SpyDB pdb=new SpyDB(new PhotoConfig(), false);
		pdb.freeDBConn(conn);
	}

	// Get the saved searches.
	protected String showSaved() {
		String query, out="";
		BASE64Decoder base64 = new BASE64Decoder();
		Connection photo=null;

		try {
			photo=getDBConn();
			Statement st=photo.createStatement();

			query = "select * from searches order by name\n";
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				byte data[];
				String tmp;
				data=base64.decodeBuffer(rs.getString(4));
				tmp = new String(data);
				out += "    <li><a href=\"" + self_uri + "?"
					+ tmp + "\">" + rs.getString(2) + "</a></li>\n";
			}
		} catch(Exception e) {
			// Nothing
		} finally {
			if(photo != null) {
				freeDBConn(photo);
			}
		}
		return(out);
	}

	// Find out if the authenticated user can add stuff.
	protected boolean canadd() {
		boolean r=false;

		try{
			PhotoUser p = security.getUser(remote_user);
			r=p.canadd;
		} catch(Exception e) {
			log("Error getting canadd permissions:  " + e.getMessage());
		}

		return(r);
	}

	// Save the new data
	protected void saveImageInfo() throws ServletException {
		if(!isAdmin()) {
			throw new ServletException("Must be an admin to do this.");
		}

		String keywords="", info="", taken="";
		String out="", stmp=null;
		int id=-1;
		int category=-1;

		// We need a short lifetime for whatever page this produces
        long l=new java.util.Date().getTime();
        l+=10000L;
        response.setDateHeader("Expires", l);

		// Get the ID
		stmp=request.getParameter("id");
		id=Integer.parseInt(stmp);

		// Get the category ID
		stmp=request.getParameter("cat");
		category=Integer.parseInt(stmp);

		// Get the string data
		taken=request.getParameter("taken");
		keywords=request.getParameter("keywords");
		info=request.getParameter("info");

		Connection photo=null;
		try {
			photo=getDBConn();
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
		} catch(Exception e) {
			log("Error updating information:  " + e);
		} finally {
			freeDBConn(photo);
		}
	}

	// Add an image
	protected void doAddPhoto() throws ServletException {
		String category="", keywords="", picture="", info="", taken="";
		String query="", out="", stmp, type;
		int id;
		Hashtable h = new Hashtable();
		Connection photo=null;
		Statement st=null;

		// We need a short lifetime for whatever page this produces
        long l=new java.util.Date().getTime();
        l+=10000L;
        response.setDateHeader("Expires", l);


		// Make sure the user can add.
		if(!canadd()) {
			send_response(tokenize("add_denied.inc", h));
			return;
		}

		File f;
		f = multi.getFile("picture");

		// Check that it's the right file type.
		type = multi.getContentType("picture");
		log("Type is " + type);
		if( type == null || (! (type.startsWith("image/jpeg"))) ) {
			h.put("FILENAME", multi.getFilesystemName("picture"));
			h.put("FILETYPE", type);
			send_response(tokenize("add_badfiletype.inc", h));
			try {
				f.delete();
			} catch(Exception e) {
				log(e.getMessage());
			}
			return;
		}

		stmp=multi.getParameter("category");
		if(stmp!=null) {
			category=PhotoUtil.dbquote_str(stmp);
		}

		stmp=multi.getParameter("keywords");
		if(stmp!=null) {
			keywords=PhotoUtil.dbquote_str(stmp);
		}

		stmp=multi.getParameter("info");
		if(stmp!=null) {
			info=PhotoUtil.dbquote_str(stmp);
		}

		stmp=multi.getParameter("taken");
		if(stmp!=null) {
			taken=PhotoUtil.dbquote_str(stmp);
		}

		try {
			FileInputStream in;
			Vector v = new Vector();
			int bufsize=1024;
			byte data[] = new byte[bufsize];

			photo=getDBConn();
			st=photo.createStatement();
			photo.setAutoCommit(false);
			query = "insert into album(keywords,descr,cat,taken,addedby)\n"
				  + "    values('" + keywords + "',\n\t'" + info + "',\n"
				  + "    \t'" + category + "',\n\t'" + taken + "',\n"
				  + "    '" + remote_uid + "')\n";
			st.executeUpdate(query);
			query = "select currval('album_id_seq')\n";
			ResultSet rs = st.executeQuery(query);
			rs.next();
			id=rs.getInt(1);

			// Encode the shit;
			int size=0, length=0;
			in = new FileInputStream(f);

			while( (length=in.read(data)) >=0 ) {
				String tmp;

				size+=length;
				if(length == bufsize) {
					byte tb[] = new byte[length];
					int j;

					for(j=0; j<length; j++) {
						tb[j] = data[j];
					}
					v.addElement(tb);
				} else {
					byte tb[] = new byte[length];
					int j;

					for(j=0; j<length; j++) {
						tb[j] = data[j];
					}
					v.addElement(tb);
				}
			}
			query ="update album set size=" + size + "where id=" + id;
			st.executeUpdate(query);

			PhotoImage photo_image=new PhotoImage(id);
			photo_image.storeImage(new ImageData(v));

			query = "insert into upload_log values(\n"
				  + "\t" + id + ", " + remote_uid + ")";
			st.executeUpdate(query);

			h.put("ID", ""+id);

			photo.commit();
			out += tokenize("add_success.inc", h);
		} catch(Exception e) {
			log(e.getMessage());
			try {
				photo.rollback();
				h.put("QUERY", query);
				h.put("ERRSTR", e.getMessage());
				out += tokenize("add_dbfailure.inc", h);
			} catch(Exception e2) {
				log("Error rolling back and/or reporting add faulre:  " + e2);
			}
		} finally {
			if(photo != null) {
				try {
					photo.setAutoCommit(true);
					freeDBConn(photo);
				} catch(Exception e) {
					log(e.getMessage());
				}
			}
			try {
				f.delete();
			} catch(Exception e) {
				log(e.getMessage());
			}
		}

		send_response(out);
	}

	// Get a list of categories for a select list
	protected String getCatList(int def) {
		String query, out="";
		Connection photo=null;
		try {
			photo=getDBConn();
			Statement st=photo.createStatement();

			query = "select * from cat where id in\n"
			  	+ "(select cat from wwwacl where\n"
			  	+ "    userid=" + remote_uid + ")\n"
			  	+ "order by name\n";

			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				int id=rs.getInt("id");
				String selected="";
				if(id==def) {
					selected=" selected";
				}
				out += "    <option value=\"" + id + "\"" + selected
					+ ">" + rs.getString("name") + "\n";
			}
		} catch(Exception e) {
			// Nothing
		} finally {
				if(photo != null) {
					freeDBConn(photo);
				}
		}
		return(out);
	}

	// Show the style form
	protected void doStyleForm () throws ServletException {
		String output;

		output = tokenize("presetstyle.inc", new Hashtable());
		send_response(output);
	}

	// Get the stylesheet from the cookie, or the default.
	protected void doGetStylesheet () throws ServletException {
		Cookie cookies[];
		String output = null;
		int i;

		cookies = request.getCookies();

		if(cookies!=null) {
			for(i=0; i<cookies.length && output == null; i++) {
				String s = cookies[i].getName();
				if(s.equalsIgnoreCase("photo_style")) {
					output = cookies[i].getValue();
				}
			}
		}

		if(output == null) {
			output = tokenize("style.css", new Hashtable());
		}

		// This is a little different, so we won't use send_response()
		response.setContentType("text/css");
		java.util.Date d=new java.util.Date();
		long l=d.getTime();
		l+=36000000L;
		response.setDateHeader("Expires", l);
		try {
			PrintWriter out = response.getWriter();
			out.print(output);
			out.close();
		} catch(Exception e) {
		}
	}

	// Set the style cookie from the POST data.
	protected void doSetStyle() throws ServletException {
		Cookie c;
		String stmp="", font="", bgcolor="", c_text="";
		Hashtable h = new Hashtable();

		stmp = request.getParameter("font");
		if(stmp != null && stmp.length() > 1) {
			font = stmp;
		}

		stmp = request.getParameter("bgcolor");
		if(stmp != null && stmp.length() > 1) {
			bgcolor = stmp;
		}

		c_text = "body,td {font-family: " + font + ", Arial; "
			   + "background-color: " + bgcolor + ";}\n";

		stmp = request.getParameter("d_transform");
		if(stmp != null && stmp.length() > 1) {
			c_text += "blockquote {text-transform: " + stmp + ";};\n";
		}

		stmp = request.getParameter("d_transform");
		if(stmp != null && stmp.length() > 1) {
			c_text += "h1,h2,h3,h4,h5 {text-transform: " + stmp + ";};\n";
		}

		// Create the cookie
		c = new Cookie("photo_style", URLEncoder.encode(c_text));
		// 30 days of cookie
		c.setMaxAge( (30 * 86400) );
		// Describe why we're doing this.
		c.setComment("Your style preferences for the photo album.");
		// Where we'll be using it.
		c.setPath(self_uri);

		// Add it to the responses.
		response.addCookie(c);

		// Prepare output.
		stmp = "";
		h.put("STYLE", c_text);

		stmp = tokenize("setstyle.inc", h);
		send_response(stmp);
	}

	// Show the add an image form.
	protected void doAddForm() throws ServletException {
		String output = new String("");
		Hashtable h = new Hashtable();

		try {
			h.put("CAT_LIST", getCatList(-1));
		} catch(Exception e) {
			h.put("CAT_LIST", "");
		}
		h.put("TODAY", PhotoUtil.getToday());
		output += tokenize("addform.inc", h);
		send_response(output);
	}

	// Show the search form.
	protected void doFindForm() throws ServletException {
		String output = new String("");
		Hashtable h = new Hashtable();

		try {
			h.put("CAT_LIST", getCatList(-1));
		} catch(Exception e) {
			h.put("CAT_LIST", "");
		}
		output += tokenize("findform.inc", h);
		send_response(output);
	}

	// View categories
	protected void doCatView() throws ServletException {
		String output = new String("");
		String query, catstuff="";
		Hashtable h = new Hashtable();
		Connection photo;

		try {
			photo = getDBConn();
		} catch(Exception e) {
			throw new ServletException("Can't get database connection:  "
				+ e.getMessage());
		}

		query = "select name,id,catsum(id) as cs from cat\n"
			  + "where id in\n"
			  + "  (select cat from wwwacl where\n"
			  + "   userid=" + remote_uid + ")\n"
			  + " order by cs desc";

		try {
			Statement st = photo.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				String t;
				if(rs.getInt(3)==1) {
					t = " image";
				} else {
					t = " images";
				}

				catstuff += "<li>" + rs.getString(1) + ":  <a href=\""+self_uri
				   	+ "?func=search&searchtype=advanced&cat="
				   	+ rs.getString(2) + "&maxret=5\">"
				   	+ rs.getString(3) + t + "</a></li>\n";
			}
		} catch(Exception e) {
		}
		finally { freeDBConn(photo); }

		h.put("CATSTUFF", catstuff);

		output += tokenize("catview.inc", h);
		send_response(output);
	}

	// Display the index page.
	protected void doIndex() throws ServletException {
		String output = new String("");;
		Hashtable h = new Hashtable();

		try {
			h.put("SAVED", showSaved());
		} catch(Exception e) {
			h.put("SAVED", "");
		}
		if(isAdmin()) {
			output += tokenize("admin/index.inc", h);
		} else {
			output += tokenize("index.inc", h);
		}
		send_response(output);
	}

	// Get the UID
	protected void getUid() throws ServletException {
		String query;

		try {
			if(session==null) {
				remote_user="guest";
			} else {
				remote_user=(String)session.getValue("username");
				// If we have a session, but no username, add guest.
				if(remote_user==null) {
					remote_user="guest";
					session.putValue("username", "guest");
				}
			}
			PhotoUser p=security.getUser(remote_user);
			remote_uid = p.id;
		} catch(Exception e) {
			throw new ServletException("Unknown user: " + remote_user);
		}
	}

	protected void getGroups() throws Exception {
		String query;

		if(groups == null) {
			groups = new Hashtable();
			Connection photo=getDBConn();

			try {
				PreparedStatement st=photo.prepareStatement(
					"select * from show_group where username = ?"
				);
				st.setString(1, remote_user);
				ResultSet rs = st.executeQuery();
				while(rs.next()) {
					String groupName=rs.getString("groupname");
					groups.put(groupName, "1");
				}
			} catch (Exception e) {
				log("Error fetching groups:  " + e);
			} finally {
				freeDBConn(photo);
			}
		}
	}

	// Display dispatcher
	protected void doDisplay() throws ServletException {
		String id=null;
		String search_id=null;

		id = request.getParameter("id");
		search_id = request.getParameter("search_id");

		if(id!=null) {
			doDisplayByID();
		} else if(search_id!=null) {
			doDisplayBySearchId();
		}
	}

	protected void doDisplayBySearchId() throws ServletException {
		PhotoSearchResults results=null;
		results=(PhotoSearchResults)session.getValue("search_results");
		int which=Integer.parseInt(request.getParameter("search_id"));
		PhotoSearchResult r = results.get(which);
		Hashtable h = new Hashtable();

		h.put("IMAGE",     r.image);
		h.put("KEYWORDS",  r.keywords);
		h.put("INFO",      r.descr);
		h.put("SIZE",      r.size);
		h.put("TAKEN",     r.taken);
		h.put("TIMESTAMP", r.ts);
		h.put("CAT",       r.cat);
		h.put("CATNUM",    r.catnum);
		h.put("ADDEDBY",   r.addedby);

		if(results.nResults() > which+1) {
			h.put("NEXT",
				"<a href=\"" + self_uri + "?func=display&search_id="
				+ (which+1) + "\">&gt;&gt;&gt;</a><br>");
		} else {
			h.put("NEXT", "");
		}

		if(which>0) {
			h.put("PREV",
				"<a href=\"" + self_uri + "?func=display&search_id="
				+ (which-1) + "\">&lt;&lt;&lt;</a><br>");
		} else {
			h.put("PREV", "");
		}

		String output = null;
		if(isAdmin()) {
			// Admin needs CATS
			int defcat=Integer.parseInt(r.catnum);
			h.put("CATS", getCatList(defcat));
			output=tokenize("admin/display.inc", h);
		} else {
			output=tokenize("display.inc", h);
		}
		send_response(output);
	}

	// Find and display images.
	protected void doDisplayByID() throws ServletException {
		String query, output = "";
		int i;
		Integer image_id;
		String stmp;
		Hashtable h = new Hashtable();
		Connection photo;

		try {
			photo=getDBConn();
		} catch(Exception e) {
			throw new ServletException("Can't get database connection:  "
				+ e.getMessage());
		}

		stmp = request.getParameter("id");
		if(stmp == null) {
			throw new ServletException("Not enough information.");
		}
		image_id=Integer.valueOf(stmp);

		query = "select a.id,a.keywords,a.descr,\n"
			+ "   a.size,a.taken,a.ts,b.name,a.cat,c.username,b.id\n"
			+ "   from album a, cat b, wwwusers c\n"
			+ "   where a.cat=b.id and a.id=" + image_id
			+ "\n   and a.addedby=c.id\n"
			+ "   and a.cat in (select cat from wwwacl where "
			+ "userid=" + remote_uid + ")\n";

		output += "<!-- Query:\n" + query + "\n-->\n";

		try {
			Statement st = photo.createStatement();
			ResultSet rs = st.executeQuery(query);

			if(rs.next() == false) {
				throw new ServletException("No data found for that id.");
			}

			h.put("IMAGE",     rs.getString(1));
			h.put("KEYWORDS",  rs.getString(2));
			h.put("INFO",      rs.getString(3));
			h.put("SIZE",      rs.getString(4));
			h.put("TAKEN",     rs.getString(5));
			h.put("TIMESTAMP", rs.getString(6));
			h.put("CAT",       rs.getString(7));
			h.put("CATNUM",    rs.getString(8));
			h.put("ADDEDBY",   rs.getString(9));

			// These don't apply here.
			h.put("PREV",      "");
			h.put("NEXT",      "");

			if(isAdmin()) {
				// Admin needs CATS
				int defcat=rs.getInt(8);
				h.put("CATS", getCatList(defcat));
				output += tokenize("admin/display.inc", h);
			} else {
				output += tokenize("display.inc", h);
			}

		} catch(SQLException e) {
			throw new ServletException("Some kinda SQL problem.");
		}
		finally { freeDBConn(photo); }

		send_response(output);
	}

	// Send the response text...
	protected void send_response(String text)
	{
		// set content type and other response header fields first
		response.setContentType("text/html");
		try {
			PrintWriter out = response.getWriter();
			out.print(text);
			Hashtable ht=new Hashtable();
			if(isAdmin()) {
				ht.put("ADMIN_FLAG",
					" - <a href=\"" + self_uri
						+ "?func=unsetadmin\">Admin mode</a>");
			} else {
				ht.put("ADMIN_FLAG", "");
			}
			out.print(tokenize("tail.inc", ht));
			out.close();
		} catch(Exception e) {
			// I really don't care at this point if this doesn't work...
		}
	}

	// Display search results
	// This whole thing will fail if there's no session.
	protected void displaySearchResults() throws ServletException {
		if(session==null) {
			throw new ServletException("There's no session!");
		}
		PhotoSearchResults results=
			(PhotoSearchResults)session.getValue("search_results");
		if(results==null) {
			throw new ServletException("There are no search results!");
		}

		String middle="";
		Hashtable h=null;
		int i=0;
		// if we have a starting point, let's start there.
		try {
			String startingS=request.getParameter("startfrom");
			if(startingS==null) {
				int starting=Integer.parseInt(startingS);
				results.set(starting);
			}
		} catch(Exception e) {
			// If there's an exception, something went wrong with finding
			// where to start from.  This is OK, we'll just start from
			// where we were when we last displayed a page.
		}

		for(i=0; i<5; i++) {
			PhotoSearchResult r=results.next();
			if(r!=null) {
				h = new Hashtable();
				h.put("KEYWORDS", r.keywords);
				h.put("DESCR",    r.descr);
				h.put("CAT",      r.cat);
				h.put("SIZE",     r.size);
				h.put("TAKEN",    r.taken);
				h.put("TS",       r.ts);
				h.put("IMAGE",    r.image);
				h.put("CATNUM",   r.catnum);
				h.put("ADDEDBY",  r.addedby);
				h.put("ID",       "" + r.id);

				if( ((i) % 2) == 0) {
					middle += "</tr>\n<tr>\n";
				}

				middle += "<td>\n";
				middle += tokenize("findmatch.inc", h);
				middle += "</td>\n";
			}
		}

		h = new Hashtable();
		h.put("TOTAL", "" + results.nResults());
		h.put("SEARCH", (String)session.getValue("encoded_search"));
		String output = tokenize("find_top.inc", h);
		output += middle;
		h.put("LINKTOMORE", linkToMore(results)); 
		output += tokenize("find_bottom.inc", h);
		send_response(output);
	}

	// Find images.
	protected void doFind() throws ServletException {
		String output = "", middle = "";
		PhotoSearch ps = new PhotoSearch();
		PhotoUser user = security.getUser(remote_user);

		PhotoSearchResults results=null;

		// Make sure there's a real session.
		if(session==null) {
			session=request.getSession(true);
		}

		// Get the results and put them in the mofo session
		results=ps.performSearch(request, user);
		session.putValue("search_results", results);
		session.putValue("encoded_search",
			ps.encodeSearch(request));

		displaySearchResults();
	}

	// Link to more search results
	protected String linkToMore(PhotoSearchResults results) {
		String ret = "";
		int remaining=results.nRemaining();

		if(remaining>0) {
			int nextwhu=5;
			if(remaining<5) {
				nextwhu=remaining;
			}

			ret += "<form method=\"POST\" action=\"" + self_uri + "\">\n";
			ret += "<input type=hidden name=func value=nextresults>\n";
			ret += "<input type=hidden name=startfrom value="
				+ results.current() + ">\n";

			ret += "<input type=\"submit\" value=\"Next " + nextwhu + "\">\n";
			ret += "</form>\n";
			ret += "<br>\n" + remaining + " pictures remaining.<br>\n";
		}
		return(ret);
	}

	// Show an image
	protected void showImage() throws ServletException {

		Vector v;
		int i, which;
		boolean thumbnail=false;
		ServletOutputStream out;

		response.setContentType("image/jpeg");
		java.util.Date d=new java.util.Date();
		long l=d.getTime();
		// This is thirty days
		l+=25920000000L;
		response.setDateHeader("Expires", l);

		String s = request.getParameter("photo_id");
		which = Integer.valueOf(s).intValue();

		s=request.getParameter("thumbnail");
		if(s!=null) {
			thumbnail=true;
		}

		if(rhash==null || !rhash.connected()) {
			throw new ServletException("Me hath no working rhash");
		}

		try {
			// The new swank image extraction object.
			PhotoImage p = new PhotoImage(which, rhash);

			// Need a binary output thingy.
			out = response.getOutputStream();

			if(thumbnail) {
				log("Requesting thumbnail");
				v=p.getThumbnail();
			} else {
				log("Requesting full image");
				v=p.getImage();
			}
			logger.log(new PhotoLogImageEntry(remote_uid.intValue(),
				which, true, request));
			for(i = 0; i<v.size(); i++) {
				out.write( (byte[])v.elementAt(i));
			}

		} catch(Exception e) {
			throw new ServletException("IOException:  " + e.getMessage());
		}
	}

	protected void doLogView() throws ServletException {
		String view, out="";
		PhotoLogView logview=null;

		try {
			logview=new PhotoLogView(this);
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}

		view=request.getParameter("view");
		if(view==null) {
			throw new ServletException("LogView without view");
		}

		if(view.equalsIgnoreCase("viewers")) {
			String which;
			which=request.getParameter("which");
			if(which==null) {
				throw new ServletException("LogView/viewers without which");
			}
			try {
				out=logview.getViewersOf(Integer.valueOf(which));
			} catch(Exception e) {
				throw new ServletException(e.getMessage());
			}
		}
		send_response(out);
	}

	// Set administrative privys
	protected void setAdmin() throws ServletException {
		try {
			getGroups();
			if(groups.containsKey("admin")) {
				session.putValue("is_admin", "1");
			}
		} catch(Exception e) {
			log("Error setting admin privs:  " + e);
		}
	}

	// Revoke administrative privys
	protected void unsetAdmin() throws ServletException  {
		if(session!=null) {
			session.removeValue("is_admin");
		}
	}

	// Returns true if the session is an admin session
	protected boolean isAdmin() {
		boolean ret=false;
		if(session!=null) {
			String admin= (String)session.getValue("is_admin");
			if(admin!=null) {
				ret=true;
			}
		}
		return(ret);
	}

	// Tokenize a template file and return the tokenized stuff.
	protected String tokenize(String file, Hashtable vars) {
		return(PhotoUtil.tokenize(this, file, vars));
	}
}
