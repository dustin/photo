<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- Import the common stuff -->
<xsl:import href="common.xsl"/>

<!-- Declare the output method -->
<xsl:output method="html"/>

<!--
 Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 $Id: default.xsl,v 1.2 2000/11/10 07:17:18 dustin Exp $
 -->

<xsl:template match="page">

	<html>
		<head>
			<title>
				<xsl:value-of select="heading/title"/>
			</title>
		</head>
		<body bgcolor="#cFcFfF">
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
						Logged in as
						<a href="{meta_stuff/self_uri}?func=credform">
						<xsl:value-of select="meta_stuff/username"/></a>.
						<br/>
						Switch to
						<a href="{meta_stuff/self_uri}?func=setstylesheet&amp;stylesheet=simple">simple</a>
						view.
						<p>
						<font size="-2">
						Copyright &#169; 1997-2000 Dustin Sallings of
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
				<xsl:apply-templates
					select="sections/section[@name='options']"/>
				<p>
				<font size="+1"><b>Photo of the [Unit of Time]</b></font><br/><br/>
				<center>
				<xsl:variable name="photo_of_the_day" select="445"/>
				<a href="{/page/meta_stuff/self_uri}?func=display&amp;id={$photo_of_the_day}">
				<img border="0" src="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={$photo_of_the_day}&amp;thumbnail=1"/>
				</a>
				</center>
				</p>
			</td>

			<td>
				<xsl:apply-templates
					select="sections/section[@name='canned_searches']"/>
			</td>
		</tr>
		<tr valign="top">
			<td>
				<xsl:apply-templates
					select="sections/section[@name='Credits']"/>
			</td>
			<td>
				<xsl:apply-templates
					select="sections/section[@name='quick_search']"/>
			</td>
		</tr>
	</table>
	This database contains about
	<xsl:value-of select="/page/meta_stuff/total_images"/> images and has
	displayed about
	<xsl:value-of select="/page/meta_stuff/total_images_shown"/>.
</xsl:template>

<!-- Handling Search Results -->

<xsl:template match="search_results_page">
	<xsl:apply-templates select="sections"/>

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
	<table border="0">
		<xsl:for-each select="search_result_row">
			<tr>
				<xsl:for-each select="search_result">
					<td>
						<table>
							<tr>
								<td>
									Keywords: <xsl:value-of select="KEYWORDS"/>
										<br/>
									Category: <xsl:value-of select="CAT"/><br/>
									Size:  <xsl:value-of select="WIDTH"/>x<xsl:value-of select="HEIGHT"/>
									(<xsl:value-of select="SIZE"/> bytes)<br/>
									Taken:  <xsl:value-of select="TAKEN"/><br/>
									Added: <xsl:value-of select="TS"/> by
										<xsl:value-of select="ADDEDBY"/><br/>
								</td>
								<td>
									<a
									href="{/page/meta_stuff/self_uri}?func=display&amp;search_id={ID}">
										<img border="0"
											width="{TN_WIDTH}"
											height="{TN_HEIGHT}"
											src="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={IMAGE}&amp;thumbnail=1"/>
										</a>
								</td>
							</tr>
						</table>
						<blockquote>
							<xsl:value-of select="DESCR"/>
						</blockquote>
					</td>
				</xsl:for-each> <!-- Individual result -->
			</tr>
		</xsl:for-each> <!-- row of results -->
	</table>
</xsl:template>

<!-- For displaying an individual image -->
<xsl:template match="show_image">
	<table width="100%"> <!-- % -->
		<tr>
			<td align="left">
				<xsl:apply-templates select="meta_stuff/prev"/>
			</td>
			<td align="right">
				<xsl:apply-templates select="meta_stuff/next"/>
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
	Info:<br/><xsl:value-of select="DESCR"/><p/>

	<a href="{/page/meta_stuff/self_uri}?func=logview&amp;view=viewers&amp;which={IMAGE}">
	Who's seen this?</a><br/>
	<a href="{/page/meta_stuff/self_uri}?func=display&amp;id={IMAGE}">Linkable image</a><br/>

</xsl:template>

<!-- category view -->
<xsl:template match="cat_view">
	<table border="1">
		<tr>
			<th>Category</th>
			<th>Entries</th>
		</tr>
		<xsl:for-each select="cat_view_item">
			<tr>
				<td><a href="{link}"><xsl:value-of select="category"/></a></td>
				<td><a href="{link}">
						<xsl:value-of select="count"/>
						<xsl:value-of select="qualifier"/>
					</a></td>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>

</xsl:stylesheet>
