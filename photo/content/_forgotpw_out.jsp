<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%@ page import="net.spy.photo.PhotoUser"%>

<h1>New Password Sent</h1>

<%
	PhotoUser user=(PhotoUser)request.getAttribute("net.spy.photo.ForgottenUser");
%>

A new password has been created and mailed to <%= user.getEmail() %>.

<%-- arch-tag: AD6F9B78-5D6F-11D9-B7EC-000A957659CC --%>
