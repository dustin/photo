<%@ page import="net.spy.photo.Category" %>
<%@ page import="net.spy.photo.Comment" %>
<%@ page import="net.spy.photo.PhotoSessionData" %>
<%@ page import="net.spy.photo.PhotoSearchResults" %>
<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%
	String imageId=request.getParameter("id");
	if(imageId==null) {
		imageId=request.getParameter("image_id");
	}
	String searchId=request.getParameter("search_id");
%>

<photo:getImage imageId="<%= imageId %>" searchId="<%= searchId %>">

	<table width="100%">
		<tr valign="top">
			<td align="left" width="10%">
				<photo:imgLink id="<%= 0 %>" relative="prev" searchId="<%= searchId %>">
					<photo:imgsrc alt="previous" border="0" url="/images/prev.png"/>
				</photo:imgLink>
			</td>

			<td align="center">
				<div class="displayBrief"><%= image.getDescr() %></div>
			</td>

			<td align="right" width="10%">
				<% if(searchId != null) { %>
					<photo:link url='<%= "/display.do?search_id=" + searchId %>'>
						<photo:imgsrc alt="pause" border="0" url="/images/pause.png"/>
					</photo:link>
					<photo:link url='<%= "/refreshDisplay.do?search_id=" + searchId %>'>
						<photo:imgsrc alt="slideshow" border="0" url="/images/play.png"/>
					</photo:link>
				<% } %>
				<photo:imgLink id="<%= 0 %>" relative="next" searchId="<%= searchId %>">
					<photo:imgsrc alt="next" border="0" url="/images/next.png"/>
				</photo:imgLink>
			</td>
		</tr>
	</table>

	<div align="center">
		<photo:imgSrc id="<%= "" + image.getId() %>"
			width="<%= "" + image.getScaledDims().getWidth() %>"
			height="<%= "" + image.getScaledDims().getHeight() %>"
			scale="true"/>
	</div>

	<p>
		<b>Category</b>:  ``<%= image.getCatName() %>''&nbsp;<b>Keywords</b>:
			<i><%= image.getKeywords() %></i><br>
		<b>Size</b>:  <%= image.getDimensions() %>
			(<%= image.getSize() %> bytes)<br>
		<b>Taken</b>:  <%= image.getTaken() %>&nbsp; <b>Added</b>:
			<%= image.getTimestamp() %>
		by <%= image.getAddedBy().getRealname() %><br>
		<b>Info</b>:
		<blockquote><%= image.getDescr()%></blockquote>

	</p>

<photo:guest negate="true">
	[<photo:link url="/logview.jsp" id="<%= "" + image.getId() %>">
			Who's seen this?
	</photo:link>] | 
	[<photo:link url="/addToGallery.do" id="<%= "" + image.getId() %>">
		Add to Gallery
	</photo:link>] | 
</photo:guest>
[<photo:imgLink id='<%= "" + image.getId() %>'>
	Linkable image
</photo:imgLink>] |
[<photo:link url='<%= "/PhotoServlet/" + image.getId() + ".jpg?id=" + image.getId() %>'>Full Size Image</photo:link>]
<%--
[<photo:imgLink id="<%= "" + image.getId() %>">Full Size Image</photo:imgLink>]
--%>


<p class="comments">

	<h1>Comments</h1>

	<logic:iterate id="comment"
		type="net.spy.photo.Comment"
		collection="<%= Comment.getCommentsForPhoto(image.getId()) %>">

		<table class="comments" width="100%">
			<tr class="comment_header">
				<td>At <%= comment.getTimestamp() %>,
					<%= comment.getUser().getRealname() %> said the
					following:
				</td>
			</tr>
			<tr class="comment_body">
				<td>
					<%= comment.getNote() %>
				</td>
			</tr>
		</table>
	</logic:iterate>

</p>

<p>

	<photo:guest negate="true">
		<bean:message key="display.comment"/><br/>
		<logic:messagesPresent>
			<bean:message key="errors.header"/>
			<ul>
				<html:messages id="error">
				<li><bean:write name="error"/></li>
				</html:messages>
			</ul><hr>
		</logic:messagesPresent>

		<html:form action="/addcomment">
			<html:errors/>
			<html:hidden property="imageId" value="<%= "" + image.getId() %>"/>
			<html:textarea property="comment" cols="50" rows="2"/>
			<br/>
			<html:submit>Add Comment</html:submit>
		</html:form>
	</photo:guest>

<p>

</p>

</photo:getImage>
