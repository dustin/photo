<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>
<h1>Login</h1>
<form method="POST" action="PhotoServlet">
	<input type="hidden" name="func" value="setviewsize"/>

	Choose the optimal viewing size for your display.
	<select name="dims">
		<option>640x480
		<option selected="1">800x600
		<option>1024x768
	</select>
	<br/>
	Remember:  <input type="checkbox" name="remember">
	<br/>
	<input type="submit" value="Set Size">

</form>

</p>
