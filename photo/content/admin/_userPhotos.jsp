<%@ page import="java.sql.ResultSet" %>

<%@ page import="net.spy.photo.sp.GetImagesSeenByUser" %>
<%@ page import="net.spy.photo.PhotoConfig" %>
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
	<template:put name='title' content='User Photos' direct='true'/>
</template:insert>

<table border="1">

<tr>
	<th>Keywords</th>
	<th>Category</th>
	<th>Count</th>
</tr>

<%
	GetImagesSeenByUser db=new GetImagesSeenByUser(new PhotoConfig());
	db.setUserId(Integer.parseInt(request.getParameter("user")));
	ResultSet rs=db.executeQuery();

	while(rs.next()) {
%>

	<tr>
		<td>
			<photo:imgLink id='<%= rs.getInt("image_id") %>'>
				<%= rs.getString("keywords") %>
			</photo:imgLink>
		</td>
		<td><%= rs.getString("category_name") %></td>
		<td><%= rs.getInt("count") %></td>
	</tr>

<%
	}
	rs.close();
	db.close();
%>

</table>
