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

<center>
	<table bgcolor="#000000">
		<tr><td>
			<table border="5" bgcolor="#fFfFfF">
				<tr><td>
					<div class="pagetitle"><template:get name='title'/></div>
				</td></tr>
			</table>
		</td></tr>
	</table>

	<table bgcolor="#000000" width="75%">
		<tr><td>
			<table bgcolor="#fFfFfF" width="100%">
           		<tr><td>
					<p>
						<template:get name='content'/>
					</p>
					<p>

						<hr>

						<div class="footer">

							<table width="100%">
								<tr valign="top">
									<td align="left">
										Logged in as
										<photo:link url="/credform.jsp">
											<%= sessionData.getUser() %>
										</photo:link>
										<photo:admin>
											<photo:link url="/adminify.do?action=unsetadmin">(admin)
											</photo:link>
										</photo:admin>
										<br/>
										<photo:sessionInfo/>
									</td>
									<td align="right">
										<template:insert
											template='/templates/quicksearch.jsp'>
										</template:insert>
									</td>
								</tr>
								<tr>
									<td></td>
									<td align="right">
										<photo:link url="/" message="page.links.home"/>
										| <photo:link url="/sizeform.jsp"
											message="page.links.size"/>
										<photo:guest negate="true">
											| <photo:link url="/sessions.jsp"
													message="page.links.sessions"/>
											| <photo:link url="/logout.do"
													message="page.links.logout"/>
										</photo:guest>
									</td>
								</tr>
							</table>

							<br/>

							<div class="footerfineprint">
								<bean:message key="page.content.footer"/><br/>
								Build <bean:message key="build.number"/> from
									<bean:message key="build.dtstamp"/>
							</div>
						</div>

					</p>
				</td></tr>
			</table>
		</tr></td>
	</table>

	<a href="http://photoservlet.sourceforge.net/"><img border="0"
		width="88" height="31" alt="SourceForge"
		src="http://sourceforge.net/sflogo.php?group_id=7312&type=1"/></a>

</center>

</body></html>
