<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- Import the common stuff -->
<xsl:import href="common.xsl"/>
<xsl:import href="forms.xsl"/>
<xsl:import href="variables.xsl"/>

<!-- Declare the output method -->
<xsl:output method="html"/>

<!--
 Copyright (c) 2002  Dustin Sallings <dustin@spy.net>
 -->

<xsl:template match="page">

	<html>
		<head>
			<title>
				<xsl:value-of select="heading/title"/>
			</title>
			<link rel="stylesheet"
				href="{meta_stuff/self_uri}?func=getstylesheet"/>
			<link rel="top" href="{meta_stuff/self_uri}" title="PhotoServlet"/>
			<link rel="author" href="http://bleu.west.spy.net/~dustin/"
				title="Dustin Sallings"/>
			<link rel="search" href="{meta_stuff/self_uri}?func=findform"
				title="PhotoServlet Search"/>

			<xsl:if test="show_image/meta_stuff/next">
				<link rel="next"
					href="{meta_stuff/self_uri}?func=display&amp;search_id={show_image/meta_stuff/next}"
					title="Search Result {show_image/meta_stuff/next}"/>
			</xsl:if>

			<xsl:if test="show_image/meta_stuff/prev">
				<link rel="prev"
					href="{meta_stuff/self_uri}?func=display&amp;search_id={show_image/meta_stuff/prev}"
					title="Search Result {show_image/meta_stuff/prev}"/>
			</xsl:if>

			<xsl:if test="show_image/meta_stuff/last">
				<link rel="first"
					href="{meta_stuff/self_uri}?func=display&amp;search_id=0"
					title="First Search Result"/>
				<link rel="last"
					href="{meta_stuff/self_uri}?func=display&amp;search_id={show_image/meta_stuff/last}"
					title="Last Search Result"/>


				<!-- if there are more, link to them -->
				<link rel="parent"
					title="Search Results"
					value="{meta_stuff/self_uri}?func=nextresults&amp;startfrom=0"/>

			<!-- Has other results -->
			</xsl:if>

		</head>
		<body background="http://bleu.west.spy.net/~dustin/images/holiday.gif" bgcolor="#cFcFfF">
			<center>

				<!-- Title wrapper -->
				<table bgcolor="#000000">
					<tr><td>
						<table border="5" bgcolor="#fFfFfF">
							<tr><td>
								<b>
									<font size="+3">
										<xsl:value-of select="heading/title"/>
									</font>
								</b>
							</td></tr>
						</table>
					</td></tr>
				</table>

				<table bgcolor="#000000" width="75%">
					<tr><td>
					<table bgcolor="#fFfFfF" width="100%">
						<tr><td>
						<p>
							<!-- Here's where all the real body goes -->
							<xsl:apply-templates/>
						</p>

						<hr/>
						<table border="0" width="100%">
						<tr valign="top">
						<td align="left">
						<font size="-1">
						Logged in as
						<a href="{meta_stuff/self_uri}?func=credform">
						<xsl:value-of
							select="meta_stuff/photo_user/username"/></a>
							<xsl:if test="meta_stuff/isadmin">
								<a href="{meta_stuff/self_uri}?func=unsetadmin">(admin mode)</a>
							</xsl:if>
						<br/>
						Switch to
						<a href="{meta_stuff/self_uri}?func=setstylesheet&amp;stylesheet=simple">simple</a>
						view.
						<br/>
						<xsl:choose>
							<xsl:when test="meta_stuff/xmlraw">
								<a href="{meta_stuff/self_uri}?func=xmlraw&amp;to=false">
									Disable XML</a>
							</xsl:when>
							<xsl:otherwise>
								<a href="{meta_stuff/self_uri}?func=xmlraw&amp;to=true">
									Enable XML</a>
							</xsl:otherwise>
						</xsl:choose>
						</font>
						</td>
						<td align="right">
							<xsl:call-template name="quick_search"/>
							<font size="-1">
								<a href="{meta_stuff/self_uri}">[HOME]</a>
								| <a href="{meta_stuff/self_uri}?func=setviewsize">[SIZE]</a>
								</font>
						</td>
						</tr>
						</table>
						<p>
						<font size="-2">
						Copyright &#169; 1997-2002 Dustin Sallings of
						<a href="http://www.spy.net/">SPY
						internetworking</a><br/>
						All images and other data
						within these pages are property of their owners,
						and may not be used without permission.</font>
						</p>
						</td></tr>
					</table>
					</td></tr>
				</table>

			<a href="http://photoservlet.sourceforge.net/">
			<img border="0" width="88" height="31" alt="SourceForge"
			src="http://sourceforge.net/sflogo.php?group_id=7312&amp;type=1"/></a>

			</center>
		</body>
	</html>

