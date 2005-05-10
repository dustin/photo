<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
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
<%-- arch-tag: B5A3CA60-5D6F-11D9-A48D-000A957659CC --%>
