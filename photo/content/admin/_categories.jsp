<%@ page import="net.spy.photo.Category" %>
<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<photo:admin explodeOnImpact="true"/>

<p>

<template:insert template='/templates/section_header.jsp'>
	<template:put name='title' content='Category Administration' direct='true'/>
</template:insert>

<html:form action="/admcatedit">
	<html:select property="catId">
		<html:option value="-1">New Category</html:option>
		<logic:iterate id="cat" type="net.spy.photo.Category"
			collection="<%= Category.getAdminCatList() %>">

			<html:option value="<%= "" + cat.getId() %>"><%= cat.getName() %>
			</html:option>
		</logic:iterate>
	</html:select>

	<html:submit>Edit</html:submit>
</html:form>
