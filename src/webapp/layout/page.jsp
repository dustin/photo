<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ page import="net.spy.photo.PhotoSessionData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%
	// Get the session data to know if we need to offer RSS feeds.
  PhotoSessionData sessionData=
	    (PhotoSessionData)session.getAttribute(PhotoSessionData.SES_ATTR);
%>

<jsp:useBean id="props" class="net.spy.photo.PhotoProperties" />

<html:html xhtml="true">
	<head>
		<title>
			<%= props.getProperty("album_name", "My Photo Album") %>
			- <tiles:insert attribute='title'/>
		</title>
		<photo:stylesheet url="/css/layout.css"/>
		<photo:stylesheet url="/css/m/jd.slideshow.css"/>
		<photo:javascript url="/js/photo.js"/>
		<photo:javascript url="/js/prototype.js"/>
		<photo:javascript url="/js/s/scriptaculous.js"/>
		<style type="text/css">
			body {
				background-image: url(<%= props.getProperty("background_img", "") %>);
			}
		</style>
		<% if(sessionData.getEncodedSearch() != null) { %>
			<c:set var="encoded"><%= sessionData.getEncodedSearch() %></c:set>
			<c:set var="baserss">
				<logic:present role="authenticated">
					<c:url value="/auth/rss.do"/>
				</logic:present>
				<logic:notPresent role="authenticated">
					<c:url value="/rss.do"/>
				</logic:notPresent>
			</c:set>
			<link rel="alternate" type="application/rss+xml"
				title="PhotoServlet Search Results Feed"
				href="<c:out value='${baserss}?${encoded}'/>"/>
		<% } %>
		<c:set var="basecrss">
			<logic:present role="authenticated">
				<c:url value="/auth/rssc"/>
			</logic:present>
			<logic:notPresent role="authenticated">
				<c:url value="/rssc"/>
			</logic:notPresent>
		</c:set>
		<link rel="alternate" type="application/rss+xml"
			title="PhotoServlet Recent Comments RSS Feed"
			href="<c:out value='${basecrss}'/>"/>
</head>
<body>

	<div id="navbar">
		<ul>
			<li><photo:link url="/index.do" message="page.links.home"/></li>
			<li>
				<form method="post" action="<c:url value='/search.do'/>">
					<div>
					<input type="hidden" name="maxret" value="6"/>
					<input type="hidden" name="fieldjoin" value="and"/>
					<input type="hidden" name="keyjoin" value="and"/>
					<input type="hidden" name="order" value="a.ts"/>
					<input type="hidden" name="sdirection" value="desc"/>
					<input type="hidden" name="field" value="keywords"/>
					<input type="hidden" name="action" value="next"/>
					<input id="tsInp" name="what" size="20" style="font-size: x-small;"/>
					</div>
				</form>
				<div id="ts" class="kcSuggestions"
					style="display:none; width: 200px; position: fixed; font-size: small;">
				</div>
				<script type="text/javascript">
					field_blur_behavior('tsInp', 'Search this Album');
					new Ajax.Autocompleter('tsInp','ts',
						'<c:url value="/matchKeyword.do"/>', { tokens: ' '} );
				</script>
			</li>
			<li>Photos
				<ul>
					<li><photo:link url="/searchForm.do"
						message="index.links.advsearch"/></li>
					<li><photo:link url="/listcomments.do"
						message="index.links.comments"/></li>
					<li><photo:link url="/catview.do"
						message="index.links.catview"/></li>
					<li><photo:link url="/galleryList.do"
						message="index.links.listgalleries"/></li>
					<li><photo:link url="/kwCloud.do"
						message="index.links.kwcloud"/></li>
					<logic:present role="authenticated">
						<li><photo:link url="/addform.do"
							message="index.links.addform"/></li>
						<li><photo:link url="/saveGalleryForm.do"
							message="index.links.savegallery"/></li>
						</logic:present>
				</ul>
			</li>
			<li>Accounts
				<ul>
					<logic:notPresent role="authenticated">
						<li><photo:link url="/credform.do"
							message="index.links.login"/></li>
					</logic:notPresent>
					<logic:present role="authenticated">
						<li><photo:link url="/logout.do" message="page.links.logout"/></li>
					</logic:present>
					<li><photo:link url="/sizeForm.do" message="page.links.size"/></li>
					<li><photo:link url="/newUserForm.do"
						message="index.links.newuser"/></li>
					<li><photo:link url="/sessions.do"
						message="page.links.sessions"/></li>
				</ul>
			</li>
			<logic:present role="admin">
			<li>Admin
				<ul>
					<li><photo:link url="/admin/reporting.do"
							message="index.links.admin.reporting"/></li>
					<li><photo:link url="/admin/userList.do"
							message="index.links.admin.user"/></li>
					<li><photo:link url="/admin/catList.do"
							message="index.links.admin.cat"/></li>
					<li><photo:link url="/admin/places.do"
							message="index.links.admin.places"/></li>
					<li><photo:link url="/admin/bulkkw.do"
							message="index.links.admin.bulkkw"/></li>
					<li><photo:link url="/admin/newprofile.do"
							message="index.links.admin.newprofile"/></li>
					<li><photo:link url="/admin/properties.do"
							message="index.links.admin.properties"/></li>
					<li><photo:link url="/admin/cacheValidation.do"
							message="index.links.admin.cacheValidation"/></li>
					<li><photo:link url="/admin/storerControl.do"
							message="index.links.admin.storerControl"/></li>
				</ul>
			</li>
			</logic:present>
		</ul>
	</div>

	<div id="content">
			<!-- messages -->
			<c:if test="${!empty photo_messages}">
				<div id="messages">
					<c:forEach var="msg" items="${photo_messages}">
						<p class="<c:out value='${msg.type}'/>">
							<img alt="<c:out value='${msg.type}'/>"
								src="<c:url value='/images/icon_${msg.type}.gif'/>"/>
							<c:out escapeXml="false" value="${msg.text}" />
						</p>
					</c:forEach>
				</div>
				<c:remove var="photo_messages" scope="session"/>
				<script type="text/javascript">
					Event.observe(window, 'load', function() {
							new Effect.Highlight("messages");
						}, false);
				</script>
			</c:if>
			<tiles:insert attribute='body'/>
	</div>
	<div id="footer"> <!-- Footer -->
		<div>
			Logged in as
			<photo:link url="/credform.do">
				<c:out value="${photoSession.user.realname}"/>
			</photo:link>
		</div>
		<div>
			<photo:sessionInfo/>
		</div>
		<div class="footerfineprint">
			<fmt:message key="page.content.footer"/><br/>
			Build 
			<fmt:message key="build.dtstamp"/> (<fmt:message key="git.long.version"/>)
		</div> <!-- footerfineprint -->
	</div> <!-- /Footer -->

	<div id="sflogo">
		<a href="http://photoservlet.sourceforge.net/"><img width="88" height="31"
			alt="SourceForge"
			src="http://sourceforge.net/sflogo.php?group_id=7312&amp;type=1"/></a>
	</div>

</body></html:html>
