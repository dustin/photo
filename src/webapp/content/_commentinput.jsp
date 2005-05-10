<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
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
	<html:submit styleClass="reset">Add Comment</html:submit>
</html:form>

<%-- arch-tag: AA4702BC-5D6F-11D9-8719-000A957659CC --%>
