<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%
	String imageId=request.getParameter("imageId");
%>

<center>
	<photo:imgLink id="<%= imageId %>" showThumbnail='true'/>
</center>

<html:form action="/addcomment">
		<logic:messagesPresent>
			<bean:message key="errors.header"/>
			<ul>
				<html:messages id="error">
				<li><bean:write name="error"/></li>
				</html:messages>
			</ul><hr>
		</logic:messagesPresent>
	<html:hidden property="imageId" value="<%= imageId %>"/>
	<html:textarea property="comment" cols="50" rows="2"/>
	<br/>
	<html:submit>Add Comment</html:submit>
</html:form>

