<%@ page import="net.spy.photo.*" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%
	// Find the results
	PhotoSessionData sessionData=
		(PhotoSessionData)session.getAttribute("photoSession");
	PhotoSearchResults results=sessionData.getResults();
	if(results==null) {
		throw new ServletException("There are no search results!");
	}
%>

<p>

<table width="100%">
<tr>
<photo:canAdd>
	<td valign="top" align="left">
		<form method="POST" action="savesearch.do">
			<input type="hidden" name="search"
				value="<%= sessionData.getEncodedSearch() %>"/>
			Save search as:  <input name="name"/>
			<input type="submit" value="Save"/>
		</form>
	</td>
</photo:canAdd>

	<td valign="top" align="right">
		<div align="right" class="search_matches">
			Search matched <%= results.nResults() %> entries.
		</div>
	</td>
</tr>
</table>

<p class="search_results">

<table>

<logic:iterate id="i" collection="<%= results %>"
	length="6">
	<% PhotoImageData image=(PhotoImageData)i; %>

<tr>
	<td width="25%" align="center">
		<photo:imgLink id="<%= image.getId() %>"
			searchId="<%= image.getSearchId() %>"
			width="<%= "" + image.getTnDims().getWidth() %>"
			height="<%= "" + image.getTnDims().getHeight() %>"
			showThumbnail="true"/>
	</td>
	<td width="25%" align="left" valign="top" bgcolor="#eFeFef">
		ID:  <%= image.getId() %><br/>
		Keywords:  <%= image.getKeywords() %><br/>
		Category:  <%= image.getCatName() %><br/>
		Size:  <%= image.getDimensions() %><br/>
		Taken:  <%= image.getTaken() %><br/>
		Added:  <%= image.getTimestamp() %> by <%= image.getAddedBy() %>
	</td>
</tr>
<tr>
	<td colspan="2" width="50%" valign="top" bgcolor="#eFeFef">
		<blockquote>
			<%= image.getDescr() %>
		</blockquote>
	</td>
</tr>

</logic:iterate>

</table>

</p>

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
      <p>
      <html:form action="nextresults.do">
        <html:hidden property="startOffset" value="<%= nextOffsetS %>"/>
        <html:hidden property="whichCursor" value="results"/>
        <html:submit>Next <%= nextWhu %></html:submit>
      </html:form>
      </p>
    <%
  }
%>
