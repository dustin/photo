<%@ page import="net.spy.photo.SavedSearch" %>

<jsp:useBean id="props" class="net.spy.photo.PhotoProperties" />

<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>
<table width="100%">
	<tr valign="top">
		<td width="50%">
			<p>
				<div class="sectionheader">Options</div>
				<ul>
					<photo:guest>
						<li><photo:link url="/credform.do"
							message="index.links.login"/></li>
					</photo:guest>
					<photo:guest negate="true">
						<li><photo:link url="/logout.do"
							message="index.links.logout"/></li>
						<li><photo:link url="/addform.do"
							message="index.links.addform"/></li>
						<li><photo:link url="/saveGalleryForm.do"
							message="index.links.savegallery"/></li>
					</photo:guest>
					<li><photo:link url="/galleryList.do"
						message="index.links.listgalleries"/></li>
					<li><photo:link url="/searchForm.do"
						message="index.links.advsearch"/></li>
					<li><photo:link url="/catview.do"
						message="index.links.catview"/></li>
					<li><photo:link url="/listcomments.do"
						message="index.links.comments"/></li>
					<li><photo:link url="/newUserForm.do"
						message="index.links.newuser"/></li>
				</ul>
			</p>
		</td>
		<td width="50%">
			<p>
				<div class="sectionheader">Canned Searches</div>

				<ul>
					<logic:iterate id="i"
						collection="<%= SavedSearch.getSearches() %>"
						type="net.spy.photo.SavedSearch">
						<% String theSearchUrl="/search.do?" + i.getSearch(); %>
						<li><photo:link url="<%= theSearchUrl %>"><%=
							i.getName() %></photo:link></li>
					</logic:iterate>
				</ul>
			</p>
		</td>
	</tr>

	<tr valign="top">
		<td width="50%">
			<p>
				<div class="sectionheader">Photo of the [Unit of Time]</div>

				<div align="center">
					<photo:imgLink id='<%= props.getProperty("photo_of_uot", "1") %>'
						alt="Image of the [Unit of Time]"
						showThumbnail='true'/>
				</div>
			</p>
		</td>
		<td width="50%">
			<p>
				<div class="sectionheader">Credits</div>

				<bean:message key="index.content.credits"/>

			</p>
		</td>
	</tr>
	<photo:admin>
		<tr>
			<td width="50%">
				<p>
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
