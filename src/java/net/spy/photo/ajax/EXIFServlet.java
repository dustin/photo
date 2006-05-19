// arch-tag: 9B7C63E8-8D4E-46B2-9D9B-2224BFD88ED3

package net.spy.photo.ajax;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.spy.jwebkit.xml.MapElement;
import net.spy.jwebkit.xml.SAXAble;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;

/**
 * Get the EXIF data for an image by path.
 */
public class EXIFServlet extends PhotoAjaxServlet {

	protected SAXAble getResults(HttpServletRequest request) throws Exception {
		int id=Integer.parseInt(request.getPathInfo().substring(1));
		PhotoSessionData ses=getSessionData(request);
		User user=ses.getUser();

		// Check the access
		Persistent.getSecurity().checkAccess(user, id);

		// Get the image to get the EXIF data to display
		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
		PhotoImageData img=pidf.getObject(id);
		Map<String, Object> metaData=img.getMetaData();
		return new MapElement("exif", "tag", metaData);
	}
}
