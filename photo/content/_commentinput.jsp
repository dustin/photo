<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%
	String imageId=request.getParameter("imageId");
%>

<center>
	<photo:imgLink id="<%= imageId %>" showThumbnail='true'/>
</center>

<html:form action="/addcomment">
	<html:errors/>
	<html:hidden property="imageId" value="<%= imageId %>"/>
	<html:textarea property="comment" cols="50" rows="2"/>
	<br/>
	<input type="submit" value="Comment"/>
	<html:submit>Add Comment</html:submit>
</html:form>

