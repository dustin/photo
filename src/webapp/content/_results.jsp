<%@ page import="net.spy.photo.*" %>
<%@ page import="net.spy.photo.search.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>
<photo:javascript url="/js/catedit.js"/>

<%
	// Find the results
	PhotoSessionData sessionData=
		(PhotoSessionData)session.getAttribute("photoSession");
	SearchResults results=sessionData.getResults();
	if(results==null) {
		throw new ServletException("There are no search results!");
	}
%>

<div id="searchheader">
<logic:present role="canadd">
	<% if(sessionData.getEncodedSearch() != null) { %>
		<form method="post" action="savesearch.do">
			<div>
				<input type="hidden" name="search"
					value="<%= sessionData.getEncodedSearch() %>"/>
				Save search as:  <input name="name"/>
				<html:submit>Save</html:submit>
			</div>
		</form>
	<% } %>
</logic:present>

	<div id="search_matches">
		Search matched <%= results.size() %> entries.
	</div>
</div>

<div id="search_results">

<logic:iterate id="image" collection="<%= results %>"
	type="net.spy.photo.search.SearchResult"
	length="<%= String.valueOf(results.getMaxRet()) %>">

<div class="search_result">
	<div class="search_result_image">
		<photo:imgLink id="<%= image.getId() %>"
			searchId="<%= image.getSearchId() %>"
			width="<%= String.valueOf(image.getTnDims().getWidth()) %>"
			height="<%= String.valueOf(image.getTnDims().getHeight()) %>"
			showThumbnail="true"/>
	</div>
	<div class="search_result_info">
		ID: <c:out value="${image.id}"/><br/>
		Keywords: <span id="<c:out value='kw${image.id}'/>"><c:forEach
			var="kw" items="${image.keywords}"> <c:out value="${kw.keyword}"
			/></c:forEach></span><br/>
		Category: <span id="<c:out value='c${image.id}'/>"
			><c:out value="${image.catName}"/></span><br/>
		Size: <c:out value="${image.dimensions}"/><br/>
		Taken:
			<span id="<c:out value='tk${image.id}'/>"
				><fmt:formatDate value="${image.taken}" pattern="yyyy-MM-dd"
				/></span><br/>
		Added: <fmt:formatDate value="${image.timestamp}"
			pattern="yyyy-MM-dd HH:mm:ss"/>
				by <c:out value="${image.addedBy.name}"/>
	</div>
	<div class="search_result_descr">
		<div id="<c:out value='d${image.id}'/>"><c:out
			value="${image.descr}"/></div>
	</div>
</div>

<logic:present role="admin">
	<script type="text/javascript">
		new Ajax.InPlaceEditor("<c:out value='kw${image.id}'/>",
			'<c:url value="/ajax/photo/keywords?imgId=${image.id}"/>');
		new Ajax.InPlaceEditor("<c:out value='tk${image.id}'/>",
			'<c:url value="/ajax/photo/taken?imgId=${image.id}"/>');
		new Ajax.InPlaceEditor("<c:out value='d${image.id}'/>",
			'<c:url value="/ajax/photo/descr?imgId=${image.id}"/>',
			{rows:4, cols:60});
		setupCategoryEditor("<c:out value='c${image.id}'/>",
			'<c:url value="/ajax/photo/cat?imgId=${image.id}"/>',
			'<c:url value="/"/>');
	</script>
</logic:present>

</logic:iterate>

</div>

<%
  // Figure out if there are any more.
  if(results.hasMoreElements()) {
    int nextOffset=results.current();
    String nextOffsetS="" + nextOffset;
    int remaining=results.nRemaining();
    int nextWhu=results.getMaxRet();
    if(remaining<nextWhu) {
      nextWhu=remaining;
    }

    %>
      <%= remaining %> results remaining.
      <div>
      <html:form action="nextresults.do">
				<div>
        <html:hidden property="startOffset" value="<%= nextOffsetS %>"/>
        <html:hidden property="whichCursor" value="results"/>
        <html:submit>Next <%= nextWhu %></html:submit>
				</div>
      </html:form>
      </div>
    <%
  }
%>
<%-- arch-tag: B2A859AA-5D6F-11D9-BEFD-000A957659CC --%>
