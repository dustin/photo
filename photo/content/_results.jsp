<%@ page import="net.spy.photo.*" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='logic' %>
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

<ul>

<p>
<logic:iterate id="i" collection="<%= results %>"
	length="6">
	<% PhotoSearchResult image=(PhotoSearchResult)i; %>

	<li>
	<photo:imgLink id="<%= image.getImageId() %>"
		showThumbnail="true"/>
	</li>

</logic:iterate>
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
        <html:hidden name="startOffset" value="<%= nextOffsetS %>"/>
        <html:hidden name="whichCursor" value="results"/>
        <html:submit>Next <%= nextWhu %></html:submit>
      </html:form>
      </p>
    <%
  }
%>

</li>
