<%@ page import="net.spy.photo.*" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%
  // Find the results
	PhotoSessionData sessionData=
		(PhotoSessionData)session.getAttribute("photoSession");
%>

<div class="sectionheader">Gallery List</div>

<ul>
<logic:iterate id="g" type="net.spy.photo.Gallery"
	collection="<%= Gallery.getGalleries(sessionData.getUser()) %>">
	<li>
		<photo:link url="/showgallery.do" id="<%= "" + g.getId() %>">
			[<%= g.getTimestamp() %>] <%= g.getOwner().getRealname() %> - <%= g.getName() %> - <%= g.size() %> images.
		</photo:link>
	</li>
</logic:iterate>
</ul>
