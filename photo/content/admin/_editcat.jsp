<%@ page import="net.spy.photo.PhotoUser" %>
<%@ page import="net.spy.photo.PhotoSecurity" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<photo:admin explodeOnImpact="true"/>

<p>

<div class="sectionheader">Category Administration</div>

<html:form action="/admcatsave">
	<html:errors/>

	<table border="1">
		<tr>
			<td>Name</td>
			<td><html:text property="name"/></td>
		</tr>
	</table>

	<table border="1">
		<tr>
			<th>User</th>
			<th>Can View</th>
			<th>Can Add</th>
		</tr>

		<logic:iterate id="user" type="net.spy.photo.PhotoUser"
			collection="<%= PhotoSecurity.getAllUsers() %>">

			<tr>
				<td><%= user.getUsername() %> (<%= user.getRealname() %>)</td>
				<td>
					<html:multibox property="catAclView"
						value="<%= "" + user.getId() %>"/>
				</td>
				<td>
					<html:multibox property="catAclAdd"
						value="<%= "" + user.getId() %>"/>
				</td>
			</tr>
		</logic:iterate>
	</table>

	<html:submit styleClass="button">Save</html:submit>
</html:form>
