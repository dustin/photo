<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>
<h1>Save a Gallery</h1>
<html:form action="/saveGallery" focus="name">
	<html:errors/>
	<table>
		<tr>
			<td>Gallery name:</td>
			<td><html:text property="name"/></td>
		</tr>
		<tr>
			<td>Public?</td>
			<td><html:checkbox property="isPublic"/></td>
	</table>
	<html:submit>Save Gallery</html:submit>
	<html:reset>Reset Form</html:reset>
</html:form>

</p>
