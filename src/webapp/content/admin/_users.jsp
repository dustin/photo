<%@ page import="net.spy.photo.User" %>
<%@ page import="net.spy.photo.UserFactory" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>

<div class="sectionheader">User Administration</div>

<html:form action="/admuseredit">
	<html:errors/>
	<html:select property="userId">
		<html:option value="-1">New User</html:option>
		<logic:iterate id="user" type="net.spy.photo.User"
			collection="<%= UserFactory.getInstance().getAllUsers() %>">

			<html:option value="<%= "" + user.getId() %>"><%= user.getName() %>
			</html:option>
		</logic:iterate>
	</html:select>

	<html:submit>Edit</html:submit>
</html:form>
<%-- arch-tag: CBE44BFF-5D6F-11D9-BB67-000A957659CC --%>
