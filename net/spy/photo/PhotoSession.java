/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoSession.java,v 1.61 2001/07/20 09:51:56 dustin Exp $
 */

package net.spy.photo;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.oreilly.servlet.*;

import net.spy.*;
import net.spy.cache.*;
import net.spy.log.*;
import net.spy.util.*;

// The class
public class PhotoSession extends Object
{ 
	// This kinda stuff is only persistent for a single connection.
	private String self_uri=null;
	private MultipartRequest multi=null;
	private SpyLog logger=null;
	private PhotoSecurity security = null;
	private PhotoServlet photo_servlet = null;

	private String xslt_stylesheet=null;

	private boolean xmlraw=false;
	private boolean debug=true;

	private PhotoAheadFetcher aheadfetcher=null;

	// These are public because they may be used by PhotoHelpers
	public HttpServletRequest request=null;
	public HttpServletResponse response=null;
	public HttpSession session=null;

	private PhotoUser user=null;

	public PhotoSession(PhotoServlet p,
		HttpServletRequest request,
		HttpServletResponse response) {

		photo_servlet=p;
		this.request=request;
		this.response=response;
		this.session=request.getSession(false);

		// The aheadfetcher
		this.aheadfetcher=p.aheadfetcher;

		logger=p.logger;
		security=p.security;
		xmlraw=false;
	}

	// This gets us back into the servlet engine's log.
	private void log(String whu) {
		photo_servlet.log(whu);
	}

	// process a request
	public void process() throws ServletException, IOException {

		String func=null, type=null, out=null;

		try {
			type = request.getContentType();
			if(type != null && type.startsWith("multipart/form-data")) {
				multi = new MultipartRequest(request, "/tmp");
			} else {
				multi = null;
			}

			// Set the self_uri
			self_uri = request.getRequestURI();

			getCreds();

			getStyleSheet();

			// Figure out what they want, default to index.
			if(multi==null) {
				func=request.getParameter("func");

				// Find out if they want raw XML or not...
				String tmp=request.getParameter("xmlraw");
				if(tmp!=null) {
					xmlraw=true;
				}
			} else {
				func=multi.getParameter("func");
			}

			out=dispatchFunction(func);

		} catch(Exception e) {
			log("Error:  " + e + "\nStack:  " + SpyUtil.getStack(e, 1));
			out=createError(e);
			try {
				sendXML(out);
			} catch(Exception e2) {
				log("Compound error:  We also failed to send back the error, "
					+ e2);
				throw new ServletException("Error sending error message", e2);
			}
			out=null;
		}

		// Some things handle their own responses, and return null.
		if(out!=null) {
			send_response(out);
		}
	}

	private String createError(Exception e) {
		StringBuffer rv=new StringBuffer();

		// XML header
		rv.append("<?xml version=\"1.0\"?>\n");

		rv.append("<exception>\n");
		rv.append("\t<text>\n");
		rv.append("\t\t" + PhotoXSLT.normalize(e.getMessage(), true) + "\n");
		rv.append("\t</text>\n");
		rv.append("\t<exception_class>\n");
		rv.append("\t\t" + PhotoXSLT.normalize(e.getClass().getName(), true)
			+ "\n");
		rv.append("\t</exception_class>\n");
		rv.append("\t<stack>\n");
		String s[]=SpyUtil.split(",", SpyUtil.getStack(e, 1));
		for(int i=0; i<s.length; i++) {
			rv.append("\t\t<stack_entry>\n");
			rv.append("\t\t\t" + PhotoXSLT.normalize(s[i], true) + "\n");
			rv.append("\t\t</stack_entry>\n");
		}
		rv.append("\t</stack>\n");
		rv.append("</exception>\n");

		return(rv.toString());
	}

