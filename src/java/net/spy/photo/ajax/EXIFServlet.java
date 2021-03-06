
package net.spy.photo.ajax;

import javax.servlet.http.HttpServletRequest;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageFactory;
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
		PhotoImageFactory pidf=PhotoImageFactory.getInstance();
		PhotoImage img=pidf.getObject(id);
		return new MapElement("exif", "tag", img.getMetaData());
	}
}
