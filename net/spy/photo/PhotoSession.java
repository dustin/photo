/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoSession.java,v 1.118 2002/05/15 08:26:15 dustin Exp $
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

	private boolean xmlraw=false;
	private boolean debug=true;

	private PhotoAheadFetcher aheadfetcher=null;

	// I use this enough that I might as well have it here.
	private PhotoConfig conf=null;

	// These are package scoped because they may be used by PhotoHelpers
	HttpServletRequest request=null;
	HttpServletResponse response=null;
	PhotoSessionData sessionData=null;

	public PhotoSession(PhotoServlet p,
		HttpServletRequest request,
		HttpServletResponse response) {

		photo_servlet=p;
		this.request=request;
		this.response=response;

		this.conf=new PhotoConfig();

		// The aheadfetcher
		this.aheadfetcher=Persistent.aheadfetcher;

		logger=Persistent.logger;
		security=Persistent.security;

		initSessionData(request);
		xmlraw=sessionData.isXmlraw();
	}

	// Initialize the session.
	private void initSessionData(HttpServletRequest request) {
		HttpSession session=request.getSession(true);

		sessionData=(PhotoSessionData)session.getAttribute("photoSession");

		// If we didn't get a session data, create a new one.
		if(sessionData==null) {
			// Get the object
			sessionData=new PhotoSessionData();
			// Initialize the user
			sessionData.setUser(security.getUser("guest"));
			// Initialize the optimal dimensions

			// Start with cookies
			Cookie cookies[] = request.getCookies();
			String dimss=null;
			if(cookies!=null) {
				for(int ci=0; ci<cookies.length && dimss == null; ci++) {
					String s = cookies[ci].getName();
					if(s.equalsIgnoreCase("photo_dims")) {
						dimss=cookies[ci].getValue();
					}
				}
			}
			// Figure out whether to get the dimensions from the cookie or
			// the config
			PhotoDimensions dim=null;
			if(dimss==null) {
				dim=new PhotoDimensionsImpl(
					conf.get("optimal_image_size", "800x600"));
			} else {
				dim=new PhotoDimensionsImpl(dimss);
				log("Loading dimensions from cookies:  " + dim);
			}
			// Stick it in the session.
			sessionData.setOptimalDimensions(dim);

			session.setAttribute("photoSession", sessionData);
		}
	}

	// This gets us back into the servlet engine's log.
	private void log(String whu) {
		photo_servlet.log(whu);
	}

	// Same here
	private void log(String whu, Exception e) {
		photo_servlet.log(whu, e);
	}

	// process a request
	public void process() throws ServletException, IOException {

		String func=null, type=null, out=null;

		try {
			type = request.getContentType();
			if(type != null && type.startsWith("multipart/form-data")) {
				// Get this to figure out the size and location of the uploads.
				multi = new MultipartRequest(request,
					conf.get("upload_tmp_dir", "/tmp"),
					conf.getInt("upload_max_size", (5*1024*1024)));
			} else {
				multi = null;
			}

			// Set the self_uri
			self_uri = request.getRequestURI();

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
			String msg=e.toString();
			if(e instanceof ServletException) {
				ServletException se=(ServletException)e;
				msg+=" -- " + se.getRootCause();
			}
			log("PhotoServlet Error", e);
			out=createError(e);
			try {
				sendXML(out);
			} catch(Exception e2) {
				log("Compound error:  We also failed to send back the error",
					e2);
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

		String msg=e.getMessage();
		if(e instanceof ServletException) {
			ServletException se=(ServletException)e;
			if(se.getRootCause()!=null
				&& se.getRootCause().getMessage()!=null) {

				msg+=" -- " + se.getRootCause().getMessage();
			}
		}

		// XML header
		rv.append("<?xml version=\"1.0\"?>\n");

		rv.append("<exception>\n");
		rv.append("\t<text>\n");
		rv.append("\t\t");
		rv.append(PhotoXSLT.normalize(msg, true));
		rv.append("\n");
		rv.append("\t</text>\n");
		rv.append("\t<exception_class>\n");
		rv.append("\t\t");
		rv.append(PhotoXSLT.normalize(e.getClass().getName(), true));
		rv.append("\n");
		rv.append("\t</exception_class>\n");
		rv.append("\t<stack>\n");
		for(Enumeration en=SpyUtil.getStackEnum(e, 1); en.hasMoreElements();) {
			rv.append("\t\t<stack_entry>\n");
			rv.append("\t\t\t");
			rv.append(PhotoXSLT.normalize(en.nextElement().toString(), true));
			rv.append("\n");
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
		log(sessionData.getUser() + " requested " + func);
		logger.log(new PhotoLogFuncEntry(sessionData.getUser().getId(),
			func, request));

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
		} else if(func.equals("xmlraw")) {
			setXMLRaw();
			out=doIndex();
		} else if(func.equals("forgotpassword")) {
			forgotPassword();
		} else if(func.equals("setcred")) {
			setCreds();
			out=doIndex();
		} else if(func.equals("setadmin")) {
			setAdmin();
			out=doIndex();
		} else if(func.equals("unsetadmin")) {
			unsetAdmin();
			out=doIndex();
		} else if(func.equals("comment")) {
			doComment();
			out=doDisplay();
		} else if(func.equals("listcomments")) {
			out=listComments();
		} else if(func.equals("setviewsize")) {
			out=setViewingSize();
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

	private String listComments() throws Exception {
		Cursor cursor=null;

		// Figure out if we need to reset or create a new comment list
		String fromScratch=request.getParameter("start");
		if( (fromScratch!=null) || (sessionData.getComments() == null)) {
			log("Loading new comment cursor for " + sessionData.getUser());
			Cursor comments=new Cursor(
				Comment.getAllComments(sessionData.getUser()));
			sessionData.setComments(comments);
		}
		cursor=sessionData.getComments();

		// if there's a startFrom listed, startFrom there.
		String startFromS=request.getParameter("startfrom");
		if(startFromS!=null) {
			int startFrom=Integer.parseInt(startFromS);
			cursor.set(startFrom);
		}

		// Grab some results.

		StringBuffer sb=new StringBuffer();
		sb.append("<all_comments>\n");

		for(int i=0; cursor.nRemaining() > 0 && i<cursor.getMaxRet(); i++) {
			XMLAble tmp=(XMLAble)cursor.next();
			sb.append(tmp.toXML());
		}

		sb.append("<meta_stuff>\n");
		sb.append(linkToMore(cursor));
		sb.append("\t<total>");
		sb.append("" + cursor.nResults());
		sb.append("</total>\n");
		sb.append("</meta_stuff>\n");

		sb.append("</all_comments>\n");

		PhotoXML xml=new PhotoXML();
		xml.setTitle("Comment List");
		xml.addBodyPart(getGlobalMeta());
		xml.addBodyPart(sb.toString());
		sendXML(xml.toString());

		return(null);
	}

	private String setViewingSize() throws Exception {
		String dimss=request.getParameter("dims");
		String out=null;

		if(dimss!=null) {
			PhotoDimensions dims=new PhotoDimensionsImpl(dimss);
			sessionData.setOptimalDimensions(dims);

			String remember=request.getParameter("remember");
			if(remember!=null) {
				// Make the cookie
				Cookie c=new Cookie("photo_dims", dimss);
				// Keep it for a year
				c.setMaxAge(365*86400);
				// Give it a comment.
				c.setComment("PhotoServlet optimal dimensions.");
				// Set the path
				c.setPath(self_uri);
				// Add it
				response.addCookie(c);
			}

			out=doIndex();
		} else {
			PhotoXML xml=new PhotoXML();
			xml.setTitle("Set Viewing Size");
			xml.addBodyPart(getGlobalMeta());
			xml.addBodyPart("<viewing_size_form/>");
			sendXML(xml.toString());
		}

		return(out);
	}

	private String saveSearch() throws ServletException {
		try {
			PhotoSearch ps = new PhotoSearch();
			ps.saveSearch(request, sessionData.getUser());

			PhotoXML xml=new PhotoXML();
			xml.setTitle("Saved Search");
			xml.addBodyPart(getGlobalMeta());
			xml.addBodyPart("<save_search_success/>");
			sendXML(xml.toString());

		} catch(Exception e) {
			throw new ServletException("Error saving search", e);
		}
		return(null);
	}

	// Set the stylesheet
	private void setStyleSheet() throws ServletException {
		String ss=request.getParameter("stylesheet");
		// Make sure something was passed in.
		if(ss!=null) {
			// Make it available for the session
			sessionData.setStylesheet(ss);
		}
	}

	// Set the stylesheet
	private void setXMLRaw() throws ServletException {
		String to=request.getParameter("to");
		// Make sure something was passed in.
		if(to!=null) {
			// Make it immediately available.
			xmlraw=Boolean.valueOf(to).booleanValue();
			// Make it available for the session
			sessionData.setXmlraw(xmlraw);
		}
	}

	private void forgotPassword() throws Exception {
		String username=request.getParameter("username");
		if(username==null) {
			throw new Exception("Username not provided.");
		}
		if(username.toLowerCase().equals("guest")) {
			throw new Exception("Cannot change the password of guest.");
		}
		PhotoUser pu=security.getUser(username);
		PhotoXML xml=new PhotoXML();

		if(pu==null) {
			xml.setTitle("No Such User");
			xml.addBodyPart(getGlobalMeta());
			xml.addBodyPart("<no_such_user>\n"
				+ "<id>" + username + "</id>"
				+ "</no_such_user>");
		} else {
			String newPass=PwGen.getPass(8);
			pu.setPassword(newPass);
			pu.save();

			Mailer m=new Mailer();
			m.setTo(pu.getEmail());
			m.setSubject("New Password for Photo Album");
			m.setBody("\n\nYour new password for " + pu.getUsername()
				+ " is " + newPass + "\n\n");
			m.send();
			log("Sent new password to " + pu.getEmail());
			xml.setTitle("Password Changed");
			xml.addBodyPart(getGlobalMeta());
			xml.addBodyPart("<generated_password>\n"
				+ "<username>" + pu.getUsername() + "</username>"
				+ "<email>" + pu.getEmail() + "</email>"
				+ "</generated_password>");
		}
		sendXML(xml.toString());
	}

	private void setCreds() throws Exception {
		String username=request.getParameter("username");
		String pass=request.getParameter("password");

		// Make sure we drop administrative privs if any
		unsetAdmin();

		PhotoUser user=security.getUser(username);
		if(user==null) {
			throw new ServletException(
				"Your username or password is incorrect.");
		}

		// We don't do anything unless the password is correct.
		if(user.checkPassword(pass)) {
			// Save the username.
			sessionData.setUser(user);

			PhotoLogEntry ple=new PhotoLogEntry(
				user.getId(), "Login", request);
			logger.log(ple);
			log("Authenticated as " + username);
		} else {
			PhotoLogEntry ple=new PhotoLogEntry(
				user.getId(), "AuthFail", request);
			logger.log(ple);
			log("AUTH FAILURE:  " + username);

			throw new ServletException(
				"Your username or password is incorrect.");
		}
	}

	// Get the saved searches.
	private String showSaved() {
		StringBuffer out=new StringBuffer();
		
		try {
			Collection c=SavedSearch.getSearches();
			for(Iterator i=c.iterator(); i.hasNext();) {
				SavedSearch s=(SavedSearch)i.next();
				out.append("    <item link=\"");
				out.append(self_uri);
				out.append("?");
				out.append(PhotoXSLT.normalize(s.getSearch(), true));
				out.append("\">");
				out.append(s.getName());
				out.append("</item>\n");
			}
		} catch(SQLException se) {
			log("Error getting saved searches, proceeding without.", se);
		}

		return(out.toString());
	}

	// Find out if the authenticated user can add stuff to the given category
	private boolean canadd(int cat) {
		return(sessionData.getUser().canAdd(cat));
	}

	// Add an image
	private String doAddPhoto() throws Exception {
		String type=null;
		String userAgent=null;
		int id=-1;

		int cat=Integer.parseInt(multi.getParameter("category"));

		// Make sure the user can add.
		if(!canadd(cat)) {
			log("User " + sessionData.getUser()
				+ " has no permission to add in the requested category.");
			throw new ServletException(
				"You are not allowed to add images here.");
		}

		// We need a short lifetime for whatever page this produces
        long l=new java.util.Date().getTime();
        l+=10000L;
        response.setDateHeader("Expires", l);

		File f = multi.getFile("picture");

		// Get the upload meta data
		type = multi.getContentType("picture");
		userAgent=this.request.getHeader("User-Agent").toLowerCase();;

		// Verify the file has a length
		if(f.length()==0) {
			f.delete();
			throw new ServletException(
				"I did not receive any data for the file you uploaded.  "
				+ "Perhaps you selected a file that doesn't exist?");
		}

		// OK, start by loading the image into a PhotoImage object.

		// Get the size from the file.
		int size=(int)f.length();
		// Encode the shit;
		int length=0;
		FileInputStream in = new FileInputStream(f);
		byte data[] = new byte[size];
		// Read in the data
		length=in.read(data);
		// If we didn't read enough data, give up.
		if(length!=size) {
			throw new Exception("Error reading enough data!");
		}
		PhotoImage photo_image=null;
		
		try {
			photo_image=new PhotoImage(data);
		} finally {
			f.delete();
		}

		// Make sure it has a type.
		log("Mime type is " + type + " format is "
			+ photo_image.getFormatString());

		SpyDB db=null;
		Connection photo=null;

		// Get the string data
		String keywords=multi.getParameter("keywords");
		if (keywords == null) {
			debug ("keywords null, setting to blank string");
			keywords = "";
		}

		String info=multi.getParameter("info");
		if (info == null) {
			debug ("info/desc null, setting to blank string");
			info = "";
		}

		// now check all the data components.
		try {
			StringBuffer test=new StringBuffer();
			test.append("<?xml version=\"1.0\"?>\n");
			test.append("<test>\n");
			test.append(info);
			test.append(keywords);
			test.append("\n</test>");

			PhotoXSLT.sendXML(test.toString(),
				null, new ByteArrayOutputStream());
		} catch (Exception ex) {
			throw new ServletException ("Problem with keywords/description, "+
				"potentially illegal character in description", ex);
		}

		// OK, things look good, let's try to store our data.
		try {
			String query=null;

			db=new SpyDB(conf);
			photo=db.getConn();
			photo.setAutoCommit(false);

			query = "insert into album(keywords, descr, cat, taken, size, "
				+ " addedby, ts, width, height)\n"
				+ "   values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement st=photo.prepareStatement(query);
			// Toss in the parameters
			st.setString(1, keywords);
			st.setString(2, info);

			st.setInt(3, cat);
			st.setString(4, multi.getParameter("taken"));
			st.setInt(5, size);
			st.setInt(6, sessionData.getUser().getId());
			st.setTimestamp(7,
				new java.sql.Timestamp(System.currentTimeMillis()));
			// Set the image width and height in the database.
			st.setInt(8, photo_image.getWidth());
			st.setInt(9, photo_image.getHeight());
			st.executeUpdate();

			query = "select currval('album_id_seq')\n";
			ResultSet rs = st.executeQuery(query);
			rs.next();
			id=rs.getInt(1);

			// Get a helper to store the data.
			PhotoImageHelper photo_helper=new PhotoImageHelper(id);
			photo_helper.storeImage(photo_image);

			photo.commit();

			// Log it
			logger.log(new PhotoLogUploadEntry(
				sessionData.getUser().getId(), id, request));

			PhotoXML xml=new PhotoXML();
			xml.setTitle("Upload Succesful");
			xml.addBodyPart(getGlobalMeta());
			xml.addBodyPart("<upload_success>\n"
				+ "<id>" + id + "</id>"
				+ "</upload_success>");

			sendXML(xml.toString());
		} catch(Exception e) {
			log("Error adding new image to database", e);
			try {
				photo.rollback();
			} catch(Exception e2) {
				log("Error rolling back", e2);
			}
			throw new ServletException("Error adding image", e);
		} finally {
			if(photo != null) {
				try {
					photo.setAutoCommit(true);
					db.close();
				} catch(Exception e) {
					log("Error cleaning up database after adding an image", e);
				}
			}
			f.delete();
		}

		return(null);
	}

	/**
	 * Get a list of categories for use in a select list.  Package scoped
	 * because helpers use it.
	 *
	 * @param def The ID of the default category.
	 * @param column The column to check for access ``canview'' or
	 * ``canadd''
	 */
	private String getCatList(int def, String column) {
		String out="";
		SpyCache cache=new SpyCache();

		String key="catList_" + column + "_"
			+ sessionData.getUser().getId() + "_" + def;

		out=(String)cache.get(key);
		// If we don't have it, build it and cache it.
		if(out==null) {
			out="";
			try {
				SpyDB photo=new SpyDB(conf);

				String query = "select * from cat where id in\n"
			  		+ "(select cat from wwwacl where\n"
			  		+ "    (userid=? or userid=?) "
					+ "     and " + column + "=true)\n"
			  		+ "order by name\n";

				PreparedStatement st = photo.prepareStatement(query);
				st.setInt(1, sessionData.getUser().getId());
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
				photo.close();
			} catch(Exception e) {
				log("Error getting category list, returning an empty one", e);
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

	// Verify a field in the request.
	private void verifyField(String field, int minlength)
		throws ServletException {

		String tmp=request.getParameter(field);
		if(tmp==null || tmp.length() < minlength) {
			throw new ServletException("Field ``" + field
				+ "'' must be at least " + minlength + " characters long.");
		}
	}

	// Add a user based on a given profile.
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

		// Check the password length
		verifyField("password", 6);
		verifyField("username", 3);
		verifyField("realname", 3);
		// I figure it won't get shorter than d@spy.net
		verifyField("email", 9);

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
			pu.addViewACLEntry(i.intValue());
		}

		pu.save();
		setCreds();

		// OK, let's try to log it.
		try {
			SpyDB db=new SpyDB(conf);
			PreparedStatement pst=db.prepareStatement(
				"insert into user_profile_log"
				+ "(profile_id, wwwuser_id, remote_addr) "
				+ "values(?,?,?)");
			pst.setInt(1, p.getId());
			pst.setInt(2, pu.getId());
			pst.setString(3, request.getRemoteAddr());
			pst.executeUpdate();
			pst.close();
			db.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

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
			throw new ServletException("Error producing stylesheet output", e);
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
			+ getCatList(-1, "canadd")
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
			+ getCatList(-1, "canview")
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

		verifyField("oldpw", 1);
		verifyField("newpw1", 6);
		verifyField("newpw2", 6);

		// Verify the old password
		if(sessionData.getUser().checkPassword(oldpw)) {
			if(newp1.equals(newp2)) {
				// Everything's OK, set it.
				sessionData.getUser().setPassword(newp1);
				sessionData.getUser().save();
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
			sb.append(sessionData.getUser());
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

		try {
			SpyDB photo = new SpyDB(conf);

			String query = "select name,id,catsum(id) as cs from cat\n"
			  	+ "where id in\n"
			  	+ "  (select cat from wwwacl where\n"
			  	+ "   (userid=? or userid=?) and canview=true)\n"
			  	+ " order by cs desc";
			PreparedStatement st = photo.prepareStatement(query);
			st.setInt(1, sessionData.getUser().getId());
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
			photo.close();
		} catch(Exception e) {
			throw new ServletException("Error producing category view", e);
		}

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
			SpyDB db=new SpyDB(conf);
			ResultSet rs=db.executeQuery(
				"select count(*) from photo_logs\n"
					+ " where log_type=get_log_type('ImgView')");
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
			SpyDB db=new SpyDB(conf);
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
			log("Error getting saved search list, it will be empty", e);
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
	String getGlobalMeta() {
		StringBuffer sb=new StringBuffer();

		sb.append("<meta_stuff>\n");
		sb.append("  <self_uri>" + self_uri + "</self_uri>\n");
		sb.append(sessionData.getUser().toXML());
		// gm+="  <username>" + user + "</username>\n";

		String tmp="";
		// Add some statistics on the index
		try {
			tmp=getTotalImagesShown();
		} catch(Exception e) {
			log("Error gathering global meta data, "
				+ "total images shown will be empty", e);
		}
		sb.append("<total_images_shown>" + tmp + "</total_images_shown>\n");
		try {
			tmp=getTotalImages();
		} catch(Exception e) {
			log("Error gathering global meta data, total images will be empty",
				e);
		}
		sb.append("<total_images>" + tmp + "</total_images>\n");
		// If the user has requested admin privs, set this flag.
		if(isAdmin()) {
			sb.append("<isadmin/>\n");
		}
		if(isSubAdmin()) {
			sb.append("<issubadmin/>\n");
		}
		if(xmlraw) {
			sb.append("<xmlraw/>\n");
		}
		sb.append("</meta_stuff>\n");

		return(sb.toString());
	}

	// Send raw XML
	void sendXML(String xml) throws Exception  {

		SpyCache cache=new SpyCache();

		if(xmlraw) {
			response.setContentType("text/xml");
		} else {
			response.setContentType("text/html");
		}

		// If they want raw XML, get it that way.
		if(xmlraw) {
			response.getWriter().print(xml);
			response.getWriter().close();
		} else {
			PhotoXSLT.sendXML(xml, sessionData.getStylesheet(), response);
		}
	}

	private void doComment() throws Exception {
		String s=request.getParameter("image_id");
		if(s==null) {
			throw new Exception("image_id not given.");
		}
		int photo_id=Integer.parseInt(s);

		String note=request.getParameter("comment");
		if(note==null || note.length()<2) {
			throw new Exception("Note not provided.");
		}

		// Verify the person has access to this image
		security.checkAccess(sessionData.getUser(), photo_id);

		Comment comment=new Comment();
		comment.setUser(sessionData.getUser());
		comment.setPhotoId(photo_id);
		comment.setRemoteAddr(request.getRemoteAddr());
		comment.setNote(note);
		comment.save();

		log("Saved note from " + sessionData.getUser());
	}

	// Display dispatcher -- can be called from a helper
	/**
	 * Display an image by ID or search ID.  This may be called by helpers
	 * (hence the package scope).  The parameters ``id'' or ``image_id''
	 * will be used, if provided, else a ``search_id'' is expected to
	 * provide the offset in the current search cursor.
	 */
	String doDisplay() throws Exception {
		String out="";
		String id=null;
		String search_id=null;

		// Allow `id' or `image_id' to be used.
		id = request.getParameter("id");
		if(id==null) {
			id = request.getParameter("image_id");
		}
		search_id = request.getParameter("search_id");

		// A search displayer thing may use this to add additional
		// information
		Hashtable h=new Hashtable();

		PhotoSearchResult r=null;

		try {
			if(id!=null) {
				r=doDisplayByID(Integer.parseInt(id), h);
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

		if(isAdmin() || isSubAdmin()) {
			// Admin needs CATS
			int defcat=r.getCatNum();
			h.put("CATS", getCatList(defcat, "canadd"));
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
			// Add the last search result ID, for navigation purposes.
			PhotoSearchResults results=sessionData.getResults();
			if(results!=null) {
				meta.append("\t<last>" + (results.nResults()-1) + "</last>\n");
			}
			meta.append("</meta_stuff>\n");

			StringBuffer comments=new StringBuffer();
			comments.append("<comments>\n");
			for(Enumeration e=Comment.getCommentsForPhoto(r.getImageId());
				e.hasMoreElements();) {

				Comment c=(Comment)e.nextElement();
				comments.append(c.toXML());
			}
			comments.append("</comments>\n");

			PhotoXML xml=new PhotoXML();
			xml.setTitle("Displaying " + h.get("IMAGE"));
			xml.addBodyPart(getGlobalMeta());
			xml.addBodyPart("<show_image>\n"
				+ meta
				+ datachunk
				+ comments
				+ "</show_image>\n"
				);

			sendXML(xml.toString());
			out=null;
		}

		return(out);
	}

	private PhotoSearchResult doDisplayBySearchId(Hashtable h)
		throws Exception {

		PhotoSearchResults results=sessionData.getResults();
		if(results==null) {
			throw new ServletException("No results in session.");
		}
		int which=Integer.parseInt(request.getParameter("search_id"));
		PhotoSearchResult r = (PhotoSearchResult)results.get(which);
		if(r==null) {
			throw new ServletException("No result in session.");
		}

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
	private PhotoSearchResult doDisplayByID(int image_id, Hashtable h)
		throws Exception {

		// Check access
		security.checkAccess(sessionData.getUser(), image_id);
		// Get the data
		PhotoSearchResult r=new PhotoSearchResult();
		// Fetch up the image
		r.find(image_id);
		// Set the scaling stuff.
		r.setMaxSize(sessionData.getOptimalDimensions());

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
			String key="t_tail_" + sessionData.getUser().getId() + "." + isAdmin();
			tail=(String)cache.get(key);

			// If we didn't get a tail from the cache, build one.
			if(tail==null) {

				Hashtable ht=new Hashtable();
				if(isAdmin() || isSubAdmin()) {
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
			log("Error sending response", e);
		}
	}

	// Display search results
	// This whole thing will fail if there's no session.
	private String displaySearchResults() throws Exception {
		PhotoSearchResults results=sessionData.getResults();
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
				log("Error finding out starting point for search results", e);
			}

			middle.append("<search_result_row>\n");

			for(i=0; i<results.getMaxRet(); i++) {
				PhotoSearchResult r=(PhotoSearchResult)results.next();
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

		StringBuffer meta=new StringBuffer();
		meta.append("<meta_stuff>\n");
		meta.append(linkToMore(results));
		meta.append("\t<total>");
		meta.append("" + results.nResults());
		meta.append("</total>\n");
		meta.append("\t<search_query>\n");
		meta.append(sessionData.getEncodedSearch());
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

		// Ask the aheadfetcher to prefetch the next x entries.
		aheadfetcher.next(results);

		return(null);
	}

	// Find images.
	private String doFind() throws Exception {

		PhotoSearch ps = new PhotoSearch();
		PhotoSearchResults results=null;
		// Get the results and put them in the mofo session
		results=ps.performSearch(request, sessionData);
		sessionData.setResults(results);
		sessionData.setEncodedSearch(ps.encodeSearch(request));

		return(displaySearchResults());
	}

	// Link to more search results
	private String linkToMore(Cursor results) {
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
		String size=null;
		ServletOutputStream out=null;

		verifyField("photo_id", 1);
		String s = request.getParameter("photo_id");
		which = Integer.valueOf(s).intValue();

		s=request.getParameter("thumbnail");
		if(s!=null) {
			size=conf.get("thumbnail_size");
		}
		s=request.getParameter("scale");
		if(s!=null) {
			size=s;
		}

		try {
			// The new swank image extraction object.
			PhotoImageHelper p = new PhotoImageHelper(which);

			// Need a binary output thingy.
			out = response.getOutputStream();

			// Image data
			PhotoImage image=null;

			// Allow all images to be cached for a bit.
			long l=System.currentTimeMillis();
			// This is ten minutes in milliseconds.

			// I'm disabling this for a bit...thanks to resin, it bypasses
			// ACLS.
			// l+=600000;
			// response.setDateHeader("Expires", l);

			PhotoDimensions pdim=null;
			if(size!=null) {
				pdim=new PhotoDimensionsImpl(size);
			}
			log("Fetching " + which + " scaled to " + pdim);
			image=p.getImage(sessionData.getUser(), pdim);

			logger.log(new PhotoLogImageEntry(sessionData.getUser().getId(),
				which, pdim, request));

			// OK, let the other side know what to expect
			response.setContentType("image/" + image.getFormatString());

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

		verifyField("view", 1);
		view=request.getParameter("view");

		if(view.equalsIgnoreCase("viewers")) {
			verifyField("which", 1);
			String which_s=request.getParameter("which");
			int which=Integer.parseInt(which_s);

			// Verify access
			try {
				PhotoSecurity.checkAccess(sessionData.getUser(), which);
			} catch(Exception e) {
				// Log it separately so the user doesn't see the security
				// details.
				log("Access denied viewing logs", e);
				throw new ServletException("Access denied.");
			}

			try {
				out=logview.getViewersOf(which);
			} catch(Exception e) {
				throw new ServletException("Error displaying viewers", e);
			}
		}
		return(out);
	}

	// Set administrative privys
	private void setAdmin() throws ServletException {
		try {
			if(sessionData.getUser().isInGroup("admin")) {
				log(sessionData.getUser()
					+ " is in the admin group, setting admin");
				sessionData.setAdmin(PhotoSessionData.ADMIN);
			} else if(sessionData.getUser().isInGroup("subadmin")) {
				log(sessionData.getUser()
					+ " is in the subadmin group, setting subadmin");
				sessionData.setAdmin(PhotoSessionData.SUBADMIN);
			}
		} catch(Exception e) {
			throw new ServletException("Error setting admin privs", e);
		}
	}

	// Revoke administrative privys
	private void unsetAdmin() throws ServletException  {
		sessionData.setAdmin(PhotoSessionData.NOADMIN);
	}

	/**
	 * Return true if this session has the admin flag set.  May be called
	 * by helpers, hence the package scope.
	 */
	boolean isAdmin() {
		return(sessionData.checkAdminFlag(PhotoSessionData.ADMIN));
	}

	/**
	 * Return true if this session has the subadmin flag set.  May be called
	 * by helpers, hence the package scope.
	 */
	boolean isSubAdmin() {
		return(sessionData.checkAdminFlag(PhotoSessionData.SUBADMIN));
	}

	// deeezbug
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
	String getSelfURI() {
		return(self_uri);
	}

	/**
	 * Get the remote user.  This can be called by helpers, hence the
	 * package scope.
	 */
	PhotoUser getUser() {
		return(sessionData.getUser());
	}

}
