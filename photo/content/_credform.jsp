<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>
<h1>Login</h1>
<form method="POST" action="login.do">
	<input type="hidden" name="func" value="setcred"/>

	<table>
		<tr>
			<td>Username:</td><td><input name="username"
				maxlength="16" size="8"/></td>
		</tr>
		<tr>
			<td>Password:</td>
			<td><input name="password" type="password" size="8"/></td>
		</tr>
	</table>
	<input type="submit" value="Authenticate"/>
	<input type="reset" value="Clear"/>
</form>

</p>

<p>

<h1>Forget your Password</h1>

<form method="POST" action="PhotoServlet">
	Email address or username:  <input name="username"/><br/>
	<input type="submit" value="Password Reset"/>
	<input type="reset" value="Clear"/>
	<input type="hidden" name="func" value="forgotpassword"/>
</form>

</p>

<p>

<h1>Other Services</h1>

<a href="PhotoServlet?func=setadmin">Request Administrative Privileges</a>
<br/>
<a href="PhotoServlet?func=changepwform">Change Password</a>