</xsl:template>

<xsl:template match="index_page">

	<table>
		<tr valign="top">

			<td>
				<p>
					<xsl:call-template name="section_header">
						<xsl:with-param name="title">
							Options
						</xsl:with-param>
					</xsl:call-template>
					<ul>
						<xsl:if test="/page/meta_stuff/photo_user/username='guest'">
							<li>
								<a href="{/page/meta_stuff/self_uri}?func=credform">Login</a>
							</li>
						</xsl:if>
						<xsl:if test="/page/meta_stuff/photo_user/canadd">
							<li>
								<a href="{/page/meta_stuff/self_uri}?func=addform">
									Add a new Image</a>
							</li>
						</xsl:if>
						<li>
							<a href="{/page/meta_stuff/self_uri}?func=findform">
							Advanced Search</a>
						</li>
						<li>
							<a href="{/page/meta_stuff/self_uri}?func=catview">
								Category View</a>
						</li>
						<li>
							<a href="{/page/meta_stuff/self_uri}?func=listcomments&amp;start=true">
								View Recent Comments</a>
						</li>
						<li>
							<a href="{/page/meta_stuff/self_uri}?func=newuserform">
								Create an Account</a>
						</li>
					</ul>
				</p>

			</td>

			<td>
				<p>
					<xsl:call-template name="section_header">
						<xsl:with-param name="title">
							Canned Searches
						</xsl:with-param>
					</xsl:call-template>
					<ul>
						<xsl:for-each select="saved_searches/item">
							<li>
								<a href="{@link}"><xsl:value-of select="."/></a>
							</li>
						</xsl:for-each>
					</ul>
				</p>
			</td>
		</tr>
		<tr valign="top">

		<td width="50%">
				<p>
					<xsl:call-template name="section_header">
						<xsl:with-param name="title">
							Photo of the [Unit of Time]
						</xsl:with-param>
					</xsl:call-template>
					<center>
					<a href="{/page/meta_stuff/self_uri}?func=display&amp;id={$photo_of_the_day}">
					<img border="0" src="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={$photo_of_the_day}&amp;thumbnail=1"/>
					</a>
					</center>
				</p>
			</td>
			<td>
				<p>
					<xsl:call-template name="section_header">
						<xsl:with-param name="title">Credits</xsl:with-param>
					</xsl:call-template>
					All pages herein were created using vi.  For more
					information on the vi web page publishing system,
					type <i>man vi</i> at your prompt.
				</p>
			</td>
			<td>

			</td>
		</tr>
	</table>

	<!-- If we're in admin mode, display the adminu -->
	<xsl:if test="/page/meta_stuff/isadmin">
		<p>
			<xsl:call-template name="section_header">
				<xsl:with-param name="title">
					Admin Menu
				</xsl:with-param>
			</xsl:call-template>
			<ul>
				<li><a
					href="{/page/meta_stuff/self_uri}?func=admuser">User Admin</a>
				</li>
				<li><a
					href="{/page/meta_stuff/self_uri}?func=admcat">Category Admin</a>
				</li>
				<li><a
					href="{/page/meta_stuff/self_uri}?func=admnewprofileform">New Profile</a>
				</li>
				<li><a
					href="{/page/meta_stuff/self_uri}?func=unsetadmin">Drop Privileges</a>
				</li>
			</ul>
		</p>
	</xsl:if>

	This database contains about
	<xsl:value-of select="/page/meta_stuff/total_images"/> images and has
	displayed about
	<xsl:value-of select="/page/meta_stuff/total_images_shown"/>.
</xsl:template>

<!-- Handling Search Results -->

