
package net.spy.photo.ajax;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;
import net.spy.xml.MapElement;
import net.spy.xml.SAXAble;

/**
 * Get the EXIF data for an image by path.
 */
public class EXIFServlet extends PhotoAjaxServlet {

	@Override
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
