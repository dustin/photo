<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html:xhtml/>

<html:html xhtml="true">

	<head>
		<title><tiles:insert attribute='errormessage'/></title>
		<photo:stylesheet url="/css/layout.css"/>
	</head>

	<body>
		<h1><tiles:insert attribute='errormessage'/></h1>

		<p>
			The document you requested is unavailable to you.
		</p>

	<logic:notPresent role="authenticated">

		<p>Perhaps logging in will give you more access.</p>

		<html:form action="/login" focus="username">
			<div>
				<label for="uname"><fmt:message key="forms.login.username"/></label>
				<html:text styleId="uname" property="username" maxlength="16" size="8"/>
			</div>

			<div>
				<label for="passwd"><fmt:message key="forms.login.password"/></label>
				<html:password styleId="passwd" property="password" size="8"/>
			</div>

			<div>
				<input type="hidden" name="return" value='refer'/>
				<html:submit><fmt:message key="forms.login.authbutton"/></html:submit>
				<html:reset><fmt:message key="forms.login.rstbutton"/></html:reset>
			</div>
		</html:form>
	</logic:notPresent>

	</body>
</html:html>

<%-- arch-tag: 166D9540-46A0-4993-93E6-1D8C6447CD84 --%>
