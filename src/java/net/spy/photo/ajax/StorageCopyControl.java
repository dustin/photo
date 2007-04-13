package net.spy.photo.ajax;

import javax.servlet.http.HttpServletRequest;

import net.spy.photo.Instantiator;
import net.spy.photo.PermanentStorage;
import net.spy.photo.StorageCopier;
import net.spy.photo.User;
import net.spy.xml.SAXAble;

public class StorageCopyControl extends PhotoAjaxServlet {
	@Override
	protected SAXAble getResults(HttpServletRequest request) throws Exception {
		User user=getUser(request);
		if(!user.isInRole(User.ADMIN)) {
			throw new Exception("Unauthorized");
		}
		StorageCopier rv=StorageCopier.getInstance();
		if(request.getPathInfo().equals("/start")) {
			String cName=request.getParameter("dest");
			getLogger().info("Requested class name:  %s", cName);
			Instantiator<PermanentStorage> c=
				new Instantiator<PermanentStorage>(cName, cName);
			PermanentStorage dest=c.getInstance();
			dest.init();
			getLogger().info("%s requesting a copy to %s", user, dest);
			rv.start(dest);
		}
		return(rv);
	}
}
