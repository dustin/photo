<jsp:useBean id="props" class="net.spy.photo.PhotoProperties" />

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<h2>Canned Searches</h2>
<ul>
	<c:forEach var="i" items="${searches}">
		<li>
			<c:set var="su">
				<c:url value="/savedSearch.do">
					<c:param name="searchId" value="${i.id}"/>
				</c:url>
			</c:set>
			<c:out escapeXml="false" value='<a href="${su}">${i.name}</a>'/>
			<span class="searchcardinality">
				Total of <c:out value="${i.count}"/> images.
			</span>
		</li>
	</c:forEach>
</ul>

<h2>Photo of the [Unit of Time]</h2>
<div id="photoOfTheUnitOfTime">
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
