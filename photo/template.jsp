<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<html><head><title><template:get name='title'/></title></head>
<body background='/~dustin/images/holiday.gif'>

<center>
	<table bgcolor="#000000">
		<tr><td>
			<table border="5" bgcolor="#fFfFfF">
				<tr><td>
					<b>
						<font size="+3"><template:get name='title'/></font>
					</b>
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

						<!-- Footer goes here -->

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
