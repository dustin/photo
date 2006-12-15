<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%@ page import="net.spy.photo.User"%>

<h1>New Password Sent</h1>

<%
	User user=(User)request.getAttribute("net.spy.photo.ForgottenUser");
%>

A new password has been created and mailed to <%= user.getEmail() %>.
