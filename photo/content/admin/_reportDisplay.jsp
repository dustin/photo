<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.ResultSetMetaData" %>
<%@ page import="java.sql.Types" %>
<%@ page import="net.spy.db.DBSP" %>
<%@ page import="net.spy.db.TypeNames" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<photo:admin explodeOnImpact="true"/>

<p>

<div class="sectionheader"><%=
	(String)request.getAttribute("reportName") %></div>

<table border="1">

<tr>
	<th>RowNum</th>
<%
	// This db thingy will be closed.
	DBSP db=(DBSP)request.getAttribute("db");
	ResultSet rs=(ResultSet)request.getAttribute("rs");
	ResultSetMetaData rsmd=rs.getMetaData();

	int cols=rsmd.getColumnCount();

	for(int i=0; i<cols; i++) {
%>

	<th><%= rsmd.getColumnName(i+1) %></th>

<% } %>
</tr>

<%
	int rowNum=0;
	while(rs.next()) {
		rowNum++;
%>

	<tr>
		<td align="right"><%= rowNum %></td>

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

%> </table> <%


	// See if there are any parameters
	Collection params=db.getParameters();
	if(params.size() > 0) {
		Map vals=new HashMap();
		for(Iterator i=db.getArguments().iterator(); i.hasNext(); ) {
			DBSP.Argument a=(DBSP.Argument)i.next();
			vals.put(a.getName(), String.valueOf(a.getValue()));
		}
		%>
			<div class="sectionheader">Adjust Report Parameters</div>
			<form method="POST"
				action="<%= request.getAttribute("rurl").toString() %>">
				<table border="1">
					<tr>
						<th>Field</th>
						<th>Value</th>
					</tr>

					<%
						for(Iterator i=params.iterator(); i.hasNext(); ) {
							DBSP.Parameter p=(DBSP.Parameter)i.next();

							String vstr=(String)vals.get(p.getName());

							String pName="p.";
							switch(p.getJavaType()) {
								case Types.VARCHAR:
									pName += "s.";
									break;
								case Types.INTEGER:
									pName += "i.";
									break;
								case Types.FLOAT:
									pName += "f.";
									break;
								default:
									throw new ServletException("Can't deal with type "
										+ TypeNames.getTypeName(p.getParamType()));
							}
							pName += p.getName();

							%>
								<tr>
									<td><%= p.getName() %></td>
									<td><input name="<%= pName %>" value="<%= vstr %>"></td>
								</tr>
							<%
						}
					%>

				</table>
				<input type="submit" value="Report">
			</form>
		<%
	}
%>
