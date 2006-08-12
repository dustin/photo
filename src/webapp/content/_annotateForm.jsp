<%@ page import="net.spy.photo.PhotoImageData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<%
	PhotoImageData image=(PhotoImageData)request.getAttribute("image");

	String searchIdS=(String)request.getParameter("search_id");
%>

<script type="text/javascript">
var url='<c:url value="/ajax/photo/annotation"/>';
// <![CDATA[
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
		var imageDiv=$("hlimage");
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

		// Update the box display
		var boxStyle=$("zoomBox").style;
		boxStyle.position='absolute';
		boxStyle.left=Math.min(boxOb.boxX1, boxOb.boxX2) + "px";
		boxStyle.top=Math.min(boxOb.boxY1, boxOb.boxY2) + "px";
		boxStyle.width=Math.abs(boxOb.boxX1 - boxOb.boxX2) + "px";
		boxStyle.height=Math.abs(boxOb.boxY1 - boxOb.boxY2) + "px";

		event.preventDefault();
	}

	function setupTag(event) {
		document.removeEventListener("mousemove", movingHighlight, true);
		document.removeEventListener("mouseup", setupTag, true);

		$('x').value=Math.min(boxOb.boxX1, boxOb.boxX2) - boxOb.imgX;
		$('y').value=Math.min(boxOb.boxY1, boxOb.boxY2) - boxOb.imgY;
		$('w').value=Math.abs(boxOb.boxX1 - boxOb.boxX2);
		$('h').value=Math.abs(boxOb.boxY1 - boxOb.boxY2);
		var formDiv=$("annotateFormDiv");
		formDiv.style.display="block";

		event.preventDefault();
	}

	function clearSelection() {
		var boxDiv=$("zoomBox");
		boxDiv.style.width="0px;";
		boxDiv.style.height="0px;";
	}

	function submitAnnotation() {
		if($F('annTitle') == '') {
			alert("You need at least a description to annotate.");
			return(false);
		}
		var x=$F("x");
		var y=$F("y");
		var width=$F("w");
		var height=$F("h");

		clearSelection();
		new Ajax.Request(url, {
			method: 'post',
			postBody: Form.serialize('annForm'),
			onComplete: function(req) {
			},
			onFailure: function(req) {
				alert("Failed to add annotation: " + req.responseText);
			},
			onSuccess: function(req) {
				var h=document.createElement("div");
				h.className="commentheader";
				h.appendChild(document.createTextNode("You just added the following at "
					+ x + "," + y + " @ " + width + "x" + height));
				var b=document.createElement("div");
				b.className="commentbody";
				b.appendChild(document.createTextNode('Keywords:	'));
				var i=document.createElement("i");
				i.appendChild(document.createTextNode($F('annKeywords')));
				b.appendChild(i);
				b.appendChild(document.createElement("br"));
				b.appendChild(document.createTextNode('Title: ' + $F('annTitle')));
				$('comments').appendChild(h);
				$('comments').appendChild(b);
			}
			});
	}

	Ajax.Responders.register({
		onCreate: function() {
			if(Ajax.activeRequestCount>0) {
		Form.disable("annForm");
		Element.show("indicator");
			}
		},
		onComplete: function() {
			if(Ajax.activeRequestCount==0) {
				Form.enable("annForm");
				Element.hide("indicator");
			}
		}
		});

	Event.observe(window, 'load', function() {
		Element.setOpacity('zoomBox', 0.5);
	}, false);
// ]]>
</script>


	<div id="hlimage">
		<photo:imgSrc id='<%= image.getId() %>' showOptimal="true"
			onMouseDown="showHighlight(event);"/>
	</div>

		<div id="zoomBox"></div>
		<div id="annotateFormDiv" style="display: none;">
			<form method="post" action="#" id="annForm"
				onsubmit="submitAnnotation(); return false;">
				<div>
					<input type="hidden" name="imgId" value="<%= image.getId() %>"/>
					<input type="hidden" name="imgDims"
						value='<%= request.getAttribute("displayDims") %>'/>
					x: <input id="x" name="x" size="3"/>
					y: <input id="y" name="y" size="3"/>
					w: <input id="w" name="w" size="3"/>
					h: <input id="h" name="h" size="3"/>
				</div>
				<div>
					keywords: <input id="annKeywords" name="keywords"/>
				</div>
				<div>
					description: <input id="annTitle" name="title"/>
				</div>
				<div>
					<input type="submit" value="Save"/>
					<img src="<c:url value='/images/indicator.gif'/>"
						alt="indicator" id="indicator" style="display: none"/>
				</div>
			</form>
		</div>

	<div>
		<b>Category</b>: <q><c:out value="${image.catName}"/></q>
		<b>Keywords</b>:
			<i><c:forEach var="kw" items="${image.keywords}">
				<c:out value="${kw.keyword}"/>
			</c:forEach></i><br/>
				<b>Size</b>:	<c:out value="${image.dimensions}"/>
			(<c:out value="${image.size}"/> bytes)<br />
		<b>Taken</b>:  <c:out value="${image.taken}"/> <b>Added</b>:
			<c:out value="${image.timestamp}"/>
		by <c:out value="${image.addedBy.realname}"/><br />
		<b>Info</b>:
		<div id="imgDescr"><c:out value="${image.descr}"/></div>

	</div>

	<div id="comments" class="comments">
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
				Title:	<c:out value="${region.title}"/>
			</div>

		</c:forEach>
	</div>

<%-- arch-tag: B457C4EC-800F-43F0-A299-4202692C1466 --%>
