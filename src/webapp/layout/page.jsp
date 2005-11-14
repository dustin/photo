<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<jsp:useBean id="props" class="net.spy.photo.PhotoProperties" />

<html:html xhtml="true">
	<head>
		<title><tiles:insert attribute='title'/></title>
		<photo:stylesheet url="/css/layout.css"/>
		<photo:javascript url="/js/debug.js"/>
		<photo:javascript url="/js/prototype.js"/>
		<photo:javascript url="/js/s/scriptaculous.js"/>
		<style type="text/css">
			body {
				background-image: url(<%= props.getProperty("background_img", "") %>);
			}
		</style>
</head>
<body>

	<div id="navbar">
		<ul>
			<li><photo:link url="/index.do" message="page.links.home"/></li>
			<logic:present role="guest">
				<li><photo:link url="/credform.do"
					message="index.links.login"/></li>
			</logic:present>
			<logic:notPresent role="guest">
				<li><photo:link url="/logout.do" message="page.links.logout"/></li>
			</logic:notPresent>
			<li><photo:link url="/searchForm.do"
				message="index.links.advsearch"/></li>
			<li><photo:link url="/listcomments.do"
				message="index.links.comments"/></li>
			<li><photo:link url="/galleryList.do"
				message="index.links.listgalleries"/></li>
			<li><photo:link url="/catview.do"
				message="index.links.catview"/></li>
			<logic:notPresent role="guest">
				<li><photo:link url="/addform.do"
					message="index.links.addform"/></li>
				<li><photo:link url="/saveGalleryForm.do"
					message="index.links.savegallery"/></li>
				<li><photo:link url="/sessions.do" message="page.links.sessions"/></li>
			</logic:notPresent>
			<li><photo:link url="/newUserForm.do"
				message="index.links.newuser"/></li>
			<li><photo:link url="/sizeForm.do" message="page.links.size"/></li>
			<photo:admin>
			<li>Admin Menu</li>
				<ul>
					<li>
						<photo:link url="/admin/reporting.do"
							message="index.links.admin.reporting"/>
					</li>
					<li>
						<photo:link url="/admin/userList.do"
							message="index.links.admin.user"/>
					</li>
					<li>
						<photo:link url="/admin/catList.do"
							message="index.links.admin.cat"/>
					</li>
					<li>
						<photo:link url="/admin/newprofile.do"
							message="index.links.admin.newprofile"/>
					</li>
					<li>
						<photo:link url="/admin/properties.do"
							message="index.links.admin.properties"/>
					</li>
					<li>
						<photo:link url="/adminify.do?action=unsetadmin"
							message="index.links.admin.droppriv"/>
					</li>
				</ul>
			</li>
	</photo:admin>
		</ul>
	</div>

	<div id="content">
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
				<fmt:message key="page.content.footer"/><br/>
				Build 
				<fmt:message key="build.dtstamp"/> (<fmt:message key="tla.long.version"/>)
			</div> <!-- footerfineprint -->
		</div><div class="rightstuff">
				<tiles:insert page='/templates/quicksearch.jsp' />
		</div> <!-- /rightstuff -->
	</div> <!-- /Footer -->

	<div id="sflogo">
		<a href="http://photoservlet.sourceforge.net/"><img width="88" height="31"
			alt="SourceForge"
			src="http://sourceforge.net/sflogo.php?group_id=7312&amp;type=1"/></a>
	</div>

</body></html:html>
<%-- arch-tag: DE9EE0C2-5D6F-11D9-BA5E-000A957659CC --%>
