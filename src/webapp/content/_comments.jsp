<%@ page import="net.spy.photo.*" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<%
	// Find the comments
	PhotoSessionData sessionData=
		(PhotoSessionData)session.getAttribute("photoSession");
	Cursor allcomments=sessionData.getComments();
	if(allcomments==null) {
		throw new ServletException("There is no comment cursor!");
	}
%>

<div class="comments">
	<logic:iterate id="comments" collection="<%= allcomments %>"
		type="net.spy.photo.GroupedComments"
		length="6">

		<div class="commentblock">
			<div class="commentimage">
				<photo:imgLink showThumbnail="true" id="<%= comments.getPhotoId() %>"/>
			</div>
			<logic:iterate id="comment"
				type="net.spy.photo.Comment"
				collection="<%= comments %>">
				<div class="commentheader">
					At <%= comment.getTimestamp() %>
						<%= comment.getUser().getRealname() %>
						said the following:
				</div>
				<div class="commentbody">
					<%= comment.getNote() %>
				</div>
			</logic:iterate>
			<% if(comments.hasMore()) { %>
				<div class="commentmore">
					<photo:imgLink id="<%= comments.getPhotoId() %>">
						More comments available on the image page.
					</photo:imgLink>
				</div>
			<% } %>
		</div>

	</logic:iterate>

</div>

<%
  // Figure out if there are any more.
  if(allcomments.hasMoreElements()) {
    int nextOffset=allcomments.current();
    String nextOffsetS="" + nextOffset;
    int remaining=allcomments.nRemaining();
    int nextWhu=allcomments.getMaxRet();
    if(remaining<nextWhu) {
      nextWhu=remaining;
    }

    %>
      <%= remaining %> comments remaining.
      <html:form action="nextcomments.do">
				<p>
        	<html:hidden property="startOffset" value="<%= nextOffsetS %>"/>
        	<html:hidden property="whichCursor" value="comments"/>
        	<html:submit>Next <%= nextWhu %></html:submit>
				</p>
      </html:form>
    <%
  }
%>
<%-- arch-tag: AB06DECA-5D6F-11D9-AD6F-000A957659CC --%>
