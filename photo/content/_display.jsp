<%@ page import="net.spy.photo.Category" %>
<%@ page import="net.spy.photo.Comment" %>
<%@ page import="net.spy.photo.PhotoSessionData" %>
<%@ page import="net.spy.photo.PhotoSearchResults" %>
<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
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
				<photo:imgLink id="<%= 0 %>"
					relative="prev" searchId="<%= searchId %>">
					<img border="0" src="/~dustin/images/l_arrow.gif"/>
				</photo:imgLink>
			</td>

			<td align="center">
				<div class="displayBrief"><%= image.getDescr() %></div>
			</td>

			<td align="right" width="10%">
				<photo:imgLink id="<%= 0 %>"
					relative="next" searchId="<%= searchId %>">
					<img border="0" src="/~dustin/images/r_arrow.gif"/>
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
		by <%= image.getAddedBy() %><br>
		<b>Info</b>:
		<blockquote><%= image.getDescr()%></blockquote>

	</p>

<photo:guest negate="true">
	[<a href="logview.jsp?id=<%= "" + image.getId() %>"> Who's seen this?</a>] | 
</photo:guest>
[<a href="display.jsp?id=<%= "" + image.getId() %>">Linkable image</a>] |
[<a href="PhotoServlet?func=getimage&photo_id=<%= "" + image.getId() %>">Full
Size Image</a>]


<p class="comments">

	<h1>Comments</h1>

	<logic:iterate id="i"
		collection="<%= Comment.getCommentsForPhoto(image.getId()) %>">

		<% Comment comment=(Comment)i; %>

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

	Submit a comment:<br/>

	<photo:guest negate="true">
		<form method="POST" action="PhotoServlet">
			<input type="hidden" name="func" value="comment"/>
			<input type="hidden" name="image_id"
				value="<%= "" + image.getId() %>"/>
			<textarea name="comment" wrap="hard" cols="50" rows="2"></textarea>
			<br/>
			<input type="submit" value="Comment"/>
		</form>
	</photo:guest>

<p>

</p>

</photo:getImage>