<xsl:template match="search_results_page">

	<!-- If the user can add, put a little form at the top to save the search -->
	<xsl:if test="/page/meta_stuff/photo_user/canadd">
		<form method="POST" action="{/page/meta_stuff/self_uri}">
			<input type="hidden" name="func" value="savesearch"/>
			<input type="hidden" name="search"
				value="{meta_stuff/search_query}"/>
			Save search as:  <input name="name"/>
			<input type="submit" value="Save"/>
		</form>
	</xsl:if>

	<!-- Let us know how many results we've got -->
	<div align="right">
		<font size="+2">
			Search matched <xsl:value-of select="meta_stuff/total"/> entries.
		</font>
	</div>

	<p>

		<!-- Put the actual results here -->
		<xsl:apply-templates select="search_results"/>

	</p>

	<!-- if there are more, link to them -->
	<xsl:variable name="r" select="meta_stuff/linktomore/remaining"/>
	<xsl:if test="$r>0">
		<xsl:value-of select="$r"/> results remaining.<p/>
		<form method="POST" action="{/page/meta_stuff/self_uri}">
			<input type="hidden" name="func" value="nextresults"/>
			<input type="hidden" name="startfrom"
				value="{meta_stuff/linktomore/startfrom}"/>
			<input type="submit"
				value="Next {meta_stuff/linktomore/nextpage}"/>
		</form>
	</xsl:if>

</xsl:template>

<xsl:template match="search_results">
	<table border="0" colspan="1" width="100%">
		<xsl:for-each select="search_result_row">
			<tr>
				<xsl:if test="search_result[1]">
					<td width="25%" align="center">
						<a href="{/page/meta_stuff/self_uri}?func=display&amp;search_id={search_result[1]/ID}">
							<xsl:choose>
								<xsl:when test="search_result[1]/TN_HEIGHT>0">
									<!-- Include the tn width and height if we have them, as
												an optimization -->
									<img border="0"
										width="{search_result[1]/TN_WIDTH}"
										height="{search_result[1]/TN_HEIGHT}"
										src="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={search_result[1]/IMAGE}&amp;thumbnail=1"/>
								</xsl:when>
								<xsl:otherwise>
									<img border="0"
										src="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={search_result[1]/IMAGE}&amp;thumbnail=1"/>
								</xsl:otherwise>
							</xsl:choose>
						</a>
					</td>
					<td width="25%" bgcolor="#efefff" valign="top">
						<font size="-1">
						ID:  <xsl:value-of select="search_result[1]/IMAGE"/>
							<br/>
						Keywords: <xsl:value-of select="search_result[1]/KEYWORDS"/>
							<br/>
						Category: <xsl:value-of select="search_result[1]/CAT"/><br/>
						Size:  <xsl:value-of select="search_result[1]/WIDTH"/>x<xsl:value-of select="search_result[1]/HEIGHT"/>
						(<xsl:value-of select="search_result[1]/SIZE"/> bytes)<br/>
						Taken:  <xsl:value-of select="search_result[1]/TAKEN"/><br/>
						Added: <xsl:value-of select="search_result[1]/TS"/> by
							<xsl:value-of select="search_result[1]/ADDEDBY"/><br/>
						</font>
					</td>
				</xsl:if>
				<xsl:if test="search_result[2]">
				<td width="25%" bgcolor="#efefff" valign="top">
					<font size="-1">
					ID:  <xsl:value-of select="search_result[2]/IMAGE"/>
						<br/>
					Keywords: <xsl:value-of select="search_result[2]/KEYWORDS"/>
						<br/>
					Category: <xsl:value-of select="search_result[2]/CAT"/><br/>
					Size:  <xsl:value-of select="search_result[2]/WIDTH"/>x<xsl:value-of select="search_result[2]/HEIGHT"/>
					(<xsl:value-of select="search_result[2]/SIZE"/> bytes)<br/>
					Taken:  <xsl:value-of select="search_result[2]/TAKEN"/><br/>
					Added: <xsl:value-of select="search_result[2]/TS"/> by
						<xsl:value-of select="search_result[2]/ADDEDBY"/><br/>
					</font>
				</td>
				<td width="25%" align="center">
					<a href="{/page/meta_stuff/self_uri}?func=display&amp;search_id={search_result[2]/ID}">
						<img border="0"
							width="{search_result[2]/TN_WIDTH}"
							height="{search_result[2]/TN_HEIGHT}"
							src="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={search_result[2]/IMAGE}&amp;thumbnail=1"/>
					</a>
				</td>
				</xsl:if>
			</tr>
			<tr>
				<td colspan="2" width="50%" valign="top" bgcolor="#efefff">
					<blockquote>
						<font size="-1">
							<xsl:apply-templates select="search_result[1]/DESCR"/>
						</font>
					</blockquote>
				</td>
				<xsl:if test="search_result[2]">
				<td colspan="2" width="50%" valign="top" bgcolor="#efefff">
					<blockquote>
						<font size="-1">
							<xsl:apply-templates select="search_result[2]/DESCR"/>
						</font>
					</blockquote>
				</td>
				</xsl:if>
			</tr>
		</xsl:for-each> <!-- row of results -->
	</table>
