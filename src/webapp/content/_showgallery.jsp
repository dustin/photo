<%@ page import="net.spy.photo.*" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<%
	// Find the results
	PhotoSessionData sessionData=
		(PhotoSessionData)session.getAttribute("photoSession");
	User user=sessionData.getUser();
	int galleryId=Integer.parseInt(request.getParameter("id"));

	Gallery g=Gallery.getGallery(user, galleryId);
%>

<h1><%= g.getName() %></h1>

<logic:iterate id="pid" type="net.spy.photo.PhotoImage"
	collection="<%= g.getImages() %>">

	<%--
	<table align="left" cellpadding="0" width="300">
	<tr>
	<td><photo:imgLink id="<%= "" + pid.getId() %>"
		width="<%= "" + pid.getTnDims().getWidth() %>"
		height="<%= "" + pid.getTnDims().getHeight() %>"
		showThumbnail="true"/><br/><%= pid.getTaken() %></td>
	</tr>
	</table>
	--%>
	<photo:imgLink id="<%= "" + pid.getId() %>"
		width="<%= "" + pid.getTnDims().getWidth() %>"
		height="<%= "" + pid.getTnDims().getHeight() %>"
		showThumbnail="true"/>
</logic:iterate>
