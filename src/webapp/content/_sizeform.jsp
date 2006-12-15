<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>

<html:xhtml/>

<h1>Set the Optimal Viewing Size</h1>
<html:form action="/setviewsize">

<html:errors/>

<p>
	Choose the optimal viewing size for your display.
	<html:select property="dims">
		<html:option value="640x480">640x480</html:option>
		<html:option value="800x600">800x600</html:option>
		<html:option value="1024x768">1024x768</html:option>
	</html:select>
	<br/>
	<html:submit>Set Size</html:submit>
</p>

</html:form>
