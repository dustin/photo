<%@ page import="net.spy.photo.Category" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<photo:admin explodeOnImpact="true"/>

<%
	String lastProfile=(String)request.getAttribute("net.spy.photo.ProfileId");
%>

<% if(lastProfile!=null) { %>

<p>
	Profile created, ID is <code><%= lastProfile %></code>.
	<hr/>
</p>

<% } %>

<p>

<div class="sectionheader">New Profile</div>

<html:form action="/admnewprofile">

	Profile description: <html:text property="name"/><br/>

	<table border="1">

	<tr>
		<th>Name</th>
		<th>Can View</th>
	</tr>

	<logic:iterate id="cat" type="net.spy.photo.Category"
		collection="<%= Category.getAdminCatList() %>">

		<tr>
			<td><%= cat.getName() %></td>
			<td>
				<html:multibox property="categories" value="<%= "" + cat.getId() %>"/>
			</td>
		</tr>
	</logic:iterate>

	</table>

	<html:submit>Create Profile</html:submit>
</html:form>

</p>
