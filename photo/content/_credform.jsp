<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<div>
<h1><fmt:message key="forms.login.header"/></h1>
<html:form action="/login" focus="username">
	<div>
	<html:errors/>
	<table>
		<tr>
			<td><fmt:message key="forms.login.username"/>:</td>
			<td>
				<html:text property="username" maxlength="16" size="8"/>
			</td>
		</tr>
		<tr>
			<td><fmt:message key="forms.login.password"/>:</td>
			<td><html:password property="password" size="8"/></td>
		</tr>
		<tr>
			<td><fmt:message key="forms.login.setadmin"/>:</td>
			<td><html:checkbox property="admin"/></td>
		</tr>
	</table>
	<html:submit>
		<fmt:message key="forms.login.authbutton"/>
	</html:submit>
	<html:reset>
		<fmt:message key="forms.login.rstbutton"/>
	</html:reset>
	</div>
</html:form>

</div>

<div>

<h1>Forget your Password</h1>

<html:form action="/forgotpw">
	<div>
	<html:errors/>
	<fmt:message key="forms.forgotpw.username"/>:
		<html:text property="username"/><br/>
	<html:submit>
		<fmt:message key="forms.forgotpw.submit"/>
	</html:submit>
	<html:reset>
		<fmt:message key="forms.forgotpw.rstbutton"/>
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
