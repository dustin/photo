<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- Import the common stuff -->
<xsl:import href="common.xsl"/>
<xsl:import href="forms.xsl"/>
<xsl:import href="variables.xsl"/>

<!-- Declare the output method -->
<xsl:output method="html"/>

<!--
 Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 $Id: default.xsl,v 1.23 2001/01/16 21:01:19 dustin Exp $
 -->

<xsl:template match="page">

	<html>
		<head>
			<title>
				<xsl:value-of select="heading/title"/>
			</title>
			<link rel="stylesheet"
			href="{meta_stuff/self_uri}?func=getstylesheet"/>
		</head>
		<body background="/~dustin/images/holiday.gif" bgcolor="#cFcFfF">
			<center>
				<b>
					<font size="+3">
						<xsl:value-of select="heading/title"/>
					</font>
				</b>

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
						</td>
						<td align="right">
							<xsl:call-template name="quick_search"/>
						</td>
						</tr>
						</table>
						<p>
						<font size="-2">
						Copyright &#169; 1997-2001 Dustin Sallings of
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
						<xsl:if test="/page/meta_stuff/photo_user/canadd">
							<li>
								<a href="{/page/meta_stuff/self_uri}?func=addform">Add a new Image</a>
							</li>
						</xsl:if>
						<li>
							<a href="{/page/meta_stuff/self_uri}?func=findform">Advanced Search</a>
						</li>
						<li>
							<a href="{/page/meta_stuff/self_uri}?func=catview">Category View</a>
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
							<img border="0"
								width="{search_result[1]/TN_WIDTH}"
								height="{search_result[1]/TN_HEIGHT}"
								src="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={search_result[1]/IMAGE}&amp;thumbnail=1"/>
						</a>
					</td>
					<td width="25%" bgcolor="#efefff" valign="top">
						<font size="-1">
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
		<img width="{WIDTH}" height="{HEIGHT}"
		  src="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={IMAGE}"/>
	</center>
	<p/>
	Category:  <xsl:value-of select="CAT"/><p/>
	Size:  <xsl:value-of select="WIDTH"/>x<xsl:value-of select="HEIGHT"/>
		(<xsl:value-of select="SIZE"/> bytes)<p/>
	Taken:  <xsl:value-of select="TAKEN"/><p/>
	Added:  <xsl:value-of select="TS"/> by <xsl:value-of select="ADDEDBY"/><p/>
	Keywords:  <xsl:value-of select="KEYWORDS"/><p/>
	Info:<br/><xsl:apply-templates select="DESCR"/><p/>

	<a href="{/page/meta_stuff/self_uri}?func=logview&amp;view=viewers&amp;which={IMAGE}">
	Who's seen this?</a><br/>
	<a href="{/page/meta_stuff/self_uri}?func=display&amp;id={IMAGE}">Linkable image</a><br/>

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

</xsl:stylesheet>
