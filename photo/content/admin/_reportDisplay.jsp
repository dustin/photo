<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>

<h1><c:out value="${reportName}"/></h1>

<table class="reportResults">

<tr>
	<th>RowNum</th>
	<c:forEach var="col" items="${columnNames}">
		<th><c:out value="${col}"/></th>
	</c:forEach>
</tr>

<c:forEach var="row" varStatus="st" items="${sptResults}">
	<tr>
		<td><c:out value="${st.index + 1}"/></td>
		<c:forEach var="col" varStatus="cst" items="${row}">
			<td>
				<c:choose>
					<%-- Special case for image ID --%>
					<c:when test="${columnNames[cst.index] eq 'image_id'}">
						<bean:define type="java.lang.String"
							id="imgId"><c:out value="${col}"/></bean:define>
						<photo:imgLink id='<%= imgId %>'>
							<c:out value="${col}"/>
						</photo:imgLink>
					</c:when>
					<%-- Special case for user ID --%>
					<c:when test="${columnNames[cst.index] eq 'user_id'}">
						<bean:define type="java.lang.String"
							id="userId"><c:out value="${col}"/></bean:define>
						<% String theLink = "/admuseredit.do?userId=" + userId; %>
						<photo:link url="<%= theLink %>">
							<c:out value="${col}"/>
						</photo:link>
					</c:when>
					<%-- All other cases --%>
					<c:otherwise>
						<c:out value="${col}"/>
					</c:otherwise>
				</c:choose>
			</td>
		</c:forEach>
	</tr>
</c:forEach>

</table>

<c:forEach var="sptParam" varStatus="st" items="${sptParams}">
	<c:if test="${st.first}">
		<h1>Adjust Report Parameters</h1>
		<form method="post"
					action="<%= request.getAttribute("rurl").toString() %>">
				<table class="reportForm">
					<tr>
						<th>Field</th>
						<th>Value</th>
					</tr>
	</c:if>
					<tr>
						<td><c:out value="${sptParam.name}" /></td>
						<td>
							<input
								name='<c:out value="${sptParam.param}"/>'
								value='<c:out value="${sptParam.value}"/>' />
						</td>
					</tr>
	<c:if test="${st.last}">
	
			</table>
			<html:submit>Report</html:submit>
		</form>
	</c:if>
</c:forEach>
<%-- arch-tag: CAA34BF2-5D6F-11D9-BE6C-000A957659CC --%>
