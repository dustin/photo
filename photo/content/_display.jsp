<%@ page import="net.spy.photo.Category" %>
<%@ page import="net.spy.photo.Comment" %>
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
			<td align="left" width="10%"></td>
			<td align="center">
				<div class="displayBrief"><%= image.getDescr() %></div>
			</td>
			<td
			align="right"
			width="10%"></td>
		</tr>
	</table>

	<div align="center">
		<photo:imgSrc id="<%= image.getImage() %>"
			width="<%= image.getScaledWidth() %>"
			height="<%= image.getScaledHeight() %>"
			scale="true"/>
	</div>

	<%--
	<photo:imgLink id="<%= image.getId() %>"/>
	<img border="0" src="PhotoServlet?func=getimage&id=<%= image.getId()%>"/>
	--%>

	<p>
		<b>Category</b>:  ``<%= image.getCat() %>''&nbsp;<b>Keywords</b>:
			<i><%= image.getKeywords() %></i><br>
		<b>Size</b>:  <%= image.getWidth() %>x<%= image.getHeight() %>
			(<%= image.getSize() %> bytes)<br>
		<b>Taken</b>:  <%= image.getTaken() %>&nbsp; <b>Added</b>:
			<%= image.getTs() %>
		by <%= image.getAddedBy() %><br>
		<b>Info</b>:
		<blockquote><%= image.getDescr()%></blockquote>

	</p>

[<a href="PhotoServlet?func=logview&view=viewers&which=<%= image.getImage() %>">
Who's seen this?</a>] | 
[<a
href="display.jsp?id=<%= image.getImage() %>">Linkable image</a>] |
[<a href="PhotoServlet?func=getimage&photo_id=<%= image.getImage() %>">Full
Size Image</a>]


<p class="comments">

	<h1>Comments</h1>

	<logic:iterate id="i"
		collection="<%= Comment.getCommentsForPhoto(Integer.parseInt(image.getImage())) %>">

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

	<photo:guest negate="1">
		<form method="POST" action="PhotoServlet">
			<input type="hidden" name="func" value="comment"/>
			<input type="hidden" name="image_id"
				value="<%= image.getImage() %>"/>
			<textarea name="comment" wrap="hard" cols="50" rows="2"></textarea>
			<br/>
			<input type="submit" value="Comment"/>
		</form>
	</photo:guest>

<p>

</p>

</photo:getImage>
