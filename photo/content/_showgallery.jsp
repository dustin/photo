<%@ page import="net.spy.photo.*" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%
	// Find the results
	PhotoSessionData sessionData=
		(PhotoSessionData)session.getAttribute("photoSession");
	PhotoUser user=sessionData.getUser();
	int galleryId=Integer.parseInt(request.getParameter("id"));

	Gallery g=Gallery.getGallery(user, galleryId);
%>

<div class="sectionheader"><%= g.getName() %></div>

<logic:iterate id="pid" type="net.spy.photo.PhotoImageData"
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
<%-- arch-tag: B7BEEA69-5D6F-11D9-8EBD-000A957659CC --%>
