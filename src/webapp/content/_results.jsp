<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<script type="text/javascript">
	Event.observe(window, 'load', function() {
		Element.hide($('savesearch'));
		var link=document.createElement("a");
		link.title="Click here to save this search";
		link.appendChild(document.createTextNode("Save this Search"));
		link.href="#";
		link.onclick=function() {
			Element.hide($('savesearchtext'));
			Element.show($('savesearch'));
			return false;
			};
		$('savesearchtext').appendChild(link);
		}, false);
</script>

<html:xhtml/>
<photo:javascript url="/js/catedit.js"/>

<div id="searchheader">
<logic:present role="canadd">
	<div id="savesearchtext"></div>
	<div id="savesearch">
		<form method="post" action="savesearch.do">
			<div>
				<input type="hidden" name="search"
					value="<c:out value="${photoSession.encodedSearchB64}"/>"/>
				Save search as:  <input name="name"/>
				<html:submit>Save</html:submit>
			</div>
		</form>
	</div>
</logic:present>

	<div id="search_matches">
		Search matched <c:out value="${photoSession.results.size}"/> entries.
		<c:set var="encoded"><c:out value="${photoSession.encodedSearch}"/></c:set>
		<c:set var="baserss">
			<logic:present role="authenticated">
				<c:url value="/auth/rss.do"/>
			</logic:present>
			<logic:notPresent role="authenticated">
				<c:url value="/rss.do"/>
			</logic:notPresent>
		</c:set>
		<a href="<c:out value='${baserss}?${encoded}'/>"><img alt="XML"
			src="<c:url value='/images/xml.png'/>"/></a>
	</div>
</div>

<div id="search_results">

<c:forEach var="img" items="${photoSession.results.page}">

<div class="search_result">
	<div class="search_result_image">
		<c:set var="dUrl">
			<c:url value="/display.do">
				<c:param name="id" value="${img.id}"/>
			</c:url>
		</c:set>
		<c:set var="iUrl">
			<c:url value="/PhotoServlet">
				<c:param name="id" value="${img.id}"/>
				<c:param name="thumbnail" value="1"/>
			</c:url>
		</c:set>
		<a href="<c:out value='${dUrl}'/>">
			<img src="<c:out value='${iUrl}'/>"
				width="<c:out value='${img.tnDims.width}'/>"
				height="<c:out value='${img.tnDims.height}'/>"/>
		</a>
	</div>
	<div class="search_result_info">
		ID: <c:out value="${img.id}"/><br/>
		Keywords: <span id="<c:out value='kw${img.id}'/>"><c:forEach
			var="kw" items="${img.keywords}"> <c:out value="${kw.keyword}"
			/></c:forEach></span><br/>
		Category: <span id="<c:out value='c${img.id}'/>"
			><c:out value="${img.catName}"/></span><br/>
		Size: <c:out value="${img.dimensions.width}x${img.dimensions.height}"
			/><br/>
		Taken:
			<span id="<c:out value='tk${img.id}'/>"
				><fmt:formatDate value="${img.taken}" pattern="yyyy-MM-dd"
				/></span><br/>
		Added: <fmt:formatDate value="${img.timestamp}"
			pattern="yyyy-MM-dd HH:mm:ss"/>
				by <c:out value="${img.addedBy.name}"/>
	</div>
	<div class="search_result_descr">
		<div id="<c:out value='d${img.id}'/>"><c:out
			value="${img.descr}"/></div>
	</div>
</div>

<logic:present role="admin">
	<script type="text/javascript">
		new Ajax.InPlaceEditor("<c:out value='kw${img.id}'/>",
			'<c:url value="/ajax/photo/keywords?imgId=${img.id}"/>');
		new Ajax.InPlaceEditor("<c:out value='tk${img.id}'/>",
			'<c:url value="/ajax/photo/taken?imgId=${img.id}"/>');
		new Ajax.InPlaceEditor("<c:out value='d${img.id}'/>",
			'<c:url value="/ajax/photo/descr?imgId=${img.id}"/>',
			{rows:4, cols:60});
		setupCategoryEditor("<c:out value='c${img.id}'/>",
			'<c:url value="/ajax/photo/cat?imgId=${img.id}"/>',
			'<c:url value="/"/>');
	</script>
</logic:present>

</c:forEach>

</div>

<c:if test="${photoSession.results.numRemaining > 0}">
	<div>
	<html:form action="nextresults.do">
		<div>
			<input type="hidden" name="startOffset"
				value="<c:out value='${photoSession.results.pageNumber + 1}'/>"/>
			<input type="hidden" name="whichCursor" value="results"/>
			<html:submit>
				<c:out value="Next ${photoSession.results.nextPageSize}"/>
			</html:submit>
		</div>
	</html:form>
	</div>
</c:if>

<%-- arch-tag: B2A859AA-5D6F-11D9-BEFD-000A957659CC --%>
