<%@ page import="net.spy.photo.SavedSearch" %>

<jsp:useBean id="props" class="net.spy.photo.PhotoProperties" />

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<table style="width: 100%">
	<tr valign="top">
		<td style="width: 50%">
			<div class="sectionheader">Options</div>
			<ul>
				<photo:guest>
					<li><photo:link url="/credform.do"
						message="index.links.login"/></li>
				</photo:guest>
				<li><photo:link url="/searchForm.do"
					message="index.links.advsearch"/></li>
				<li><photo:link url="/listcomments.do"
					message="index.links.comments"/></li>
				<li><photo:link url="/galleryList.do"
					message="index.links.listgalleries"/></li>
				<li><photo:link url="/catview.do"
					message="index.links.catview"/></li>
				<li><photo:link url="/newUserForm.do"
					message="index.links.newuser"/></li>
				<photo:guest negate="true">
					<li><photo:link url="/addform.do"
						message="index.links.addform"/></li>
					<li><photo:link url="/saveGalleryForm.do"
						message="index.links.savegallery"/></li>
					<li><photo:link url="/logout.do"
						message="index.links.logout"/></li>
				</photo:guest>
			</ul>
		</td>
		<td style="width: 50%">
			<div class="sectionheader">Canned Searches</div>

			<ul>
				<logic:iterate id="i"
					collection="<%= SavedSearch.getSearches() %>"
					type="net.spy.photo.SavedSearch">
					<% String theSearchUrl="/search.do?" + i.getSearchURL(); %>
					<li><photo:link url="<%= theSearchUrl %>"><%=
						i.getName() %></photo:link></li>
				</logic:iterate>
			</ul>
		</td>
	</tr>

	<tr valign="top">
		<td style="width: 50%">
			<div class="sectionheader">Photo of the [Unit of Time]</div>

			<div class="centered">
				<photo:imgLink id='<%= props.getProperty("photo_of_uot", "1") %>'
					alt="Image of the [Unit of Time]"
					showThumbnail='true'/>
			</div>
		</td>
		<td style="width: 50%">
			<div class="sectionheader">Credits</div>
			<fmt:message key="index.content.credits"/>
		</td>
	</tr>
	<photo:admin>
		<tr>
			<td style="width: 50%">
				<div class="sectionheader">Admin Menu</div>

				<ul>
					<li>
						<photo:link url="/admin/reporting.do"
							message="index.links.admin.reporting"/>
					</li>
					<li>
						<photo:link url="/admin/userList.do"
							message="index.links.admin.user"/>
					</li>
					<li>
						<photo:link url="/admin/catList.do"
							message="index.links.admin.cat"/>
					</li>
					<li>
						<photo:link url="/admin/newprofile.do"
							message="index.links.admin.newprofile"/>
					</li>
					<li>
						<photo:link url="/admin/properties.do"
							message="index.links.admin.properties"/>
					</li>
					<li>
						<photo:link url="/adminify.do?action=unsetadmin"
							message="index.links.admin.droppriv"/>
					</li>
				</ul>
			</td>
			<td style="width: 50%">
			</td>
	</tr>
	</photo:admin>
</table>

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