	private String dispatchFunction(String func) throws Exception {

		// If there's no function, set the function to index.
		if(func==null) {
			func="index";
		}

		// Lowercase it so that we don't have to keep doing case ignores
		func=func.toLowerCase();
		log(user + " requested " + func);
		String out=null;

		// OK, see what they're doing.
		if(func.equals("search")) {
			out=doFind();
		} else if(func.equals("nextresults")) {
			out=displaySearchResults();
		} else if(func.equals("addimage")) {
			out=doAddPhoto();
		} else if(func.equals("index")) {
			out=doIndex();
		} else if(func.equals("findform")) {
			out=doFindForm();
		} else if(func.equals("addform")) {
			out=doAddForm();
		} else if(func.equals("changepwform")) {
			out=doChangePWForm();
		} else if(func.equals("changepw")) {
			out=doChangePW();
		} else if(func.equals("catview")) {
			out=doCatView();
		} else if(func.equals("setstyle")) {
			out=doSetStyle();
		} else if(func.equals("styleform")) {
			out=doStyleForm();
		} else if(func.equals("getstylesheet")) {
			out=doGetStylesheet();
		} else if(func.equals("display")) {
			out=doDisplay();
		} else if(func.equals("logview")) {
			out=doLogView();
		} else if(func.equals("getimage")) {
			out=showImage();
		} else if(func.equals("credform")) {
			out=showCredForm();
		} else if(func.equals("newuserform")) {
			out=showNewUserForm();
		} else if(func.equals("adduser")) {
			out=addUser();
		} else if(func.equals("savesearch")) {
			out=saveSearch();
		} else if(func.equals("setstylesheet")) {
			setStyleSheet();
			out=doIndex();
		} else if(func.equals("setcred")) {
			setCreds();
			out=doIndex();
		} else if(func.equals("setadmin")) {
			setAdmin();
			out=doIndex();
		} else if(func.equals("unsetadmin")) {
			unsetAdmin();
			out=doIndex();
		} else if(func.startsWith("adm")) {
			// Anything that starts with rep is probably reporting.
			PhotoAdmin adm=new PhotoAdmin(this);
			out=adm.process(func);
		} else if(func.startsWith("rep")) {
			// Anything that starts with rep is probably reporting.
			PhotoReporting rep=new PhotoReporting(this);
			out=rep.process(func);
		} else {
			throw new ServletException("No known function.");
		}
		return(out);
	}

	private String saveSearch() throws ServletException {
		try {
			PhotoSearch ps = new PhotoSearch();
			ps.saveSearch(request, user);

			PhotoXML xml=new PhotoXML();
			xml.setTitle("Saved Search");
			xml.addBodyPart(getGlobalMeta());
			xml.addBodyPart("<save_search_success/>");
			sendXML(xml.toString());

		} catch(Exception e) {
			Hashtable h = new Hashtable();
			log("Search save failed:  " + e);
			throw new ServletException(
				"Error saving search", e);
		}
		return(null);
	}

	// Set the stylesheet
	private void setStyleSheet() throws ServletException {
		String ss=request.getParameter("stylesheet");

		// Make sure something was passed in.
		if(ss!=null) {
			// Make sure a session exists
			if(session==null) {
				session=request.getSession(true);
			}

			// Make it available for the session
			session.setAttribute("photo_stylesheet", ss);
			// Make it immediately available
			xslt_stylesheet=ss;
		}
	}

	// Figure out what stylesheet to use
	private void getStyleSheet() {
		xslt_stylesheet=null;

		PhotoConfig conf=new PhotoConfig();

		if(session!=null) {
			xslt_stylesheet=(String)session.getAttribute("photo_stylesheet");
		}
	}

	private void getCreds() throws ServletException {
		getUid();
		log("Authenticated as " + user);
	}

	public void setCreds() throws ServletException, IOException {
		String username=request.getParameter("username");
		String pass=request.getParameter("password");

		// Make sure we drop administrative privs if any
		unsetAdmin();

		PhotoUser user=security.getUser(username);

		// We don't do anything unless the password is correct.
		if(user.checkPassword(pass)) {
			// Make sure there is a session.
			if(session==null) {
				session=request.getSession(true);
			}
			// Save the username.
			session.setAttribute("photo_user", user);

			// Make it available
			this.user=user;

			// Set the UID variable.
			getUid();
		}
	}

	// Grab a connection from the pool.
	private Connection getDBConn() throws Exception {
		SpyDB pdb=new SpyDB(new PhotoConfig(), false);
		return(pdb.getConn());
	}

	// Gotta free the connection
	private void freeDBConn(Connection conn) {
		SpyDB pdb=new SpyDB(new PhotoConfig(), false);
		pdb.freeDBConn(conn);
	}

	// Get the saved searches.
	private String showSaved() {
		String out="";
		Connection photo=null;
		SpyCache cache=new SpyCache();
		out=(String)cache.get("saved_searches");
		// If we don't have it cached, grab it and cache it.
		if(out==null) {
			log("saved_searches was not cached, must fetch from the db");
			try {
				out="";
				photo=getDBConn();
				Base64 base64 = new Base64();
				Statement st=photo.createStatement();

				String query = "select * from searches order by name\n";
				ResultSet rs = st.executeQuery(query);
				while(rs.next()) {
					byte data[];
					data=base64.decode(rs.getString(4));
					String tmp = new String(data);
					out += "    <item link=\"" + self_uri + "?"
						+ PhotoXSLT.normalize(tmp, true) + "\">"
						+ rs.getString(2) + "</item>\n";
				}
				// Cache for fifteen minutes.
				cache.store("saved_searches", out, 15*60*1000);
			} catch(Exception e) {
				log("Error getting search data, returning none:  " + e);
			} finally {
				if(photo != null) {
					freeDBConn(photo);
				}
			}
		}

		return(out);
	}

