<jsp:useBean id="props" class="net.spy.photo.PhotoProperties" />

<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>

<template:insert template='/templates/section_header.jsp'>
	<template:put name='title' direct='true' content='Property Editor'/>
</template:insert>

<html:form action="/admin/saveProperties.do">
	<html:errors/>

	<table border="1">
		<tr>
			<th>Property Name</th>
			<th>Property Value</th>
		</tr>
		<logic:iterate id="i" type="java.util.Map.Entry"
			collection="<%= props.entrySet() %>">
			<tr>
				<td>
					<%= i.getKey() %>
				</td>
				<td>
					<html:text
						property="<%= (String)i.getKey() %>"
						size="30"
						value="<%= (String)i.getValue() %>"/>
				</td>
			</tr>
		</logic:iterate>
	</table>
	<br/>
	<html:submit>Save Properties</html:submit>
</html:form>

</p>
