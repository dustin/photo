<%@ page import="net.spy.photo.Category" %>
<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>

<template:insert template='/templates/section_header.jsp'>
	<template:put name='title' content='Category Administration' direct='true'/>
</template:insert>

<form method="POST" action="/photo/PhotoServlet">
	<input type=hidden name=func value=admcatedit>
	<select name=cat>
		<option value="-1">New Category</option>
		<logic:iterate id="i" collection="<%= Category.getAdminCatList() %>">
			<% Category cat=(Category)i; %>

			<option value="<%= cat.getId() %>"><%= cat.getName() %>
		</logic:iterate>
	</select>

	<input type="submit" value="Edit">
</form>