	// Find out if the authenticated user can add stuff.
	private boolean canadd() {
		return(user.canAdd());
	}

	// Add an image
	private String doAddPhoto() throws ServletException {
		String type=null;
		String userAgent=null;
		int id=-1;
		Connection photo=null;

		// Make sure the user can add.
		if(!canadd()) {
			log("User " + user + " has no permission to add.");
			throw new ServletException(
				"You are not allowed to add images.");
		}

		// We need a short lifetime for whatever page this produces
        long l=new java.util.Date().getTime();
        l+=10000L;
        response.setDateHeader("Expires", l);

		File f = multi.getFile("picture");

		// Get the upload meta data
		type = multi.getContentType("picture");
		userAgent=this.request.getHeader("User-Agent").toLowerCase();;

		// Check that it's the right file type.
		log("Type is " + type);
		if( type == null) {
			try {
				f.delete();
			} catch(Exception e) {
				log("Error deleting file " + f + ": "  + e);
			}
			// Throw an exception declaring the type to be bad.
			throw new ServletException(
				multi.getFilesystemName("picture")
				+ " NULL is a bad type, only image/jpeg or " +
					"image/pjpeg is accepted");
		}
		if ( 
				// netscape
				(type.startsWith("image/jpeg"))
				||
				// explorer
				(type.startsWith("image/pjpeg") && userAgent.indexOf("msie")>=0)   
			)  {
			// it will upload it then

		} else {
			try {
				f.delete();
			} catch(Exception e) {
				log("Error deleting file " + f + ": "  + e);
			}
			// Throw an exception declaring the type to be bad.
			throw new ServletException(
				multi.getFilesystemName("picture") + " ("
				+ type + ") is a bad type, only image/jpeg and "
				+ "image/pjpeg is accepted.  Your browser was "
				+ userAgent + ".");
		}

		// OK, things look good, let's try to store our data.
		try {
			FileInputStream in=null;
			String query=null;

			// Get the size from the file.
			int size=(int)f.length();

			// Encode the shit;
			int length=0;
			in = new FileInputStream(f);
			byte data[] = new byte[size];
			// Read in the data
			length=in.read(data);
			// If we didn't read enough data, give up.
			if(length!=size) {
				throw new Exception("Error reading enough data!");
			}
			PhotoImage photo_image=new PhotoImage(data);

			photo=getDBConn();
			photo.setAutoCommit(false);
			query = "insert into album(keywords, descr, cat, taken, size, "
				+ " addedby, ts, width, height, tn_width, tn_height)\n"
				+ "   values(?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0)";
			PreparedStatement st=photo.prepareStatement(query);
			// Toss in the parameters
			if (multi.getParameter("keywords") == null) {
				debug ("keywords null, setting to blank string");
				st.setString(1, "");
			} else {
				st.setString(1, multi.getParameter("keywords"));
			}

			if (multi.getParameter("info") == null) {
				debug ("info/desc null, setting to blank string");
				st.setString(2, "");
			} else {
				st.setString(2, multi.getParameter("info"));
			}

			st.setInt(3, Integer.parseInt(multi.getParameter("category")));
			st.setString(4, multi.getParameter("taken"));
			st.setInt(5, size);
			st.setInt(6, user.getId());
			st.setTimestamp(7,
				new java.sql.Timestamp(System.currentTimeMillis()));
			// Set the image width and height in the database.
			st.setInt(8, photo_image.width());
			st.setInt(9, photo_image.height());
			st.executeUpdate();

			query = "select currval('album_id_seq')\n";
			ResultSet rs = st.executeQuery(query);
			rs.next();
			id=rs.getInt(1);

			// Get a helper to store the data.
			PhotoImageHelper photo_helper=new PhotoImageHelper(id);
			photo_helper.storeImage(photo_image);

			// Log that the data was stored in the cache, so that, perhaps,
			// it can be permanently stored later on.
			query = "insert into upload_log (photo_id, wwwuser_id, ts)\n"
				+ "  values(?, ?, ?)\n";
			st=photo.prepareStatement(query);
			st.setInt(1, id);
			st.setInt(2, user.getId());
			st.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			st.executeUpdate();

			photo.commit();

			PhotoXML xml=new PhotoXML();
			xml.setTitle("Upload Succesful");
			xml.addBodyPart(getGlobalMeta());
			xml.addBodyPart("<upload_success>\n"
				+ "<id>" + id + "</id>"
				+ "</upload_success>");

			sendXML(xml.toString());
		} catch(Exception e) {
			log("Error adding new image to database:  " + e);
			try {
				photo.rollback();
			} catch(Exception e2) {
				log("Error rolling back and/or reporting add falure:  " + e2);
			}
			throw new ServletException("Error adding image", e);
		} finally {
			if(photo != null) {
				try {
					photo.setAutoCommit(true);
					freeDBConn(photo);
				} catch(Exception e) {
					log("Error cleaning up database after adding an image: "
						+ e);
				}
			}
			try {
				f.delete();
			} catch(Exception e) {
				log("Error removing temporary file after adding an image:  "
					+ e);
			}
		}

		return(null);
	}

