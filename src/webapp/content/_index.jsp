<jsp:useBean id="props" class="net.spy.photo.PhotoProperties" />

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<script src="js/m/mootools.release.83.js" type="text/javascript"></script>
<script src="js/m/timed.slideshow.js" type="text/javascript"></script>

<script type="text/javascript">
	var searchUrl='<c:url value="/ajax/slideshow"/>';
	var displayBase='<c:url value="/display.do"/>';
	var imgBase='<c:url value="/PhotoServlet"/>';
	// <![CDATA[
	// Set up the UOT slideshow
	function setupUOT(req) {
		var slideData=new Array();

		var imgs=req.responseXML.getElementsByTagName("img");
		for(var i=0; i<imgs.length; i++) {
			var img=imgs[i];
			var imgid=img.getAttribute("id");
			var detailUrl=displayBase + "?id=" + imgid;
			var imgUrl=imgBase + "?thumbnail=1&id=" + imgid;
			var descr=getElementText(img, 'descr', 0);

			slideData.push(new Array(imgUrl, detailUrl, 'Image ' + imgid, descr));
		}
	 var slideshow = new timedSlideShow($('photoOfTheUnitOfTime'), slideData);
	}
	// Load the Unit of Time picture
	function loadUOTData() {
		new Ajax.Request(searchUrl, { method: 'get', onSuccess: setupUOT });
	}
	// Event.observe(window, 'load', loadUOTData, false);
	addLoadEvent(loadUOTData);
	// ]]>
</script>

<logic:present role="admin">
<script type="text/javascript">
	var deleteBase='<c:url value="/deleteSearch.do"/>';
	// <![CDATA[
	function deleteSearch(searchId) {
		if(confirm("You sure you want to delete this search, B?")) {
			Element.show("search_del_indicator_" + searchId);
			new Ajax.Request(deleteBase, {
				method: 'post',
				postBody: $H({searchId: searchId}).toQueryString(),
				onFailure: function(req) {
					alert("Failed to delete.  :(");
				},
				onSuccess: function(req) {
					new Effect.Fade("search_" + searchId);
				},
				});
		}
		return false;
	}
	// ]]>
</script>
</logic:present>

<h2>Canned Searches</h2>
<ul id="savedsearches">
	<c:forEach var="i" items="${searches}">
		<li class="savedsearch" id="search_<c:out value='${i.id}'/>">
			<c:set var="su">
				<c:url value="/savedSearch.do">
					<c:param name="searchId" value="${i.id}"/>
				</c:url>
			</c:set>
			<logic:present role="admin">
				<a class="deletelink" href="#" title="Delete this search"
					onclick="return deleteSearch(<c:out value='${i.id}'/>);">
					<img src="<c:url value='/images/trash.gif'/>" alt="delete"/>
				</a>
				<img src="<c:url value='/images/indicator.gif'/>"
					alt="indicator" style="display: none"
					id="search_del_indicator_<c:out value='${i.id}'/>"/>
			</logic:present>
			<c:out escapeXml="false" value='<a href="${su}">${i.name}</a>'/>
			<span class="searchcardinality">
				Total of <c:out value="${i.count}"/> images.
			</span>
		</li>
	</c:forEach>
</ul>

<h2>Photo of the [Unit of Time]</h2>
<div class="jdSlideshow" id="photoOfTheUnitOfTime">
	<photo:imgLink id='<%= props.getProperty("photo_of_uot", "1") %>'
		alt="Image of the [Unit of Time]" showThumbnail='true'/>
</div>

<h2>Credits</h2>
<fmt:message key="index.content.credits"/>

<div class="metaInfo">
	<photo:metaInfo>
		<%-- This is kind of ugly, but there seems to be a resin jstl bug --%>
		<c:set var="mImgs">
			<fmt:formatNumber><%= metaImages %></fmt:formatNumber>
		</c:set>
		<c:set var="mShwn">
			<fmt:formatNumber><%= metaShown %></fmt:formatNumber>
		</c:set>
		<fmt:message key="index.metainfo">
			<fmt:param><c:out value="${mImgs}" /></fmt:param>
			<fmt:param><c:out value="${mShwn}" /></fmt:param>
		</fmt:message>
	</photo:metaInfo>
</div>
<%-- arch-tag: AE9DD2A8-5D6F-11D9-B583-000A957659CC --%>
