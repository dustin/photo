<%@ page import="net.spy.photo.*" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

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
      <p>
      <html:form action="nextcomments.do">
        <html:hidden property="startOffset" value="<%= nextOffsetS %>"/>
        <html:hidden property="whichCursor" value="comments"/>
        <html:submit styleClass="button">Next <%= nextWhu %></html:submit>
      </html:form>
      </p>
    <%
  }
%>