	// Get a list of categories for a select list
	// Public because helpers use it
	public String getCatList(int def) {
		String out="";
		Connection photo=null;
		SpyCache cache=new SpyCache();

		String key="catList_" + def;

		out=(String)cache.get(key);
		// If we don't have it, build it and cache it.
		if(out==null) {
			out="";
			try {
				photo=getDBConn();

				String query = "select * from cat where id in\n"
			  		+ "(select cat from wwwacl where\n"
			  		+ "    userid=? or userid=?)\n"
			  		+ "order by name\n";

				PreparedStatement st = photo.prepareStatement(query);
				st.setInt(1, user.getId());
				st.setInt(2, PhotoUtil.getDefaultId());
				ResultSet rs = st.executeQuery();

				while(rs.next()) {
					int id=rs.getInt("id");
					String selected="";
					if(id==def) {
						selected=" selected=\"1\"";
					}
					out += "    <option value=\"" + id + "\"" + selected
						+ ">" + rs.getString("name") + "</option>\n";
				}
				// Cache it for five minutes
				cache.store(key, out, 5*60*1000);
			} catch(Exception e) {
				log("Error getting category list:  " + e);
			} finally {
					if(photo != null) {
						freeDBConn(photo);
					}
			}
			// Cache this for five minutes.
			cache.store(key, out, 5*60*1000);
		}
		return(out);
	}

	// Show the ``login'' form
	private String showCredForm() throws Exception {
		PhotoXML xml=new PhotoXML();
		xml.setTitle("Authentication Form");
		xml.addBodyPart(getGlobalMeta());
		xml.addBodyPart("<auth_form/>");
		sendXML(xml.toString());
		return(null);
	}

	// Show the new user form
	private String showNewUserForm() throws Exception {
		PhotoXML xml=new PhotoXML();
		xml.setTitle("New User Form");
		xml.addBodyPart(getGlobalMeta());
		xml.addBodyPart("<profile_add_user/>");
		sendXML(xml.toString());
		return(null);
	}

	private String addUser() throws Exception {
		String pass1=request.getParameter("password");
		String pass2=request.getParameter("pass2");
		String username=request.getParameter("username");

		if(pass1==null || pass2==null) {
			throw new Exception("Pass1 or Pass2 is null");
		}

		if(!pass1.equals(pass2)) {
			throw new Exception("Passwords don't match.");
		}

		// Load the profile
		Profile p=new Profile(request.getParameter("profile"));
		PhotoUser pu=security.getUser(username);
		if(pu!=null) {
			throw new Exception("User " + username + " already exists");
		}
		pu=new PhotoUser();

		// Set the fields
		pu.setUsername(username);
		pu.setPassword(pass1);
		pu.setRealname(request.getParameter("realname"));
		pu.setEmail(request.getParameter("email"));

		// Add the ACL entries
		for(Enumeration e=p.getACLEntries(); e.hasMoreElements();) {
			Integer i=(Integer)e.nextElement();
			pu.addACLEntry(i.intValue());
		}

		pu.save();
		setCreds();
		return(doIndex());
	}

	// Show the style form
	private String doStyleForm() throws ServletException {
		return(tokenize("presetstyle.inc", new Hashtable()));
	}

