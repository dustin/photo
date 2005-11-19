<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<div class="cloudcontainer">
<c:forEach var="k" items="${keywords}">
	<c:set var="u">
		<c:url value="/search.do">
			<c:param name="maxret" value="6"/>
			<c:param name="order" value="a.ts"/>
			<c:param name="sdirection" value="desc"/>
			<c:param name="field" value="keywords"/>
			<c:param name="what" value="${k.kwmatch.keyword.keyword}"/>
		</c:url>
	</c:set>
	<a href="<c:out value='${u}'/>"
		class="cloud <c:out value='cloud${k.bucket}'/>"
		title="<c:out value='${k.kwmatch.count} matches for ${k.kwmatch.keyword.keyword}'/>">
			<c:out value="${k.kwmatch.keyword.keyword}"/></a>
</c:forEach>
</div>
<%-- arch-tag: B3DBD981-E8D0-4DA5-8910-7A5744978858 --%>
