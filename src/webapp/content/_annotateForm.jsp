<%@ page import="net.spy.photo.PhotoImageData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>


<%
	PhotoImageData image=(PhotoImageData)request.getAttribute("image");

	String searchIdS=(String)request.getParameter("search_id");
%>

<script language="JavaScript">
	var boxOb=new Object();
	boxOb.boxX1=0;
	boxOb.boxY1=0;
	boxOb.boxX2=0;
	boxOb.boxY2=0;

	boxOb.imgX=0;
	boxOb.imgY=0;
	boxOb.imgX2=0;
	boxOb.imgY2=0;

	function showHighlight(event) {
		// Find the image
		var imageDiv=document.getElementById("hlimage");
		var theImage=imageDiv.getElementsByTagName("img")[0];
		boxOb.imgX=theImage.offsetLeft;
		boxOb.imgY=theImage.offsetTop;
		boxOb.imgX2=boxOb.imgX + theImage.offsetWidth;
		boxOb.imgY2=boxOb.imgY + theImage.offsetHeight;

		boxOb.boxX1=event.clientX + window.scrollX;
		boxOb.boxY1=event.clientY + window.scrollY;
		document.addEventListener("mousemove", movingHighlight, true);
		document.addEventListener("mouseup", setupTag, true);
		event.preventDefault();
	}

	function movingHighlight(event) {
		// Don't allow the selection to go outside of the image.
		boxOb.boxX2=Math.min(boxOb.imgX2,
			Math.max(boxOb.imgX, event.clientX + window.scrollX));
		boxOb.boxY2=Math.min(boxOb.imgY2,
			Math.max(boxOb.imgY, event.clientY + window.scrollY));

		var boxDiv=document.getElementById("zoomBox");
		boxDiv.style.left=Math.min(boxOb.boxX1, boxOb.boxX2) + "px;";
		boxDiv.style.top=Math.min(boxOb.boxY1, boxOb.boxY2) + "px;";
		boxDiv.style.width=Math.abs(boxOb.boxX1 - boxOb.boxX2) + "px;";
		boxDiv.style.height=Math.abs(boxOb.boxY1 - boxOb.boxY2) + "px;";
	}

	function setupTag(event) {
		document.removeEventListener("mousemove", movingHighlight, true);
		document.removeEventListener("mouseup", setupTag, true);

		document.annotateForm.x.value=
			Math.min(boxOb.boxX1, boxOb.boxX2) - boxOb.imgX;
		document.annotateForm.y.value=
			Math.min(boxOb.boxY1, boxOb.boxY2) - boxOb.imgY;
		document.annotateForm.w.value=Math.abs(boxOb.boxX1 - boxOb.boxX2);
		document.annotateForm.h.value=Math.abs(boxOb.boxY1 - boxOb.boxY2);
		var formDiv=document.getElementById("annotateFormDiv");
		formDiv.style.display="block";

		event.preventDefault();
	}

	function clearSelection() {
		var boxDiv=document.getElementById("zoomBox");
		boxDiv.style.width="0px;";
		boxDiv.style.height="0px;";
	}
</script>


	<div id="hlimage">
		<photo:imgSrc id='<%= image.getId() %>' showOptimal="true"
			onMouseDown="showHighlight(event);"/>
	</div>

		<div id="zoomBox"></div>
		<div id="annotateFormDiv" style="display: none;">
			<html:form method="POST" action="/annotate"
				onsubmit="clearSelection(); return true;">
				<input type="hidden" name="imageId" value="<%= image.getId() %>"/>
				<input type="hidden" name="imgDims"
					value='<%= request.getAttribute("displayDims") %>'/>
				x: <input name="x" size="3"/>
				y: <input name="y" size="3"/>
				w: <input name="w" size="3"/>
				h: <input name="h" size="3"/><br/>
				keywords: <input name="keywords"/><br/>
				description: <input name="title"><br/>
				<input type="submit" value="Save"/>
			</html:form>
		</div>

	<div>
		<b>Category</b>: <q><c:out value="${image.catName}"/></q>
		<b>Keywords</b>:
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

	<div class="comments">
		<h1>Current Annotations</h1>

		<c:forEach var="region" items="${image.annotations}">

			<div class="commentheader">
				At <c:out value="${region.timestamp}"/>,
					<c:out value="${region.user.realname}"/> made annotation
					#<c:out value="${region.id}"/> at
				<c:out
					value="${region.x},${region.y} @ ${region.width}x${region.height}"/>
			</div>
			<div class="commentbody">
				Keywords:  <i><c:forEach var="kw" items="${region.keywords}">
					<c:out value="${kw.keyword}"/>
				</c:forEach></i><br/>
				Title:  <c:out value="${region.title}"/>
			</div>

		</c:forEach>
	</div>

<%-- arch-tag: B457C4EC-800F-43F0-A299-4202692C1466 --%>
