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

<table>

<logic:iterate id="i" collection="<%= results %>"
	length="6">
	<% PhotoSearchResult image=(PhotoSearchResult)i; %>

<tr>
	<td width="25%" align="center">
		<photo:imgLink id="<%= image.getImage() %>"
			width="<%= image.getTnWidth() %>"
			height="<%= image.getTnHeight() %>"
			showThumbnail="true"/>
	</td>
	<td width="25%" align="left" valign="top" bgcolor="#eFeFef">
		ID:  <%= image.getImage() %><br/>
		Keywords:  <%= image.getKeywords() %><br/>
		Category:  <%= image.getCat() %><br/>
		Size:  <%= image.getWidth() %>x<%= image.getHeight() %><br/>
		Taken:  <%= image.getTaken() %><br/>
		Added:  <%= image.getTs() %> by <%= image.getAddedBy() %>
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
