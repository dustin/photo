<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<jsp:useBean id="props" class="net.spy.photo.PhotoProperties" />

<html:html xhtml="true">
	<head>
		<title><tiles:insert attribute='title'/></title>
		<photo:stylesheet url="/css/layout.css"/>
		<photo:stylesheet url="/css/colors.css"/>
		<c:if test="${slideshowMode eq 1}">
			<photo:stylesheet url="/css/slideshow.css"/>
		</c:if>
		<style type="text/css">
			body {
				background-image: url(<%= props.getProperty("background_img", "") %>);
			}
		</style>
</head>
<body>

	<div id="pagetitle"><tiles:insert attribute='title'/></div>

	<div id="pagebody">
			<tiles:insert attribute='body'/>
	</div>
	<div id="footer"> <!-- Footer -->
		<div class="leftstuff">
			<div>
				Logged in as
				<photo:link url="/credform.do">
					<c:out value="${photoSession.user.realname}"/>
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
				<tiles:insert page='/templates/quicksearch.jsp' />
				<photo:link url="/index.do" message="page.links.home"/>
				| <photo:link url="/sizeForm.do" message="page.links.size"/>
				<photo:guest negate="true">
					| <photo:link url="/sessions.do" message="page.links.sessions"/>
					| <photo:link url="/logout.do" message="page.links.logout"/>
				</photo:guest>
		</div> <!-- /rightstuff -->
	</div> <!-- /Footer -->

	<div id="sflogo">
		<a href="http://photoservlet.sourceforge.net/"><img width="88" height="31"
			alt="SourceForge"
			src="http://sourceforge.net/sflogo.php?group_id=7312&amp;type=1"/></a>
	</div>

</body></html:html>
