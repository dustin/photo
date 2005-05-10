<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>
<h1><fmt:message key="forms.changepw.header"/></h1>
<html:form action="/changepw" focus="oldpw">
	<html:errors/>
	<table>
		<tr>
			<td><fmt:message key="forms.changepw.oldpw"/>:</td>
			<td><html:password property="oldpw" size="8"/></td>
		</tr>
		<tr>
			<td><fmt:message key="forms.changepw.newpw1"/>:</td>
			<td><html:password property="newpw1" size="8"/></td>
		</tr>
		<tr>
			<td><fmt:message key="forms.changepw.newpw2"/>:</td>
			<td><html:password property="newpw2" size="8"/></td>
	</table>
	<html:submit>
		<fmt:message key="forms.changepw.authbutton"/>
	</html:submit>
	<html:reset>
		<fmt:message key="forms.changepw.rstbutton"/>
	</html:reset>
</html:form>

</p>
<%-- arch-tag: A93F6790-5D6F-11D9-B948-000A957659CC --%>