	// Get the stylesheet from the cookie, or the default.
	private String doGetStylesheet () throws ServletException {
		String output = null;
		int i;

		Cookie cookies[] = request.getCookies();

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
			log("Error producing stylesheet output:  " + e);
		}
		// We handled our own response.
		return(null);
	}

	// Set the style cookie from the POST data.
	private String doSetStyle() throws ServletException {
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

		stmp = request.getParameter("h_transform");
		if(stmp != null && stmp.length() > 1) {
			c_text += "h1,h2,h3,h4,h5 {text-transform: " + stmp + ";};\n";
		}

		// Create the cookie
		Cookie c = new Cookie("photo_style", URLEncoder.encode(c_text));
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
		return(stmp);
	}

	// Show the add an image form.
	private String doAddForm() throws Exception {

		PhotoXML xml=new PhotoXML();
		xml.setTitle("Add a Photo");
		xml.addBodyPart(getGlobalMeta());
		xml.addBodyPart("<add_form>\n"
			+ "\t<cat_list>\n"
			+ getCatList(-1)
			+ "\t</cat_list>\n"
			+ "\t<today>\n"
			+ PhotoUtil.getToday()
			+ "\t</today>\n"
			+ "</add_form>"
			);
		sendXML(xml.toString());

		return(null);
	}

	// Show the search form.
	private String doFindForm() throws Exception {

		PhotoXML xml=new PhotoXML();
		xml.setTitle("Find an Image");
		xml.addBodyPart(getGlobalMeta());
		xml.addBodyPart("<find_form>\n"
			+ "\t<cat_list>\n"
			+ getCatList(-1)
			+ "\t</cat_list>\n"
			+ "</find_form>"
			);
		sendXML(xml.toString());

		return(null);
	}

	// The change password form
	private String doChangePWForm() throws Exception {
		PhotoXML xml=new PhotoXML();
		xml.setTitle("Change Password Form");
		xml.addBodyPart(getGlobalMeta());
		xml.addBodyPart("<change_password_form/>");
		sendXML(xml.toString());
		return(null);
	}

	// Saving the changed password
	private String doChangePW() throws Exception {
		PhotoXML xml=new PhotoXML();
		xml.setTitle("Changed Password");
		xml.addBodyPart(getGlobalMeta());

		// Work on changing the password
		StringBuffer sb=new StringBuffer();
		sb.append("<changed_password>\n");

		String oldpw=request.getParameter("oldpw");
		String newp1=request.getParameter("newpw1");
		String newp2=request.getParameter("newpw2");

		if(oldpw==null || newp1==null || newp2==null) {
			throw new ServletException(
				"Password change form not filled out correctly");
		}

		// Verify the old password
		if(user.checkPassword(oldpw)) {
			if(newp1.equals(newp2)) {
				// Everything's OK, set it.
				user.setPassword(newp1);
				user.save();
				sb.append("<ok/>");
			} else {
				// New and old password don't match
				sb.append("<error>");
				sb.append("Passwords don't match.");
				sb.append("</error>");
			}
		} else {
			// The oldpw entered for the user is incorrect
			sb.append("<error>");
			sb.append("Invalid password for ");
			sb.append(user);
			sb.append("</error>");
		}

		sb.append("</changed_password>\n");

		// Add the above body part.
		xml.addBodyPart(sb.toString());

		sendXML(xml.toString());
		return(null);
	}

	// View categories
	private String doCatView() throws Exception {
		String catstuff="";
		Connection photo=null;

		try {
			photo = getDBConn();

			String query = "select name,id,catsum(id) as cs from cat\n"
			  	+ "where id in\n"
			  	+ "  (select cat from wwwacl where\n"
			  	+ "   userid=? or userid=?)\n"
			  	+ " order by cs desc";
			PreparedStatement st = photo.prepareStatement(query);
			st.setInt(1, user.getId());
			st.setInt(2, PhotoUtil.getDefaultId());
			ResultSet rs = st.executeQuery();

			while(rs.next()) {
				String t;
				if(rs.getInt(3)==1) {
					t = " image";
				} else {
					t = " images";
				}

				// Out of that, build the XML for the output.
				catstuff += "<cat_view_item>\n";
				catstuff += " <category>" + rs.getString(1) + "</category>\n";
				catstuff += " <cat_n>" + rs.getString(2) + "</cat_n>\n";
				catstuff += " <count>" + rs.getString(3) + "</count>\n";
				catstuff += " <qualifier>" + t + "</qualifier>\n";
				catstuff += "</cat_view_item>\n\n";
			}
		} catch(Exception e) {
			log("Error producing category view:  " + e);
		}
		finally { freeDBConn(photo); }

		PhotoXML xml=new PhotoXML();
		xml.setTitle("View Images by Category");
		xml.addBodyPart(getGlobalMeta());
		xml.addBodyPart(
			"<category_view>\n"
			+ catstuff
			+ "</category_view>\n"
			);

		sendXML(xml.toString());
		return(null);
	}

	// Some basic statistic stuff for the home page.
	private String getTotalImagesShown() throws Exception {
		String ret=null;
		String key="photo_total_images_shown";
		NumberFormat nf=NumberFormat.getNumberInstance();

		SpyCache cache=new SpyCache();

		ret=(String)cache.get(key);
		if(ret==null) {
			SpyDB db=new SpyDB(new PhotoConfig());
			ResultSet rs=db.executeQuery("select count(*) from photo_log");
			rs.next();
			ret=nf.format(rs.getInt(1));
			// Recalculate every hour
			cache.store(key, ret, 60*60*1000);
			db.close();
		}

		return(ret);
	}

	private String getTotalImages() throws Exception {
		String ret=null;
		String key="photo_total_images";
		NumberFormat nf=NumberFormat.getNumberInstance();

		SpyCache cache=new SpyCache();

		ret=(String)cache.get(key);
		if(ret==null) {
			SpyDB db=new SpyDB(new PhotoConfig());
			ResultSet rs=db.executeQuery("select count(*) from album");
			rs.next();
			ret=nf.format(rs.getInt(1));
			// Recalculate once a day
			cache.store(key, ret, 86400*1000);
			db.close();
		}
		return(ret);
	}

	// Display the index page.
	private String doIndex() throws Exception {
		String saved="";

		// Get the saved searches.
		try {
			saved=showSaved();
		} catch(Exception e) {
			log("Error getting saved search list:  " + e);
		}

		PhotoXML xml=new PhotoXML();
		xml.setTitle("My Photo Album");
		xml.addBodyPart(getGlobalMeta());
		xml.addBodyPart(
			"<index_page>\n"
			+ "<saved_searches>\n"
			+ saved
			+ "</saved_searches>\n"
			+ "</index_page>\n"
			);

		sendXML(xml.toString());

		return(null);
	}

	private void sendText(String tmplate, Hashtable h)
		throws ServletException {

		try {
			String txt=tokenize(tmplate, h);
			PrintWriter out=response.getWriter();
			out.print(txt);
			out.close();
		} catch(Exception e) {
			// nothing now
		}
	}

	// Get the global meta data
	private String getGlobalMeta() {
		StringBuffer sb=new StringBuffer();

		sb.append("<meta_stuff>\n");
		sb.append("  <self_uri>" + self_uri + "</self_uri>\n");
		sb.append(user.toXML());
		// gm+="  <username>" + user + "</username>\n";

		String tmp="";
		// Add some statistics on the index
		try {
			tmp=getTotalImagesShown();
		} catch(Exception e) {
			log("Error gathering global meta data:  " + e);
		}
		sb.append("<total_images_shown>" + tmp + "</total_images_shown>\n");
		try {
			tmp=getTotalImages();
		} catch(Exception e) {
			log("Error gathering global meta data:  " + e);
		}
		sb.append("<total_images>" + tmp + "</total_images>\n");
		// If the user has requested admin privs, set this flag.
		if(isAdmin()) {
			sb.append("<isadmin/>\n");
		}
		sb.append("</meta_stuff>\n");

		return(sb.toString());
	}

	// Send raw XML
	private void sendXML(String xml) throws Exception  {

		SpyCache cache=new SpyCache();

		if(xmlraw) {
			response.setContentType("text/plain");
		} else {
			response.setContentType("text/html");
		}

		// If they want raw XML, get it that way.
		if(xmlraw) {
			response.getWriter().print(xml);
			response.getWriter().close();
		} else {
			PhotoXSLT.sendXML(xml, xslt_stylesheet, response);
		}
	}

	// Get the UID
	private void getUid() throws ServletException {
		try {
			if(session==null) {
				user=security.getUser("guest");
			} else {
				user=(PhotoUser)session.getAttribute("photo_user");
				// If we have a session, but no username, add guest.
				if(user==null) {
					user=security.getUser("guest");
					session.setAttribute("photo_user", user);
					// make sure admin isn't set.
					unsetAdmin();
				}
			}
		} catch(Exception e) {
			log("Error getting user:  " + e);
			e.printStackTrace();
			throw new ServletException("Error initing uid", e);
		}
	}

	// Display dispatcher -- can be called from a helper
	public String doDisplay() throws Exception {
		String out="";
		String id=null;
		String search_id=null;

		id = request.getParameter("id");
		search_id = request.getParameter("search_id");

		// A search displayer thing may use this to add additional
		// information
		Hashtable h=new Hashtable();

		PhotoSearchResult r=null;

		try {
			if(id!=null) {
				r=doDisplayByID(h);
			} else if(search_id!=null) {
				r=doDisplayBySearchId(h);
			} else {
				throw new ServletException("No search id, and no search_id");
			}
		} catch(Exception e) {
			throw new ServletException("Error displaying image", e);
		}

		// Populate the hash with the image parts.
		r.addToHash(h);
		// Generate the XML
		String datachunk=r.showXML(self_uri);

		if(isAdmin()) {
			// Admin needs CATS
			int defcat=r.getCatNum();
			h.put("CATS", getCatList(defcat));
			out=tokenize("admin/display.inc", h);
		} else {

			StringBuffer meta=new StringBuffer();
			meta.append("<meta_stuff>\n");
			if(h.get("PREV")!=null) {
				meta.append("\t<prev>" + h.get("PREV") + "</prev>\n");
			}
			if(h.get("NEXT")!=null) {
				meta.append("\t<next>" + h.get("NEXT") + "</next>\n");
			}
			meta.append("</meta_stuff>\n");

			PhotoXML xml=new PhotoXML();
			xml.setTitle("Displaying " + h.get("IMAGE"));
			xml.addBodyPart(getGlobalMeta());
			xml.addBodyPart("<show_image>\n"
				+ meta
				+ datachunk
				+ "</show_image>\n"
				);

			sendXML(xml.toString());
			out=null;
		}

		return(out);
	}

	private PhotoSearchResult doDisplayBySearchId(Hashtable h)
		throws Exception {

		PhotoSearchResults results=null;
		results=(PhotoSearchResults)session.getAttribute("search_results");
		int which=Integer.parseInt(request.getParameter("search_id"));
		PhotoSearchResult r = results.get(which);

		// Add the PREV and NEXT button stuff, if applicable.
		if(results.nResults() > which+1) {
			h.put("NEXT", "" + (which+1));
		}

		if(which>0) {
			h.put("PREV", "" + (which-1));
		}
		return(r);
	}

	// Find and display images.
	private PhotoSearchResult doDisplayByID(Hashtable h) throws Exception {
		// Get the image_id.  We know it exists, because you can't get to
		// this function without one...of course, it may not parse.
		int image_id=Integer.parseInt(request.getParameter("id"));

		// Get the data
		PhotoSearchResult r=new PhotoSearchResult();
		r.find(image_id, user.getId());

		return(r);
	}

	// Send the response text...
	private void send_response(String text)
	{
		// set content type and other response header fields first
		response.setContentType("text/html");
		try {
			PrintWriter out = response.getWriter();

			String tail=null;
			SpyCache cache=new SpyCache();
			String key="t_tail_" + user.getId() + "." + isAdmin();
			tail=(String)cache.get(key);

			// If we didn't get a tail from the cache, build one.
			if(tail==null) {

				Hashtable ht=new Hashtable();
				if(isAdmin()) {
					ht.put("ADMIN_FLAG",
						" - <a href=\"" + self_uri
							+ "?func=unsetadmin\">Admin mode</a>");
				} else {
					ht.put("ADMIN_FLAG", "");
				}

				tail=tokenize("tail.inc", ht);
				// Store if for fifteen minutes.
				cache.store(key, tail, 15*60*1000);
			}

			// Print out the document and the tail together.
			out.print(text + tail);
			out.close();
		} catch(Exception e) {
			log("Error sending response:  " + e);
		}
	}

	// Display search results
	// This whole thing will fail if there's no session.
	private String displaySearchResults() throws Exception {
		if(session==null) {
			throw new ServletException("There's no session!");
		}
		PhotoSearchResults results=
			(PhotoSearchResults)session.getAttribute("search_results");
		if(results==null) {
			throw new ServletException("There are no search results!");
		}

		// We do the middle first, but, well, so what?
		StringBuffer middle=new StringBuffer();

		// Lock the results so the aheadfetcher can't mess us up once we
		// get going.
		synchronized(results) {
			int i=0;
			// if we have a starting point, let's start there.
			try {
				String startingS=request.getParameter("startfrom");
				if(startingS!=null) {
					int starting=Integer.parseInt(startingS);
					results.set(starting);
				}
			} catch(Exception e) {
				log("Error finding out starting point for search results:  "
					+ e);
			}

			middle.append("<search_result_row>\n");

			for(i=0; i<results.getMaxRet(); i++) {
				PhotoSearchResult r=results.next();
				if(r!=null) {
					// No, this really doesn't belong here.
					if( (i>0) && ((i) % 2) == 0) {
						middle.append("</search_result_row>");
						middle.append("<search_result_row>\n");
					}
					middle.append("<search_result>\n");
					middle.append(r.showXML(self_uri));
					middle.append("</search_result>\n");
				}
			}

			middle.append("</search_result_row>\n");
		}

		// Ask the aheadfetcher to prefetch the next x entries.
		aheadfetcher.next(results);

		StringBuffer meta=new StringBuffer();
		meta.append("<meta_stuff>\n");
		meta.append(linkToMore(results));
		meta.append("\t<total>");
		meta.append("" + results.nResults());
		meta.append("</total>\n");
		meta.append("\t<search_query>\n");
		meta.append((String)session.getAttribute("encoded_search"));
		meta.append("\t</search_query>\n");
		meta.append("</meta_stuff>\n");

		PhotoXML xml=new PhotoXML();
		xml.setTitle("Search Results");
		xml.addBodyPart(getGlobalMeta());
		xml.addBodyPart("<search_results_page>\n"
			+ meta
			+ "\t<search_results>\n"
			+ middle
			+ "\t</search_results>\n"
			+ "</search_results_page>\n"
			);
		sendXML(xml.toString());

		return(null);
	}

	// Find images.
	private String doFind() throws Exception {

		// Make sure there's a real session.
		if(session==null) {
			session=request.getSession(true);
		}

		try {
			PhotoSearch ps = new PhotoSearch();
			PhotoSearchResults results=null;
			// Get the results and put them in the mofo session
			results=ps.performSearch(request, user);
			session.setAttribute("search_results", results);
			session.setAttribute("encoded_search",
				ps.encodeSearch(request));
		} catch(Exception e) {
			log("Error performing search:  " + e);
		}

		return(displaySearchResults());
	}

	// Link to more search results
	private String linkToMore(PhotoSearchResults results) {
		String ret = null;
		int remaining=results.nRemaining();

		if(remaining>0) {
			// Decide how to display the button.  The button will read
			// ``Next x'' and x will either be the maximum search result
			// page size, or the number of remaining elements, whichever is
			// smaller.
			int nextwhu=results.getMaxRet();
			if(remaining<nextwhu) {
				nextwhu=remaining;
			}

			ret = "<linktomore>\n"
				+ "  <startfrom>" + results.current() + "</startfrom>\n"
				+ "  <remaining>" + remaining + "</remaining>\n"
				+ "  <nextpage>"  + nextwhu + "</nextpage>\n"
				+ "</linktomore>\n";
		} else {
			ret = "<linktomore>\n"
				+ "  <remaining>" + 0 + "</remaining>\n"
				+ "</linktomore>\n";
		}
		return(ret);
	}

	// Show an image
	private String showImage() throws ServletException {

		int which=-1;
		boolean thumbnail=false;
		ServletOutputStream out=null;

		String s = request.getParameter("photo_id");
		which = Integer.valueOf(s).intValue();

		s=request.getParameter("thumbnail");
		if(s!=null) {
			thumbnail=true;
		}

		try {
			// The new swank image extraction object.
			PhotoImageHelper p = new PhotoImageHelper(which);

			// Need a binary output thingy.
			out = response.getOutputStream();

			// Image data
			PhotoImage image=null;

			if(thumbnail) {
				log("Requesting thumbnail");

				// We're going to go ahead and allow the thumbnails to be
				// cached for an hour.
				long l=System.currentTimeMillis();
				// This is an hour in milliseconds.
				l+=3600000;
				response.setDateHeader("Expires", l);

				image=p.getThumbnail(user.getId());
			} else {
				log("Requesting full image");
				image=p.getImage(user.getId());
			}

			logger.log(new PhotoLogImageEntry(user.getId(),
				which, true, request));

			// OK, let the other side know this is going to be a jpeg.
			response.setContentType("image/jpeg");

			out.write(image.getData());

		} catch(Exception e) {
			throw new ServletException("Error displaying image", e);
		}
		// We handle our own response here, because this is an image.
		return(null);
	}

	private String doLogView() throws ServletException {
		String view, out="";
		PhotoLogView logview=null;

		try {
			logview=new PhotoLogView(this);
		} catch(Exception e) {
			throw new ServletException("Error displaying logs", e);
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
				throw new ServletException("Error displaying viewers", e);
			}
		}
		return(out);
	}

	// Set administrative privys
	private void setAdmin() throws ServletException {
		try {
			if(user.isInGroup("admin")) {
				log(user + " is in the admin group, setting admin");
				session.setAttribute("photo_is_admin", "iamthewalrus");
			}
		} catch(Exception e) {
			log("Error setting admin privs:  " + e);
		}
	}

	// Revoke administrative privys
	private void unsetAdmin() throws ServletException  {
		if(session!=null) {
			session.removeAttribute("photo_is_admin");
		}
	}

	// Returns true if the session is an admin session
	public boolean isAdmin() {
		boolean ret=false;
		if(session!=null) {
			String admin= (String)session.getAttribute("photo_is_admin");
			if(admin!=null) {
				log("photo_is_admin is set to " + admin);
				if(admin.equals("iamthewalrus")) {
					ret=true;
				}
			}
		}
		return(ret);
	}

	private void debug (String msg) {
		if (debug) {
			log("PhotoSession debug: " + msg);
		}
	}


	// Tokenize a template file and return the tokenized stuff.
	private String tokenize(String file, Hashtable vars)
		throws ServletException {
		String rv=PhotoUtil.tokenize(this, file, vars);
		if(rv==null) {
			throw new ServletException("Tokenizer returned null, perhaps the "
				+ " template " + file + " could not be found?");
		}
		return(rv);
	}

	/**
	 * Get the self URI.
	 */
	public String getSelfURI() {
		return(self_uri);
	}

	/**
	 * Get the remote user.
	 */
	public PhotoUser getUser() {
		return(user);
	}

}
