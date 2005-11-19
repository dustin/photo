<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<h1>Save a Gallery</h1>
<html:form action="/saveGallery" focus="name">
	<html:errors/>
	<div>
		<label for="name">Gallery name:</label>
		<html:text styleId="name" property="name"/>
	</div>
	<div>
			<label for="public">Public?</label>
			<html:checkbox styleId="public" property="isPublic"/>
	</div>
	<div>
		<html:submit>Save Gallery</html:submit>
		<html:reset>Reset Form</html:reset>
	</div>
</html:form>

<%-- arch-tag: B5A3CA60-5D6F-11D9-A48D-000A957659CC --%>
