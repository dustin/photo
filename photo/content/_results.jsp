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
	type="net.spy.photo.PhotoImageData"
	length="<%= "" + results.getMaxRet() %>">

<tr>
	<td style="width: 25%" class="centered">
		<photo:imgLink id="<%= image.getId() %>"
			searchId="<%= image.getSearchId() %>"
			width="<%= "" + image.getTnDims().getWidth() %>"
			height="<%= "" + image.getTnDims().getHeight() %>"
			showThumbnail="true"/>
	</td>
	<td style="width: 25%; background: #eFeFef;" class="leftAligned" valign="top">
		ID:  <%= image.getId() %><br/>
		Keywords:  <%= image.getKeywords() %><br/>
		Category:  <%= image.getCatName() %><br/>
		Size:  <%= image.getDimensions() %><br/>
		Taken:  <%= image.getTaken() %><br/>
		Added:  <%= image.getTimestamp() %>
						by <%= image.getAddedBy().getUsername() %>
	</td>
</tr>
<tr>
	<td colspan="2" style="width: 25%; background: #eFeFef;" valign="top">
		<div class="imgDescr">
			<%= image.getDescr() %>
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
