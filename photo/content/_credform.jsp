<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>
<h1><bean:message key="forms.login.header"/></h1>
<html:form action="/login" focus="username">
	<html:errors/>
	<table>
		<tr>
			<td><bean:message key="forms.login.username"/>:</td>
			<td>
				<html:text property="username" maxlength="16" size="8"/>
			</td>
		</tr>
		<tr>
			<td><bean:message key="forms.login.password"/>:</td>
			<td><html:password property="password" size="8"/></td>
		</tr>
	</table>
	<html:submit><bean:message key="forms.login.authbutton"/></html:submit>
	<html:reset><bean:message key="forms.login.rstbutton"/></html:reset>
</html:form>

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

<a href="adminify.do?action=setadmin">Request Administrative Privileges</a>
<br/>
<photo:link url="/changepw.jsp">Change Password</photo:link>
