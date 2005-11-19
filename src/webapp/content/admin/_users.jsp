<%@ page import="net.spy.photo.User" %>
<%@ page import="net.spy.photo.UserFactory" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<h1>User Administration</h1>

<p>
	<a href="<c:url value='/admuseredit.do'><c:param name='userId' value='-1'/></c:url>">
		Create a New User</a>
</p>

<ul>
		<logic:iterate id="user" type="net.spy.photo.User"
			collection="<%= UserFactory.getInstance().getAllUsers() %>">

			<c:set var="u"><c:url value="/admuseredit.do">
				<c:param name="userId"><%= String.valueOf(user.getId()) %></c:param>
			</c:url></c:set>

			<li>
				<a href="<c:out value='${u}'/>">
					<%= user.getName() %> (<%= user.getRealname() %>)
				</a>
			</li>

		</logic:iterate>
</ul>

<%-- arch-tag: CBE44BFF-5D6F-11D9-BB67-000A957659CC --%>
