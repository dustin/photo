<%@ page import="net.spy.photo.Category" %>
<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<photo:admin explodeOnImpact="true"/>

<p>

<template:insert template='/templates/section_header.jsp'>
	<template:put name='title' content='New Profile' direct='true'/>
</template:insert>

<form method="POST" action="/photo/PhotoServlet">
	<input type=hidden name=func value=admnewprofile>

	Profile description: <input name="name"><br/>

	<table border="1">

	<tr>
		<th>Name</th>
		<th>Can View</th>
	</tr>

	<logic:iterate id="i" collection="<%= Category.getAdminCatList() %>">
		<% Category cat=(Category)i; %>

		<tr>
			<td><%= cat.getName() %></td>
			<td>
				<input type="checkbox" name="catacl_view" value="<%= cat.getId() %>">
			</td>
		</tr>
	</logic:iterate>

	</table>

	<input type="submit" value="Create Profile">
</form>
