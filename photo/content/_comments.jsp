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

<p>

<table width="100%">

<logic:iterate id="comments" collection="<%= allcomments %>"
	type="net.spy.photo.GroupedComments"
	length="6">

	<tr valign="top">
		<td class="commentimage">
			<photo:imgLink showThumbnail="true"
				id="<%= comments.getPhotoId() %>"/>
		</td>
		<td>
			<table class="comments" width="100%">
				<logic:iterate id="comment"
					type="net.spy.photo.Comment"
					collection="<%= comments %>">
					<tr valign="top" class="commentheader">
						<td>At <%= comment.getTimestamp() %>
							<%= comment.getUser().getRealname() %>
							said the following:
						</td>
					</tr>
					<tr valign="top" class="commentbody">
						<td>
							<%= comment.getNote() %>
						</td>
					</tr>
				</logic:iterate>
				<% if(comments.hasMore()) { %>
					<tr class="commentmore">
						<td>
							<photo:imgLink id="<%= comments.getPhotoId() %>">
								More comments available on the image page.
							</photo:imgLink>
						</td>
					</tr>
				<% } %>
			</table>
		</td>
	</tr>

</logic:iterate>

</table>

</p>

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
        <html:submit>Next <%= nextWhu %></html:submit>
      </html:form>
      </p>
    <%
  }
%>
