<%@ page import="net.spy.photo.*" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
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

<logic:iterate id="i" collection="<%= results %>"
	length="6">
	<% PhotoSearchResult image=(PhotoSearchResult)i; %>

	<li>
	<photo:imgLink id="<%= image.getImageId() %>"
		showThumbnail="true"/>
	</li>

</logic:iterate>

</li>
