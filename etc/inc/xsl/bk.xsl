<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<!--
 Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 $Id: bk.xsl,v 1.2 2000/11/10 07:17:18 dustin Exp $
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
						</td></tr>
					</table>
					</td></tr>
				</table>

			</center>
		</body>
	</html>

</xsl:template>

<xsl:template match="title">
	<!-- Don't do title again -->
</xsl:template>

<xsl:template match="image">
	<!-- Don't do image again -->
</xsl:template>

<xsl:template match="list">
	<center>
		<b><xsl:value-of select="@title"/></b>
	</center>
	<ul>
		<xsl:apply-templates/>
	</ul>
</xsl:template>

<xsl:template match="item">
	 <li><a href="{@link}"><xsl:apply-templates/></a></li>
</xsl:template>

<xsl:template match="section">
	<p>
		<font size="+1"><b><xsl:value-of select="title"/></b></font><br/><br/>
		<xsl:apply-templates select="content"/>
	</p>
</xsl:template>

<!-- Handle (ignore) HTML forms. -->

<xsl:attribute-set name="input-stuff">
	<xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
	<xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
	<xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="select-stuff">
	<xsl:attribute name="size"><xsl:value-of select="@size"/></xsl:attribute>
	<xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="option-stuff">
	<xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="textarea-stuff">
	<xsl:attribute name="name">
		<xsl:value-of select="@name"/>
	</xsl:attribute>
	<xsl:attribute name="cols">
		<xsl:value-of select="@cols"/>
	</xsl:attribute>
	<xsl:attribute name="rows">
		<xsl:value-of select="@rows"/>
	</xsl:attribute>
	<xsl:attribute name="wrap">
		<xsl:value-of select="@wrap"/>
	</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="form-stuff">
	<xsl:attribute name="method">
		<xsl:value-of select="@method"/>
	</xsl:attribute>
	<xsl:attribute name="action">
		<xsl:value-of select="@action"/>
	</xsl:attribute>
	<xsl:attribute name="enctype">
		<xsl:value-of select="@enctype"/>
	</xsl:attribute>
</xsl:attribute-set>

<xsl:template match="form">
	<xsl:copy use-attribute-sets="form-stuff">
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>
<xsl:template match="input">
	<xsl:copy use-attribute-sets="input-stuff">
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>
<xsl:template match="select">
	<xsl:copy use-attribute-sets="select-stuff">
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>
<xsl:template match="option">
	<xsl:copy use-attribute-sets="option-stuff">
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>
<xsl:template match="textarea">
	<xsl:copy use-attribute-sets="textarea-stuff">
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<!-- Handling Search Results -->

<xsl:template match="search_results">
	<xsl:value-of select="linktomore/remaining"/> results remaining.<p/>
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
									Size:  <xsl:value-of select="SIZE"/><br/>
									Taken:  <xsl:value-of select="TAKEN"/><br/>
									Added: <xsl:value-of select="TS"/> by
										<xsl:value-of select="ADDEDBY"/><br/>
								</td>
								<td>
									<a
									href="{SELF_URI}?func=display&amp;search_id={ID}">
										<img border="0"
											width="{TN_WIDTH}"
											height="{TN_HEIGHT}"
											src="{SELF_URI}?func=getimage&amp;photo_id={IMAGE}&amp;thumbnail=1"/>
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

<xsl:template match="linktomore">
	<form method="POST" action="{SELF_URI}">
		<input type="hidden" name="func" value="nextresults"/>
		<input type="hidden" name="startfrom" value="{startfrom}"/>
		<input type="submit" value="Next {nextpage}"/>
	</form>
</xsl:template>

<!-- For displaying an individual image -->
<xsl:template match="show_image">
	<center>
		<img src="{SELF_URI}?func=getimage&amp;photo_id={IMAGE}"/>
	</center>
	<p/>
	Category:  <xsl:value-of select="CAT"/><p/>
	Size:  <xsl:value-of select="SIZE"/><p/>
	Taken:  <xsl:value-of select="TAKEN"/><p/>
	Taken:  <xsl:value-of select="TAKEN"/><p/>
	Added:  <xsl:value-of select="TS"/> by <xsl:value-of select="ADDEDBY"/><p/>
	Keywords:  <xsl:value-of select="KEYWORDS"/><p/>
	Info:<br/><xsl:value-of select="DESCR"/><p/>
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

<xsl:template match="br">
	<xsl:copy><xsl:apply-templates/></xsl:copy>
</xsl:template>
<xsl:template match="tr">
	<xsl:copy><xsl:apply-templates/></xsl:copy>
</xsl:template>
<xsl:template match="th">
	<xsl:copy><xsl:apply-templates/></xsl:copy>
</xsl:template>
<xsl:template match="td">
	<xsl:copy><xsl:apply-templates/></xsl:copy>
</xsl:template>
<xsl:template match="table">
	<xsl:copy><xsl:apply-templates/></xsl:copy>
</xsl:template>

</xsl:stylesheet>
