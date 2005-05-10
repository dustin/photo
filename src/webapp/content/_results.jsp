<%@ page import="net.spy.photo.*" %>
<%@ page import="net.spy.photo.search.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%
	// Find the results
	PhotoSessionData sessionData=
		(PhotoSessionData)session.getAttribute("photoSession");
	SearchResults results=sessionData.getResults();
	if(results==null) {
		throw new ServletException("There are no search results!");
	}
%>

<div>

<table width="100%">
<tr>
<photo:canAdd>
	<% if(sessionData.getEncodedSearch() != null) { %>
	<td valign="top" class="leftAligned">
		<form method="POST" action="savesearch.do">
			<div>
			<input type="hidden" name="search"
				value="<%= sessionData.getEncodedSearch() %>"/>
			Save search as:  <input name="name"/>
			<html:submit>Save</html:submit>
		</div>
		</form>
	</td>
	<% } %>
</photo:canAdd>

	<td valign="top" class="rightAligned">
		<div class="rightAligned search_matches">
			Search matched <%= results.size() %> entries.
		</div>
	</td>
</tr>
</table>

</div>

<div class="search_results">

<table>

<logic:iterate id="image" collection="<%= results %>"
	type="net.spy.photo.search.SearchResult"
	length="<%= String.valueOf(results.getMaxRet()) %>">

<tr>
	<td style="width: 25%" class="centered">
		<photo:imgLink id="<%= image.getId() %>"
			searchId="<%= image.getSearchId() %>"
			width="<%= String.valueOf(image.getTnDims().getWidth()) %>"
			height="<%= String.valueOf(image.getTnDims().getHeight()) %>"
			showThumbnail="true"/>
	</td>
	<td style="width: 25%; background: #eFeFef;" class="leftAligned" valign="top">
		ID: <c:out value="${image.id}"/><br/>
		Keywords: <c:forEach var="kw" items="${image.keywords}">
			<c:out value="${kw.keyword}"/>
		</c:forEach><br/>
		Category: <c:out value="${image.catName}"/><br/>
		Size: <c:out value="${image.dimensions}"/><br/>
		Taken: <fmt:formatDate value="${image.taken}" pattern="yyyy-MM-dd"/><br/>
		Added: <fmt:formatDate value="${image.timestamp}"
			pattern="yyyy-MM-dd HH:mm:ss"/>
				by <c:out value="${image.addedBy.name}"/>
	</td>
</tr>
<tr>
	<td colspan="2" style="width: 25%; background: #eFeFef;" valign="top">
		<div class="imgDescr">
			<c:out value="${image.descr}"/>
		</div>
	</td>
</tr>

</logic:iterate>

</table>

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
