<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<jsp:useBean id="props" class="net.spy.photo.PhotoProperties" />

<%-- The following tags must be kept as-is --%>
<photo:initSessionData/>
<%-- <photo:logRequest/> --%>
<html><head><title><template:get name='title'/></title>
<photo:stylesheet url="/style.css"/>
</head>
<body background='<%= props.getProperty("background_img", "") %>'>

	<div id="pagetitle"><template:get name='title'/></div>

	<div id="pagebody">
			<template:get name='content'/>
	</div>
	<div class="footer"> <!-- Footer -->
		<div class="leftstuff">
			<div>
				Logged in as
				<photo:link url="/credform.jsp">
					<%= sessionData.getUser().getRealname() %>
				</photo:link>
				<photo:admin>
					<photo:link url="/adminify.do?action=unsetadmin">(admin)</photo:link>
				</photo:admin>
			</div>
			<div>
				<photo:sessionInfo/>
			</div>
			<div class="footerfineprint">
				<bean:message key="page.content.footer"/><br/>
				Build <bean:message key="build.number"/> from
				<bean:message key="build.dtstamp"/>
			</div> <!-- footerfineprint -->
		</div><div class="rightstuff">
				<template:insert template='/templates/quicksearch.jsp' />
				<photo:link url="/" message="page.links.home"/>
				| <photo:link url="/sizeform.jsp" message="page.links.size"/>
				<photo:guest negate="true">
					| <photo:link url="/sessions.jsp" message="page.links.sessions"/>
					| <photo:link url="/logout.do" message="page.links.logout"/>
				</photo:guest>
		</div> <!-- /rightstuff -->
	</div> <!-- /Footer -->

	<div id="sflogo">
		<a href="http://photoservlet.sourceforge.net/"><img border="0"
			width="88" height="31" alt="SourceForge"
			src="http://sourceforge.net/sflogo.php?group_id=7312&type=1"/></a>
	</div>

</body></html>
