<%@ page import="javax.servlet.http.*" %>
<%@ page import="net.spy.photo.*" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%
	int id=Integer.parseInt(request.getParameter("id"));
%>

<p>

<center>
	<photo:imgLink id="<%= "" + id %>" showThumbnail='true'/>
</center>

</p>

<p>

<table width="100%" border="1">

<tr>
	<th>User</th>
	<th>IP</th>
	<th>User Agent</th>
	<th>Dimensions</th>
	<th>When</th>
</tr>

<logic:iterate id="log" type="net.spy.photo.PhotoLogViewEntry"
	collection="<%= PhotoLogViewEntry.getViewersOf(id) %>">

	<tr>
		<td><%= log.getUsername() %></td>
		<td><%= log.getRemoteAddr() %></td>
		<td><%= log.getUserAgent() %></td>
		<td><%= log.getImageSize() %></td>
		<td><%= log.getTimeStamp() %></td>
	</tr>

</logic:iterate>

</table>

</p>
