<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>

<p>
<h1>Set the Optimal Viewing Size</h1>
<html:form action="/setviewsize">
<form method="POST" action="PhotoServlet">

<html:errors/>

	Choose the optimal viewing size for your display.
	<html:select property="dims">
		<html:option value="640x480">640x480</html:option>
		<html:option value="800x600">800x600</html:option>
		<html:option value="1024x768">1024x768</html:option>
	</html:select>
	<br/>
	<%--
	Remember:  <html:checkbox property="remember">
	<br/>
	--%>
	<html:submit>Set Size</html:submit>

</html:form>
</p>
