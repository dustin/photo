<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%-- The following tags must be kept as-is --%>
<photo:initSessionData/>
<%-- <photo:logRequest/> --%>
<html><head><title><template:get name='title'/></title>
<link rel="stylesheet" href="style.css"/>
</head>
<body background='/~dustin/images/holiday.gif'>

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
										<a href="credform.jsp"><%=
										sessionData.getUser() %></a>.
									</td>
									<td align="right">
										<template:insert
											template='/templates/quicksearch.jsp'>
										</template:insert>
									</td>
								</tr>
							</table>

							<br/>

							<div class="footerfineprint">
								Copyright (&copy;) 1997-2002 Dustin Sallings of
								<a href="http://www.spy.net/">SPY Internetworking</a>.
								<br/>
								All images and other data within these pages
								are property of their owners, and may not be
								used without permission.  
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
