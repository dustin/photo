<%@ page import="net.spy.photo.PhotoUser" %>
<%@ page import="net.spy.photo.Category" %>
<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>
<jsp:useBean id="adminUserForm" scope="session"
	class="net.spy.photo.struts.AdminUserForm"/>

<photo:admin explodeOnImpact="true"/>

<p>

<template:insert template='/templates/section_header.jsp'>
	<template:put name='title' content='User Administration' direct='true'/>
</template:insert>

<html:form action="/admusersave">
	<html:errors/>

	<table border="1">
		<tr>
			<td>Username</td>
			<td><html:text property="username"/></td>
		</tr>
		<tr>
			<td>Password</td>
			<td><html:password property="password"/></td>
		</tr>
		<tr>
			<td>Realname</td>
			<td><html:text property="realname"/></td>
		</tr>
		<tr>
			<td>Email</td>
			<td><html:text property="email"/></td>
		</tr>
		<tr>
			<td>Can add</td>
			<td><html:checkbox property="canadd"/></td>
		</tr>
		<tr>
			<td>Administrative Status</td>
			<td>
				<html:select property="adminStatus">
					<html:option value="none">None</html:option>
					<html:option value="admin">Admin</html:option>
					<html:option value="subadmin">Subadmin</html:option>
				</html:select>
			</td>
		</tr>
	</table>

	<table border="1">
		<tr>
			<th>Category</th>
			<th>Can View</th>
			<th>Can Add</th>
		</tr>

		<logic:iterate id="cat" type="net.spy.photo.Category"
			collection="<%= Category.getAdminCatList() %>">

			<tr>
				<td><%= cat.getName() %></td>
				<td>
					<html:multibox property="catAclView" value="<%= "" + cat.getId() %>"/>
				</td>
				<td>
					<html:multibox property="catAclAdd" value="<%= "" + cat.getId() %>"/>
				</td>
			</tr>
		</logic:iterate>
	</table>

	<html:submit>Save</html:submit>
	<p>
		<% String uid=adminUserForm.getUserId();
			String theUrl="/admin/userPhotos.jsp?user=" + uid;
		%>
		<photo:link url='<%= theUrl %>'>Photos this user has seen</photo:link>
	</p>
</html:form>
