<%@ page import="net.spy.photo.Category" %>
<%@ page import="net.spy.photo.CategoryFactory" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<h1>Category Administration</h1>

<p>
	<a href="<c:url value='/admcatedit.do'><c:param name='catId' value='-1'/></c:url>">
		Create a New Category</a>
</p>

<ul>
	<logic:iterate id="cat" type="net.spy.photo.Category"
		collection="<%= CategoryFactory.getInstance().getAdminCatList() %>">

		<c:set var="u"><c:url value="/admcatedit.do">
			<c:param name="catId"><%= String.valueOf(cat.getId()) %></c:param>
		</c:url></c:set>

		<li>
			<a href="<c:out value='${u}'/>"><%= cat.getName() %></a>
		</li>
	</logic:iterate>
</ul>
