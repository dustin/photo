<%@ page import="net.spy.photo.Category" %>
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


</photo:getImage>