</xsl:template>

<!-- For displaying an individual image -->
<xsl:template match="show_image">
	<table width="100%">
		<tr valign="top">
			<td align="left" width="10%">
				<xsl:if test="meta_stuff/prev">
					<a href="{/page/meta_stuff/self_uri}?func=display&amp;search_id={meta_stuff/prev}"><img
					alt="&lt;&lt;&lt;" border="0" src="/~dustin/images/l_arrow.gif"/></a>
				</xsl:if>
			</td>
			<td align="center">
				<b>
					<font size="-3">
						<xsl:apply-templates select="DESCR"/>
					</font>
				</b>
			</td>
			<td align="right" width="10%">
				<xsl:if test="meta_stuff/next">
					<a href="{/page/meta_stuff/self_uri}?func=display&amp;search_id={meta_stuff/next}"><img
					alt="&gt;&gt;&gt;" border="0" src="/~dustin/images/r_arrow.gif"/></a>
				</xsl:if>
			</td>
		</tr>
	</table>

	<center>
		<img width="{SCALED_WIDTH}" height="{SCALED_HEIGHT}"
		  src="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={IMAGE}&amp;scale={SCALED_WIDTH}x{SCALED_HEIGHT}"/>
	</center>
	<p/>
	<b>Category</b>:  ``<xsl:value-of select="CAT"/>''&#160;
	<b>Keywords</b>:  <i><xsl:value-of select="KEYWORDS"/></i><br/>
	<b>Size</b>:  <xsl:value-of select="WIDTH"/>x<xsl:value-of select="HEIGHT"/>
		(<xsl:value-of select="SIZE"/> bytes)<br/>
	<b>Taken</b>:  <xsl:value-of select="TAKEN"/>&#160;
	<b>Added</b>:  <xsl:value-of select="TS"/>
		by <xsl:value-of select="ADDEDBY"/><br/>
	<b>Info</b>:<blockquote><xsl:apply-templates select="DESCR"/></blockquote>

	[<a href="{/page/meta_stuff/self_uri}?func=logview&amp;view=viewers&amp;which={IMAGE}">
	Who's seen this?</a>] | 
	[<a href="{/page/meta_stuff/self_uri}?func=display&amp;id={IMAGE}">Linkable image</a>] |
	[<a href="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={IMAGE}">Full Size Image</a>]

	<xsl:if test="comments/photo_comment">
		<p class="comments">
		<h1>Comments:</h1>

		<xsl:for-each select="comments/photo_comment">
			<table class="comments" width="100%">
				<tr class="comment_header">
					<td>At <xsl:value-of select="timestamp"/>&#160;
						<xsl:value-of select="photo_user/realname"/> said the
						following:
					</td>
				</tr>
				<tr class="comment_body">
					<td>
						<xsl:value-of select="note"/>
					</td>
				</tr>
			</table>
		</xsl:for-each>

		</p>
	</xsl:if>

	<p>
	<xsl:if test="/page/meta_stuff/photo_user/username!='guest'">
		Submit a comment:<br/>
		<form action="{/page/meta_stuff/self_uri}" method="POST">
			<input type="hidden" name="func" value="comment"/>
			<input type="hidden" name="image_id" value="{IMAGE}"/>
			<textarea name="comment" wrap="hard" cols="50" rows="2"></textarea>
			<br/>
			<input type="submit" value="Comment"/>
		</form>
	</xsl:if>
	</p>


</xsl:template>

<!-- category view -->
<xsl:template match="category_view">

	<table border="1">
		<tr>
			<th>Category</th>
			<th>Entries</th>
		</tr>
		<xsl:for-each select="cat_view_item">
			<tr>
				<td><a href="{/page/meta_stuff/self_uri}?func=search&amp;cat={cat_n}&amp;maxret=6"><xsl:value-of select="category"/></a></td>
				<td><a href="{/page/meta_stuff/self_uri}?func=search&amp;cat={cat_n}&amp;maxret=6">
						<xsl:value-of select="count"/>
						<xsl:value-of select="qualifier"/>
					</a></td>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>

<!-- Display the results of the password change. -->
<xsl:template match="changed_password">
	<xsl:choose>
		<xsl:when test="error">
			Error saving password:  <xsl:value-of select="error"/>
		</xsl:when>
		<xsl:when test="ok">
			Password save complete.
		</xsl:when>
	</xsl:choose>
</xsl:template>

<xsl:template match="upload_success">
	Well, it looks like your image made it.
	The ID is <xsl:value-of select="id"/>.  It can be seen by clicking
	<a href="{/page/meta_stuff/self_uri}?func=display&amp;id={id}">here</a>.
</xsl:template>

<xsl:template match="save_search_success">
	Your search has been saved, press the ``back'' button on your browser
	to continue with your search.
</xsl:template>

<xsl:template match="no_such_user">
	The username or E-mail address ``<xsl:value-of select="id"/>'' does not
exist.  Please check the number and try again.
</xsl:template>

<xsl:template match="generated_password">
	A new password for user ``<xsl:value-of select="username"/>'' has been
E-mailed to you.
</xsl:template>

<xsl:template match="profile_add_user">
	<form method="POST" action="{/page/meta_stuff/self_uri}">
		<input type="hidden" name="func" value="adduser"/>
		<center>
		<p>
		Registering an account requires a valid profile ID.  If you don't
		have one, you probably shouldn't be here.
		</p>
		<table>
			<tr>
				<td>Profile</td>
				<td><input name="profile"/></td>
			</tr>
			<tr>
				<td>Username</td>
				<td><input name="username"/></td>
			</tr>
			<tr>
				<td>Password</td>
				<td><input type="password" name="password"/></td>
			</tr>
			<tr>
				<td>Password (again)</td>
				<td><input type="password" name="pass2"/></td>
			</tr>
			<tr>
				<td>Name</td>
				<td><input name="realname"/></td>
			</tr>
			<tr>
				<td>Email</td>
				<td><input name="email"/></td>
			</tr>
			<tr>
				<td colspan="2">
					<center>
						<input type="submit" value="Add Me"/>
						<input type="reset" value="Clear"/>
					</center>
				</td>
			</tr>
		</table>
		</center>
	</form>
</xsl:template>

<xsl:template match="all_comments">
	<table width="100%">
		<xsl:for-each select="photo_comment">
			<tr valign="top">
				<td>
					<a href="{/page/meta_stuff/self_uri}?func=display&amp;id={photo_id}">
						<img border="0"
							src="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={photo_id}&amp;thumbnail=1"/>
					</a>
				</td>

				<td>
					<table class="comments" width="100%">
						<tr valign="top" class="comment_header">
							<td>At <xsl:value-of select="timestamp"/>&#160;
								<xsl:value-of select="photo_user/realname"/> said the
								following:
							</td>
						</tr>
						<tr valign="top" class="comment_body">
							<td>
								<xsl:value-of select="note"/>
							</td>
						</tr>
					</table>

				</td>
			</tr>
		</xsl:for-each>
	</table>

	<br/>

	<!-- if there are more, link to them -->
	<xsl:variable name="r" select="meta_stuff/linktomore/remaining"/>
	<xsl:if test="$r>0">
		<xsl:value-of select="$r"/> results remaining.<p/>
		<form method="POST" action="{/page/meta_stuff/self_uri}">
			<input type="hidden" name="func" value="listcomments"/>
			<input type="hidden" name="startfrom"
				value="{meta_stuff/linktomore/startfrom}"/>
			<input type="submit"
				value="Next {meta_stuff/linktomore/nextpage}"/>
		</form>
	</xsl:if>

</xsl:template>

<xsl:template match="adm_new_profile">
	New profile saved as <xsl:value-of select="."/>.
</xsl:template>

</xsl:stylesheet>
