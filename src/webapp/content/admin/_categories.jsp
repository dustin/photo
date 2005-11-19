<%@ page import="net.spy.photo.Category" %>
<%@ page import="net.spy.photo.CategoryFactory" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<h1>Category Administration</h1>

<html:form action="/admcatedit">
	<div>
		<html:select property="catId">
			<html:option value="-1">New Category</html:option>
			<logic:iterate id="cat" type="net.spy.photo.Category"
				collection="<%= CategoryFactory.getInstance().getAdminCatList() %>">

				<html:option value="<%= "" + cat.getId() %>"><%= cat.getName() %>
				</html:option>
			</logic:iterate>
		</html:select>
	</div>

	<div>
		<html:submit>Edit</html:submit>
	</div>
</html:form>
<%-- arch-tag: C6C09115-5D6F-11D9-BE9B-000A957659CC --%>
