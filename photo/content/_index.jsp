<%@ page import="net.spy.photo.SavedSearch" %>
<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
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
						<li><a href="credform.jsp">Login</a></li>
					</photo:guest>
					<photo:guest negate="1">
						<li><a href="PhotoServlet?func=addform">Add a New Image</a></li>
					</photo:guest>
					<li><a href="search.jsp">Advanced Search</a></li>
					<li><a href="PhotoServlet?func=catview">Category View</a></li>
					<li><a href="listcomments.do">Show Recent Comments</a></li>
					<li><a href="PhotoServlet?func=newuserform">Create an Account</a></li>
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
					<photo:imgLink id='3469'
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

				All pages herein were created using vi.  For more
				information on the vi web page publishing system, type
				<i>man vi</i> at your prompt.
			</p>
		</td>
	</tr>
</table>

<p>
	<photo:showMetaInfo/>
</p>
