<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<div>
<h1><bean:message key="forms.login.header"/></h1>
<html:form action="/login" focus="username">
	<div>
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
		<tr>
			<td><bean:message key="forms.login.setadmin"/>:</td>
			<td><html:checkbox property="admin"/></td>
		</tr>
	</table>
	<html:submit>
		<bean:message key="forms.login.authbutton"/>
	</html:submit>
	<html:reset>
		<bean:message key="forms.login.rstbutton"/>
	</html:reset>
	</div>
</html:form>

</div>

<div>

<h1>Forget your Password</h1>

<html:form action="/forgotpw">
	<div>
	<html:errors/>
	<bean:message key="forms.forgotpw.username"/>:
		<html:text property="username"/><br/>
	<html:submit>
		<bean:message key="forms.forgotpw.submit"/>
	</html:submit>
	<html:reset>
		<bean:message key="forms.forgotpw.rstbutton"/>
	</html:reset>
	</div>
</html:form>

</div>

<div>

<h1>Other Services</h1>

<a href="adminify.do?action=setadmin">Request Administrative Privileges</a>
<br/>
<photo:link url="/changePwForm.do">Change Password</photo:link>

</div>
