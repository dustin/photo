<%@ page import="net.spy.photo.User" %>
<%@ page import="net.spy.photo.UserFactory" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<h1>Category Administration</h1>

<html:form action="/admcatsave">
	<html:errors/>

	<fieldset>
		<legend>Name</legend>
			<label for="name">Name</label>
			<html:text styleId="name" property="name"/>
	</fieldset>

	<fieldset>
		<legend>Permissions</legend>

	<table border="1">
		<tr>
			<th>User</th>
			<th>Can View</th>
			<th>Can Add</th>
		</tr>

		<% int catId=Integer.parseInt(request.getParameter("catId")); %>

		<logic:iterate id="user" type="net.spy.photo.User"
			collection="<%= UserFactory.getInstance().getAllUsers() %>">

			<%
				String className="";
				String wclassName="";
				String rclassName="";
				if(user.canAdd(catId)) {
					className="writeaccess";
				} else if(user.canView(catId)) {
					className="readaccess";
				}
				if(user.canAdd(catId)) {
					wclassName="writeaccess";
				}
				if(user.canView(catId)) {
					rclassName="readaccess";
				}
			%>

			<tr>
				<td class="<%= className %>"><%= user.getName() %>
					(<%= user.getRealname() %>)</td>
				<td class="<%= rclassName %>">
					<html:multibox property="catAclView"
						value="<%= "" + user.getId() %>"/>
				</td>
				<td class="<%= wclassName %>">
					<html:multibox property="catAclAdd"
						value="<%= "" + user.getId() %>"/>
				</td>
			</tr>
		</logic:iterate>
	</table>

	</fieldset>

	<div>
		<html:hidden property="catId"/>
		<html:submit>Save</html:submit>
	</div>
</html:form>
<%-- arch-tag: C7F1ECA8-5D6F-11D9-826B-000A957659CC --%>
