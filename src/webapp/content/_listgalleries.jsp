<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<h1>Gallery List</h1>

<ul>
	<c:forEach var="g" items="${photoSession.galleries.page}">
		<c:set var="u">
			<c:url value="/showGallery.do">
				<c:param name="id" value="${g.id}"/>
			</c:url>
		</c:set>
		<li>
			<a href="<c:out value='${u}'/>">
				[<c:out value="${g.timestamp}"/>]
					<c:out value="${g.owner.realname} - ${g.name} - ${g.size} images."/>
			</a>
		</li>
	</c:forEach>
</ul>
