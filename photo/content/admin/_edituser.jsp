<%@ page import="net.spy.photo.PhotoUser" %>
<%@ page import="net.spy.photo.PhotoSecurity" %>
<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

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
			<td>Realname</td>
			<td><html:text property="realname"/></td>
		</tr>
		<tr>
			<td>Email</td>
			<td><html:text property="email"/></td>
		</tr>
		<tr>
			<td>Can add</td>
			<td><html:checkbox property="email"/></td>
		</tr>
	</table>

	<%--
	<html:select property=userId>
		<html:option value="-1">New User</html:option>
		<logic:iterate id="i" collection="<%= PhotoSecurity.getAllUsers() %>">
			<% PhotoUser user=(PhotoUser)i; %>

			<html:option value="<%= user.getId() %>"><%= user.getUsername() %>
			</html:option>
		</logic:iterate>
	</html:select>
	--%>

	<html:submit>Save</html:submit>
</html:form>
