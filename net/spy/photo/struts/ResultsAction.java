// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ResultsAction.java,v 1.2 2002/05/16 18:35:24 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

/**
 * Action to adjust a cursor before sending it to a result page.
 */
public class ResultsAction extends PhotoAction {

	/**
	 * Get an instance of ResultsAction.
	 */
	public ResultsAction() {
		super();
	}

    /**
     * Perform the adjustment (if any).
     */
    public ActionForward perform(ActionMapping mapping,
        ActionForm form,
        HttpServletRequest request,HttpServletResponse response)
        throws IOException, ServletException {

        CursorForm cf=(CursorForm)form;

        // Verify there's something to do.
        if(cf.getStartOffset()!=null && cf.getWhichCursor()!=null) {
            // Get the session data
            PhotoSessionData sessionData=getSessionData(request);
            // Get the cursoor
            Cursor c=null;
            if(cf.getWhichCursor().equals("results")) {
                c=sessionData.getResults();
            } else if(cf.getWhichCursor().equals("comments")) {
                c=sessionData.getComments();
            } else {
                throw new ServletException(
                    "Invalid cursor, how did this get through?");
            }

            // And set the offset.
            int offset=Integer.parseInt(cf.getStartOffset());
            c.set(offset);
        }

        return(mapping.findForward("success"));
    }

}
