<%@ page import="net.spy.photo.PhotoImageData" %>
<%@ page import="net.spy.photo.Comment" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%
	PhotoImageData image=(PhotoImageData)request.getAttribute("image");
	String searchIdS=(String)request.getParameter("search_id");
%>

<% if(searchIdS == null) { %>
	<div class="displayBrief"><c:out value="${image.descr}"/></div>
<% } else { %>
	<table width="100%">
		<tr valign="top">
			<td align="left" width="10%">
					<photo:imgLink id="0" relative="prev"
						searchId='<%= "" + searchIdS %>'>
						<photo:imgsrc alt="previous" border="0" url="/images/prev.png"/>
					</photo:imgLink>
			</td>

			<td align="center">
				<div class="displayBrief"><c:out value="${image.descr}"/></div>
			</td>

			<td align="right" width="10%">
					<photo:link url='<%= "/display.do?search_id=" + searchIdS %>'>
						<photo:imgsrc alt="pause" border="0" url="/images/pause.png"/>
					</photo:link>
					<photo:link url='<%= "/refreshDisplay.do?search_id=" + searchIdS %>'>
						<photo:imgsrc alt="slideshow" border="0" url="/images/play.png"/>
					</photo:link>
					<photo:imgLink id="<%= 0 %>" relative="next"
							searchId="<%= "" + searchIdS %>">
						<photo:imgsrc alt="next" border="0" url="/images/next.png"/>
					</photo:imgLink>
			</td>
		</tr>
	</table>
<% } %>

	<div align="center">
		<photo:imgSrc id='<%= image.getId() %>'
			width='<%= String.valueOf(image.getScaledDims().getWidth()) %>'
			height='<%= String.valueOf(image.getScaledDims().getHeight()) %>'
			scale="true"/>
	</div>

	<p>
		<b>Category</b>: ``<c:out value="${image.catName}"/>''&nbsp;<b>Keywords</b>:
			<i><c:out value="${image.keywords}"/></i><br>
		<b>Size</b>:  <c:out value="${image.dimensions}"/>
			(<c:out value="${image.size}"/> bytes)<br>
		<b>Taken</b>:  <c:out value="${image.taken}"/>&nbsp; <b>Added</b>:
			<c:out value="${image.timestamp}"/>
		by <c:out value="${image.addedBy.realName}"/><br>
		<b>Info</b>:
		<blockquote><c:out value="${image.descr}"/></blockquote>

	</p>

<photo:guest negate="true">
	[<photo:link url="/logView.do" id="<%= String.valueOf(image.getId()) %>">
			Who's seen this?
	</photo:link>] | 
	[<photo:link url="/addToGallery.do" id="<%= String.valueOf(image.getId()) %>">
		Add to Gallery
	</photo:link>] | 
</photo:guest>
[<photo:imgLink id='<%= String.valueOf(image.getId()) %>'>
	Linkable image
</photo:imgLink>] |
[<photo:link url='<%= "/PhotoServlet/" + image.getId() + ".jpg?id=" + image.getId() %>'>Full Size Image</photo:link>]
<%--
[<photo:imgLink id="<%= "" + image.getId() %>">Full Size Image</photo:imgLink>]
--%>


<p class="comments">

	<h1>Comments</h1>

	<logic:iterate id="comment"
		type="net.spy.photo.Comment"
		collection="<%= Comment.getCommentsForPhoto(image.getId()) %>">

		<div class="comments">
			<div class="commentheader">
				At <%= comment.getTimestamp() %>,
					<%= comment.getUser().getRealname() %> said the following:
			</div>
			<div class="commentbody">
				<%= comment.getNote() %>
			</div>
		</div>
	</logic:iterate>

</p>

<p>

	<photo:guest negate="true">
		<bean:message key="display.comment"/><br/>
		<logic:messagesPresent>
			<bean:message key="errors.header"/>
			<ul>
				<html:messages id="error">
				<li><bean:write name="error"/></li>
				</html:messages>
			</ul><hr>
		</logic:messagesPresent>

		<html:form action="/addcomment">
			<html:errors/>
			<html:hidden property="imageId"
				value="<%= String.valueOf(image.getId()) %>"/>
			<html:textarea property="comment" cols="50" rows="2"/>
			<br/>
			<html:submit styleClass="button">Add Comment</html:submit>
		</html:form>
	</photo:guest>
