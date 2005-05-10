<%@ page import="net.spy.photo.Category" %>
<%@ page import="net.spy.photo.CategoryFactory" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>

<div class="sectionheader">Category Administration</div>

<html:form action="/admcatedit">
	<html:select property="catId">
		<html:option value="-1">New Category</html:option>
		<logic:iterate id="cat" type="net.spy.photo.Category"
			collection="<%= CategoryFactory.getInstance().getAdminCatList() %>">

			<html:option value="<%= "" + cat.getId() %>"><%= cat.getName() %>
			</html:option>
		</logic:iterate>
	</html:select>

	<html:submit>Edit</html:submit>
</html:form>
<%-- arch-tag: C6C09115-5D6F-11D9-BE9B-000A957659CC --%>
