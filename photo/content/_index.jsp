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
						<li><a href="login.do">Login</a></li>
					</photo:guest>
					<photo:guest negate="1">
						<li><a href="PhotoServlet?func=addform">Add a New Image</a></li>
					</photo:guest>
					<li><a href="search.do">Advanced Search</a></li>
					<li><a href="PhotoServlet?func=catview">Category View</a></li>
					<li><a href="PhotoServlet?func=listcomments&start=true">Show Recent Comments</a></li>
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

				<%-- <photo:getSavedSearches/> --%>
				<ul>
					<logic:iterate id="i"
						collection="<%= SavedSearch.getSearches() %>">
						<% SavedSearch s=(SavedSearch)i; %>
						<li><a href="PhotoServlet?<%= s.getSearch() %>"><%=
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

				<center>
					<a href="PhotoServlet?func=display&id=3469"><img
						border="0"
						src="PhotoServlet?func=getimage&photo_id=3469&thumbnail=1"></a>
				</center>
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
