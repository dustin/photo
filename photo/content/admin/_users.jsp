<%@ page import="net.spy.photo.PhotoUser" %>
<%@ page import="net.spy.photo.PhotoSecurity" %>
<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>

<template:insert template='/templates/section_header.jsp'>
	<template:put name='title' content='User Administration' direct='true'/>
</template:insert>

<form method="POST" action="/photo/PhotoServlet">
	<input type=hidden name=func value=admuseredit>
	<select name=userid>
		<option value="-1">New User</option>
		<logic:iterate id="i" collection="<%= PhotoSecurity.getAllUsers() %>">
			<% PhotoUser user=(PhotoUser)i; %>

			<option value="<%= user.getId() %>"><%= user.getUsername() %>
		</logic:iterate>
	</select>

	<input type="submit" value="Edit">
</form>
