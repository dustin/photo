<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>
<h1><bean:message key="forms.changepw.header"/></h1>
<html:form action="/changepw" focus="oldpw">
	<html:errors/>
	<table>
		<tr>
			<td><bean:message key="forms.changepw.oldpw"/>:</td>
			<td><html:password property="oldpw" size="8"/></td>
		</tr>
		<tr>
			<td><bean:message key="forms.changepw.newpw1"/>:</td>
			<td><html:password property="newpw1" size="8"/></td>
		</tr>
		<tr>
			<td><bean:message key="forms.changepw.newpw2"/>:</td>
			<td><html:password property="newpw2" size="8"/></td>
	</table>
	<html:submit styleClass="button">
		<bean:message key="forms.changepw.authbutton"/>
	</html:submit>
	<html:reset styleClass="button">
		<bean:message key="forms.changepw.rstbutton"/>
	</html:reset>
</html:form>

</p>
