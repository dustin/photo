<%@ page import="net.spy.photo.User" %>
<%@ page import="net.spy.photo.UserFactory" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<h1>User Administration</h1>

<html:form action="/admuseredit">
	<html:errors/>

	<div>
		<html:select property="userId">
			<html:option value="-1">New User</html:option>
			<logic:iterate id="user" type="net.spy.photo.User"
				collection="<%= UserFactory.getInstance().getAllUsers() %>">

				<html:option value="<%= "" + user.getId() %>"><%= user.getName() %>
				</html:option>
			</logic:iterate>
		</html:select>
	</div>

	<div>
		<html:submit>Edit</html:submit>
	</div>
</html:form>
<%-- arch-tag: CBE44BFF-5D6F-11D9-BB67-000A957659CC --%>
