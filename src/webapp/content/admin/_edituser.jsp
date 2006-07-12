<%@ page import="net.spy.photo.User" %>
<%@ page import="net.spy.photo.UserFactory" %>
<%@ page import="net.spy.photo.Category" %>
<%@ page import="net.spy.photo.CategoryFactory" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>
<jsp:useBean id="adminUserForm" scope="session"
	class="net.spy.photo.struts.AdminUserForm"/>

<html:xhtml/>

<h1>User Administration</h1>

<html:form action="/admusersave">
	<html:errors/>

	<table border="1">
		<tr>
			<td>Username</td>
			<td><html:text property="username"/></td>
		</tr>
		<tr>
			<td>Password</td>
			<td><html:password property="password"/></td>
		</tr>
		<tr>
			<td>Realname</td>
			<td><html:text property="realname"/></td>
		</tr>
		<tr>
			<td>Email</td>
			<td><html:text property="email"/></td>
		</tr>
		<tr>
			<td>Can add</td>
			<td><html:checkbox property="canadd"/></td>
		</tr>
		<tr>
			<td>Administrative Status</td>
			<td>
				<html:select property="adminStatus">
					<html:option value="none">None</html:option>
					<html:option value="admin">Admin</html:option>
					<html:option value="subadmin">Subadmin</html:option>
				</html:select>
			</td>
		</tr>
	</table>

	<table border="1">
		<tr>
			<th>Category</th>
			<th>Can View</th>
			<th>Can Add</th>
		</tr>

		<% User user=UserFactory.getInstance().getObject(
			Integer.parseInt(request.getParameter("userId"))); %>

		<logic:iterate id="cat" type="net.spy.photo.Category"
			collection="<%= CategoryFactory.getInstance().getAdminCatList() %>">

			<%
				String className="";
				String wclassName="";
				String rclassName="";
				if(user != null) {
					if(user.canAdd(cat.getId())) {
						className="writeaccess";
					} else if(user.canView(cat.getId())) {
						className="readaccess";
					}
					if(user.canAdd(cat.getId())) {
						wclassName="writeaccess";
					}
					if(user.canView(cat.getId())) {
						rclassName="readaccess";
					}
				}
			%>

			<tr>
				<td class="<%= className %>"><%= cat.getName() %></td>
				<td class="<%= rclassName %>">
					<html:multibox property="catAclView" value="<%= "" + cat.getId() %>"/>
				</td>
				<td class="<%= wclassName %>">
					<html:multibox property="catAclAdd" value="<%= "" + cat.getId() %>"/>
				</td>
			</tr>
		</logic:iterate>
	</table>

	<div>
		<html:hidden property="userId"/>
		<html:submit>Save</html:submit>
	</div>
</html:form>
<div>
	<% String uid=adminUserForm.getUserId();
		String theUrl="/report/userImgs.do?p.i.user_id=" + uid;
	%>
	<photo:link url='<%= theUrl %>'>Photos this user has seen</photo:link>
</div>
<div>
	<%
		theUrl="/report/userRecent.do?p.i.user_id=" + uid;
	%>
	<photo:link url='<%= theUrl %>'>Recent photos this user has seen</photo:link>
</div>
<div>
	<%
		theUrl="/report/userRecentVotes.do?p.i.user_id=" + uid;
	%>
	<photo:link url='<%= theUrl %>'>Recent photos this user voted on</photo:link>
</div>
<div>
	<%
		theUrl="/report/userLogins.do?p.i.user_id=" + uid;
	%>
	<photo:link url='<%= theUrl %>'>Recent logins by this user</photo:link>
</div>
<%-- arch-tag: C890F4CC-5D6F-11D9-951D-000A957659CC --%>
