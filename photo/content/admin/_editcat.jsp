<%@ page import="net.spy.photo.User" %>
<%@ page import="net.spy.photo.UserFactory" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>

<div class="sectionheader">Category Administration</div>

<html:form action="/admcatsave">
	<html:errors/>

	<table border="1">
		<tr>
			<td>Name</td>
			<td><html:text property="name"/></td>
		</tr>
	</table>

	<table border="1">
		<tr>
			<th>User</th>
			<th>Can View</th>
			<th>Can Add</th>
		</tr>

		<logic:iterate id="user" type="net.spy.photo.User"
			collection="<%= UserFactory.getInstance().getAllUsers() %>">

			<tr>
				<td><%= user.getName() %> (<%= user.getRealname() %>)</td>
				<td>
					<html:multibox property="catAclView"
						value="<%= "" + user.getId() %>"/>
				</td>
				<td>
					<html:multibox property="catAclAdd"
						value="<%= "" + user.getId() %>"/>
				</td>
			</tr>
		</logic:iterate>
	</table>

	<html:submit>Save</html:submit>
</html:form>
<%-- arch-tag: C7F1ECA8-5D6F-11D9-826B-000A957659CC --%>
