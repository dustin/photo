<%@ page import="net.spy.photo.SavedSearch" %>

<jsp:useBean id="props" class="net.spy.photo.PhotoProperties" />

<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>
<table width="100%">
	<tr valign="top">
		<td width="50%">
			<p>
				<template:insert template='/templates/section_header.jsp'>
					<template:put name='title' content='Options' direct='true'/>
				</template:insert>
				<ul>
					<photo:guest>
						<li><photo:link url="/credform.jsp"
							message="index.links.login"/></li>
					</photo:guest>
					<photo:guest negate="true">
						<li><photo:link url="/logout.do"
							message="index.links.logout"/></li>
						<li><photo:link url="/addform.jsp"
							message="index.links.addform"/></li>
						<li><photo:link url="/savegallery.jsp"
							message="index.links.savegallery"/></li>
					</photo:guest>
					<li><photo:link url="/listgalleries.jsp"
						message="index.links.listgalleries"/></li>
					<li><photo:link url="/search.jsp"
						message="index.links.advsearch"/></li>
					<li><photo:link url="/catview.jsp"
						message="index.links.catview"/></li>
					<li><photo:link url="/listcomments.do"
						message="index.links.comments"/></li>
					<li><photo:link url="/newuserform.jsp"
						message="index.links.newuser"/></li>
				</ul>
			</p>
		</td>
		<td width="50%">
			<p>
				<template:insert template='/templates/section_header.jsp'>
					<template:put name='title'
						content='Canned Searches' direct='true'/>
				</template:insert>

				<ul>
					<logic:iterate id="i"
						collection="<%= SavedSearch.getSearches() %>">
						<% SavedSearch s=(SavedSearch)i; %>
						<li><a href="search.do?<%= s.getSearch() %>"><%=
							s.getName() %></a></li>
					</logic:iterate>
				</ul>
			</p>
		</td>
	</tr>

	<tr valign="top">
		<td width="50%">
			<p>
				<template:insert template='/templates/section_header.jsp'>
					<template:put name='title'
						content='Photo of the [Unit of Time]' direct='true'/>
				</template:insert>

				<div align="center">
					<photo:imgLink id='<%= props.getProperty("photo_of_uot", "1") %>'
						width="194" height="146"
						alt="Image of the [Unit of Time]"
						showThumbnail='true'/>
				</div>
			</p>
		</td>
		<td width="50%">
			<p>
				<template:insert template='/templates/section_header.jsp'>
					<template:put name='title'
						content='Credits' direct='true'/>
				</template:insert>

				<bean:message key="index.content.credits"/>

			</p>
		</td>
	</tr>
	<photo:admin>
		<tr>
			<td width="50%">
				<p>
					<template:insert template='/templates/section_header.jsp'>
						<template:put name='title'
							content='Admin Menu' direct='true'/>
					</template:insert>

					<ul>
						<li>
							<photo:link url="/admin/users.jsp"
								message="index.links.admin.user"/>
						</li>
						<li>
							<photo:link url="/admin/categories.jsp"
								message="index.links.admin.cat"/>
						</li>
						<li>
							<photo:link url="/admin/newprofile.jsp"
								message="index.links.admin.newprofile"/>
						</li>
						<li>
							<photo:link url="/adminify.do?action=unsetadmin"
								message="index.links.admin.droppriv"/>
						</li>
					</ul>

				</p>
			</td>
			<td width="50%">
			</td>
	</tr>
	</photo:admin>
</table>

<p>
	<photo:showMetaInfo/>
</p>
