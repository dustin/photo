<%@ page import="javax.servlet.http.*" %>
<%@ page import="net.spy.photo.*" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%-- Need a base64 encoder --%>
<jsp:useBean id="base64" class="net.spy.util.Base64" scope="application"/>

<p>

<table width="100%" border="1">

<tr>
	<th>User</th>
	<th>Login Time</th>
	<th>Idle(s)</th>
	<th>Images Served</th>
	<th>Most Recent</th>
</tr>

<logic:iterate id="ses" type="javax.servlet.http.HttpSession"
	collection="<%= SessionWatcher.listSessions() %>">

	<% PhotoSessionData sessionData=
		(PhotoSessionData)ses.getAttribute("photoSession"); %>
	<% User user=sessionData.getUser(); %>

	<tr>
		<td><%= user.getRealname() %> (<%= user.getName() %>)</td>
		<td><%= new java.util.Date(ses.getCreationTime()) %></td>
		<td><%= (double)(System.currentTimeMillis()
			- ses.getLastAccessedTime())/1000.0 %></td>
		<td><%= sessionData.getImagesSeen() %></td>
		<td>
			<% if(sessionData.getLastImageSeen() != 0) { %>
				<photo:imgLink id="<%= sessionData.getLastImageSeen() %>"
					alt='<%= "Image " + sessionData.getLastImageSeen() %>'
					showThumbnail='true'/>
				<%
					String theUrl="/viewSession.do?id="
						+ base64.encode(ses.getId().getBytes());
				 %>
				 <div class="commentmore">
				 	<photo:link url="<%= theUrl %>">(more)</photo:link>
				 </div>
			<% } else { %>
				n/a
			<% } %>
		</td>
	</tr>

</logic:iterate>

</table>

</p>
<%-- arch-tag: B7076486-5D6F-11D9-AA4E-000A957659CC --%>
