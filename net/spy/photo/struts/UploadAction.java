// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: UploadAction.java,v 1.2 2002/05/18 07:30:11 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.*;

import net.spy.photo.*;

/**
 * The action performed when an image is uploaded.
 */
public class UploadAction extends PhotoAction {

	/**
	 * Get an instance of UploadAction.
	 */
	public UploadAction() {
		super();
	}

	/**
	 * Process the upload.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		UploadForm uf=(UploadForm)form;

		// Get the session data.
		PhotoSessionData sessionData=getSessionData(request);

		// Verify upload permission.
		int cat=Integer.parseInt(uf.getCategory());
		if(!sessionData.getUser().canAdd(cat)) {
			throw new ServletException(
				sessionData.getUser() + " can't add to category " + cat);
		}

		// Save the image.
		SpyDB db=null;
		Connection conn=null;
		try {
			String query=null;

			db=new SpyDB(new PhotoConfig());
			conn=db.getConn();
			conn.setAutoCommit(false);

			// First build the query to store the image.
			query = "insert into album(keywords, descr, cat, taken, size, "
				+ " addedby, ts, width, height)\n"
				+ "   values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement st=conn.prepareStatement(query);

			st.setString(1, uf.getKeywords());
			st.setString(2, uf.getInfo());
			st.setInt(3, Integer.parseInt(uf.getCategory()));
			st.setString(4, uf.getTaken());
			st.setInt(5, uf.getPhotoImage().size());
			st.setInt(6, sessionData.getUser().getId());
			st.setTimestamp(7,
				new java.sql.Timestamp(System.currentTimeMillis()));
			st.setInt(8, uf.getPhotoImage().getWidth());
			st.setInt(9, uf.getPhotoImage().getHeight());
			st.executeUpdate();

			// Get the image ID
			query = "select currval('album_id_seq')";
			ResultSet rs = st.executeQuery(query);
			rs.next();
			int id=rs.getInt(1);

			// Store the actual image data.
			PhotoImageHelper photo_helper=new PhotoImageHelper(id);
			photo_helper.storeImage(uf.getPhotoImage());

			// Image is stored, commit.
			conn.commit();

			// Log the new image
			Persistent.logger.log(new PhotoLogUploadEntry(
				sessionData.getUser().getId(), id, request));

			// Before we return, make the ID available to the next handler
			request.setAttribute("net.spy.photo.UploadID", new Integer(id));

		} catch(Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		} finally {
			if(conn!=null) {
				try {
					conn.setAutoCommit(true);
					db.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		return(mapping.findForward("success"));
	}

}
