<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<div class="comments">
	<c:forEach var="commentList" items="${photoSession.comments.page}">
		<div class="commentblock">

			<div class="commentimage">
				<c:set var="dUrl">
					<c:url value="/display.do">
						<c:param name="id" value="${commentList.photoId}"/>
					</c:url>
				</c:set>
				<c:set var="iUrl">
					<c:url value="/PhotoServlet">
						<c:param name="id" value="${commentList.photoId}"/>
						<c:param name="thumbnail" value="1"/>
					</c:url>
				</c:set>
				<a href="<c:out value='${dUrl}'/>">
					<img src="<c:out value='${iUrl}'/>"/></a>
			</div>

			<c:forEach var="comment" items="${commentList.allObjects}">
				<div class="commentheader">
					At <c:out value="${comment.timestamp}"/>
						<c:out value="${comment.user.realname}"/> said the following:
				</div>
				<div class="commentbody">
					<c:out value="${comment.note}"/>
				</div>
			</c:forEach>

			<c:if test="${commentList.moreAvailable}">
				<div class="commentmore">
					<a href="<c:out value='${dUrl}'/>">
						More comments available on the image page.
					</a>
				</div>
			</c:if>

		</div>
	</c:forEach>
</div>

<c:if test="${photoSession.comments.numRemaining > 0}">
	<div>
	<html:form action="nextcomments.do">
		<div>
			<input type="hidden" name="startOffset"
				value="<c:out value='${photoSession.comments.pageNumber + 1}'/>"/>
			<input type="hidden" name="whichCursor" value="comments"/>
			<html:submit>
				<c:out value="Next ${photoSession.comments.nextPageSize}"/>
			</html:submit>
		</div>
	</html:form>
	</div>
</c:if>
