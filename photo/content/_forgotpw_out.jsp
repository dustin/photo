<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%@ page import="net.spy.photo.PhotoUser"%>

<h1>New Password Sent</h1>

<%
	PhotoUser user=(PhotoUser)request.getAttribute("net.spy.photo.ForgottenUser");
%>

A new password has been created and mailed to <%= user.getEmail() %>.

