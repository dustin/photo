<%@ page import="net.spy.photo.PhotoImageData" %>
<%@ page import="net.spy.photo.PhotoDimensions" %>
<%@ page import="net.spy.photo.PhotoRegion" %>
<%@ page import="net.spy.photo.PhotoUtil" %>
<%@ page import="net.spy.photo.Comment" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<photo:javascript url="/js/annimg.js"/>

<%
	PhotoImageData image=(PhotoImageData)request.getAttribute("image");
	// Need to know the new dims for the rest of the page
	float scaleFactor=PhotoUtil.getScaleFactor(image.getDimensions(),
		(PhotoDimensions)request.getAttribute("displayDims"));

	String searchIdS=(String)request.getParameter("search_id");
%>

<% if(searchIdS == null) { %>
	<div class="displayBrief"><c:out value="${image.descr}"/></div>
<% } else { %>
	<table width="100%">
		<tr valign="top">
			<td align="left" width="30%">
					<photo:imgLink id="0" relative="prev"
						searchId='<%= "" + searchIdS %>'>
						<photo:imgsrc alt="previous" url="/images/prev.png"/>
					</photo:imgLink>
			</td>

			<td align="center">
				<div class="displayBrief"><c:out value="${image.descr}"/></div>
			</td>

			<td align="right" width="30%">
					<photo:link url='<%= "/display.do?search_id=" + searchIdS %>'>
						<photo:imgsrc alt="pause" url="/images/pause.png"/>
					</photo:link>
					<photo:link url='<%= "/refreshDisplay.do?search_id=" + searchIdS %>'>
						<photo:imgsrc alt="slideshow" url="/images/play.png"/>
					</photo:link>
					<photo:imgLink id="<%= 0 %>" relative="next"
							searchId="<%= "" + searchIdS %>">
						<photo:imgsrc alt="next" url="/images/next.png"/>
					</photo:imgLink>
			</td>
		</tr>
	</table>
<% } %>

	<div id="imgDisplay">
		<photo:imgSrc id='<%= image.getId() %>' showOptimal="true"
			class="annotated"
			usemap="#annotationMap"/>
	</div>

	<div>
		<b>Category</b>: <q><c:out
			value="${image.catName}"/></q>&nbsp;<b>Keywords</b>:
			<i><c:forEach var="kw" items="${image.keywords}">
				<c:out value="${kw.keyword}"/>
			</c:forEach></i><br/>
		<b>Size</b>:  <c:out value="${image.dimensions}"/>
			(<c:out value="${image.size}"/> bytes)<br />
		<b>Taken</b>:  <c:out value="${image.taken}"/> <b>Added</b>:
			<c:out value="${image.timestamp}"/>
		by <c:out value="${image.addedBy.realName}"/><br />
		<b>Info</b>:
		<div class="imgDescr"><c:out value="${image.descr}"/></div>

	</div>

<logic:notPresent role="guest">
	[<photo:link url="/annotateForm.do" id="<%= String.valueOf(image.getId()) %>">
			Annotate
	</photo:link>] | 
	[<photo:link url="/logView.do" id="<%= String.valueOf(image.getId()) %>">
			Who's seen this?
	</photo:link>] | 
	[<photo:link url="/addToGallery.do" id="<%= String.valueOf(image.getId()) %>">
		Add to Gallery
	</photo:link>] | 
</logic:notPresent>
[<photo:imgLink id='<%= String.valueOf(image.getId()) %>'>
	Linkable image
</photo:imgLink>] |
[<photo:link url='<%= "/PhotoServlet/" + image.getId() + ".jpg?id=" + image.getId() %>'>Full Size Image</photo:link>]
<%--
[<photo:imgLink id="<%= "" + image.getId() %>">Full Size Image</photo:imgLink>]
--%>


<div class="comments">

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

</div>

<div>

	<logic:notPresent role="guest">
		<fmt:message key="display.comment"/><br/>
		<html:errors/>

		<html:form action="/addcomment">
			<html:errors/>
			<html:hidden property="imageId"
				value="<%= String.valueOf(image.getId()) %>"/>
			<html:textarea property="comment" cols="50" rows="2"/>
			<br/>
			<html:submit>Add Comment</html:submit>
		</html:form>
	</logic:notPresent>
</div>
<map name="annotationMap">
	<logic:iterate id="region"
		type="net.spy.photo.AnnotatedRegion"
		collection="<%= image.getAnnotations() %>">

		<%
			PhotoRegion scaledRegion=PhotoUtil.scaleRegion(region, scaleFactor);
			int rx1=scaledRegion.getX();
			int ry1=scaledRegion.getY();
			int rx2=scaledRegion.getWidth() + rx1;
			int ry2=scaledRegion.getHeight() + ry1;

			String coords=rx1 + "," + ry1 + "," + rx2 + "," + ry2;
		%>

		<area alt="" title="<%= region.getTitle() %>"
			href="nohref" shape="rect" coords="<%= coords %>"/>

	</logic:iterate>
</map>
<%-- arch-tag: AC919514-5D6F-11D9-ACF8-000A957659CC --%>
