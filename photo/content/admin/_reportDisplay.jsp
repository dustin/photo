<%@ page import="java.sql.ResultSet" %>

<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.ResultSetMetaData" %>
<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<photo:admin explodeOnImpact="true"/>

<p>

<template:insert template='/templates/section_header.jsp'>
	<template:put name='title'
		content='<%= (String)request.getAttribute("reportName") %>' direct='true'/>
</template:insert>

<table border="1">

<tr>
<%
	ResultSet rs=(ResultSet)request.getAttribute("rs");
	ResultSetMetaData rsmd=rs.getMetaData();

	int cols=rsmd.getColumnCount();

	for(int i=0; i<cols; i++) {
%>

	<th><%= rsmd.getColumnName(i+1) %></th>

<% } %>
</tr>

<%
	while(rs.next()) {
%>

	<tr>

	<%
		for(int i=1; i<=cols; i++) {
			String colName=rsmd.getColumnName(i);

			%> <td> <%

			if(colName.equals("image_id")) {
				%>
					<photo:imgLink id='<%= rs.getString(i) %>'>
						<%= rs.getString(i) %>
					</photo:imgLink>
				<%
			} else {
				%> <%= rs.getString(i) %> <%
			}

			%> </td> <%

		}
	%>
	</tr>

<%
	}
	rs.close();
%>

</table>
