<%@ page import="net.spy.photo.PhotoUser" %>
<%@ page import="net.spy.photo.PhotoSecurity" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>

<div class="sectionheader">User Administration</div>

<html:form action="/admuseredit">
	<html:errors/>
	<html:select property="userId">
		<html:option value="-1">New User</html:option>
		<logic:iterate id="user" type="net.spy.photo.PhotoUser"
			collection="<%= PhotoSecurity.getAllUsers() %>">

			<html:option value="<%= "" + user.getId() %>"><%= user.getUsername() %>
			</html:option>
		</logic:iterate>
	</html:select>

	<html:submit>Edit</html:submit>
</html:form>
<%-- arch-tag: CBE44BFF-5D6F-11D9-BB67-000A957659CC --%>
