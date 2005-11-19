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

<html:xhtml/>

<photo:javascript url="/js/annimg.js"/>
<photo:javascript url="/js/catedit.js"/>

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
						<img src="<c:url value='/images/prev.png'/>" alt="previous"/>
					</photo:imgLink>
			</td>

			<td align="center">
				<div class="displayBrief"><c:out value="${image.descr}"/></div>
			</td>

			<td align="right" width="30%">
					<photo:link url='<%= "/display.do?search_id=" + searchIdS %>'>
						<img src="<c:url value='/images/pause.png'/>" alt="pause"/>
					</photo:link>
					<photo:link url='<%= "/refreshDisplay.do?search_id=" + searchIdS %>'>
						<img src="<c:url value='/images/play.png'/>" alt="slideshow"/>
					</photo:link>
					<photo:imgLink id="<%= 0 %>" relative="next"
							searchId="<%= "" + searchIdS %>">
						<img src="<c:url value='/images/next.png'/>" alt="next"/>
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

	<c:set var="imgId"><%= image.getId() %></c:set>

	<div>
		<b>Category</b>: <q><span id="imgCat"><c:out
			value="${image.catName}"/></span></q>&nbsp;<b>Keywords</b>:
			<i><span id="imgKeywords"><c:forEach
				var="kw" items="${image.keywords}"> <c:out value="${kw.keyword}"
					/></c:forEach></span></i><br/>
		<b>Size</b>:  <c:out value="${image.dimensions}"/>
			(<c:out value="${image.size}"/> bytes)<br />
		<b>Taken</b>:  <span id="imgTaken"><c:out value="${image.taken}"/></span>
		<b>Added</b>:
			<c:out value="${image.timestamp}"/>
		by <c:out value="${image.addedBy.realName}"/><br />
		<b>Info</b>:
		<div id="imgDescr" class="imgDescr"><c:out value="${image.descr}"/></div>
		<logic:present role="admin">
			<script type="text/javascript">
				new Ajax.InPlaceEditor('imgDescr',
					'<c:url value="/ajax/photo/descr?imgId=${imgId}"/>', {rows: 10, cols: 80});
				new Ajax.InPlaceEditor('imgKeywords',
					'<c:url value="/ajax/photo/keywords?imgId=${imgId}"/>');
				new Ajax.InPlaceEditor('imgTaken',
					'<c:url value="/ajax/photo/taken?imgId=${imgId}"/>');
				setupCategoryEditor('imgCat',
					'<c:url value="/ajax/photo/cat?imgId=${imgId}"/>',
					'<c:url value="/"/>');
			</script>
		</logic:present>

	</div>

<logic:present role="authenticated">
	[<photo:link url="/annotateForm.do" id="<%= String.valueOf(image.getId()) %>">
			Annotate
	</photo:link>] | 
	[<photo:link url="/logView.do" id="<%= String.valueOf(image.getId()) %>">
			Who's seen this?
	</photo:link>] | 
	[<photo:link url="/addToGallery.do" id="<%= String.valueOf(image.getId()) %>">
		Add to Gallery
	</photo:link>] | 
</logic:present>
[<photo:imgLink id='<%= String.valueOf(image.getId()) %>'>
	Linkable image
</photo:imgLink>] |
[<photo:link url='<%= "/PhotoServlet/" + image.getId() + ".jpg?id=" + image.getId() %>'>Full Size Image</photo:link>]
<%--
[<photo:imgLink id="<%= "" + image.getId() %>">Full Size Image</photo:imgLink>]
--%>


<div id="comments" class="comments">

	<h1>Comments</h1>

	<div id="commentanchor"></div>

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

	<logic:present role="authenticated">
	<script type="text/javascript">
		function submitComment() {

			var imgid=<%= image.getId() %>;
			var comment=$F("comment");
			if(comment == "") {
				alert("You'll need to actually type in a comment to post it.");
				return(false);
			}
			Form.disable("commentForm");
			$("comment").value="";

			Element.show("addindicator");
			var postBody=$H({imgId: imgid, comment: comment}).toQueryString();
			new Ajax.Request('<c:url value="/ajax/photo/comment"/>', {
				method: 'post',
				postBody: postBody,
				onComplete: function(req) {
					Form.enable("commentForm");
					Element.hide("addindicator");
				},
				onFailure: function(req) {
					alert("Failed to add comment.");
					},
				onSuccess: function(req) {
					var c=document.createElement("div");
					h=document.createElement("div");
					h.className="commentheader";
					h.appendChild(document.createTextNode("You added"));
					b=document.createElement("div");
					b.className="commentbody";
					b.appendChild(document.createTextNode(comment));

					c.className="comments";
					c.appendChild(h);
					c.appendChild(b);
					$("comments").appendChild(c);
					}
				});
		}
	</script>
		<fmt:message key="display.comment"/><br/>
		<html:form action="/addcomment" onsubmit="submitComment(); return false;"
			styleId="commentForm">
			<div>
				<html:textarea styleId="comment" property="comment" cols="50" rows="2"/>
			</div>
			<div>
				<html:submit>Add Comment</html:submit>
			<img src="<c:url value='/images/indicator.gif'/>"
				alt="indicator" id="addindicator" style="display: none"/>
			</div>
		</html:form>
	</logic:present>
</div>
<map id="annotationMap" name="annotationMap">
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
