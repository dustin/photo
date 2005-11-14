<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"
%><%@ taglib uri='/tlds/photo.tld' prefix='photo' %>
<ul>
<c:forEach var="k" items="${keywords}">
	<c:set var="u"><c:url value="/PhotoServlet">
		<c:param name="id" value="${k.imgId}"/>
		<c:param name="scale" value="50x50"/></c:url></c:set>
	<li><span
		class="informal"><img alt="img" src='<c:out value="${u}"/>'/></span><c:out
		value="${k.keyword.keyword}"/><span
		class="informal"> (<fmt:message key="kwmatch.count">
			<fmt:param value="${k.count}"/></fmt:message>)</span></li>
</c:forEach>
</ul>
<%-- arch-tag: 630C44EE-5CAE-4CEE-A091-77F329484F33 --%>
