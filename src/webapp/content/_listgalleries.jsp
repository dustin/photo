<%@ page import="net.spy.photo.*" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
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
		<photo:link url="/showGallery.do" id="<%= "" + g.getId() %>">
			[<%= g.getTimestamp() %>] <%= g.getOwner().getRealname() %> - <%= g.getName() %> - <%= g.size() %> images.
		</photo:link>
	</li>
</logic:iterate>
</ul>
<%-- arch-tag: B0678113-5D6F-11D9-B3F1-000A957659CC --%>
