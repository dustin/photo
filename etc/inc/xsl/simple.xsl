<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- import common stuff -->
<xsl:import href="common.xsl"/>

<!-- set output method -->
<xsl:output method="html"/>

<!--
 Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 $Id: simple.xsl,v 1.4 2000/12/27 06:05:25 dustin Exp $
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
				</center>
				<!-- Here's where all the real body goes -->
				<xsl:apply-templates/>

				<hr/>
				Logged in as
				<a href="{meta_stuff/self_uri}?func=credform">
				<xsl:value-of select="meta_stuff/photo_user/username"/></a>.
				<br/>
				Switch to
				<a href="{meta_stuff/self_uri}?func=setstylesheet&amp;stylesheet=default">default</a>
				view.
				<p>
				<font size="-2">
				Copyright &#169; 1997-2000 Dustin Sallings of
				<a href="http://www.spy.net/">SPY internetworking</a><br/>
				All images and other data
				within these pages are property of their owners,
				and may not be used without permission.</font>
				</p>

		</body>
	</html>

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
	<xsl:for-each select="search_result_row">
		<xsl:for-each select="search_result">

			<a href="{/page/meta_stuff/self_uri}?func=display&amp;search_id={ID}">
				<img border="0"
				src="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={IMAGE}&amp;thumbnail=1"/>
			</a><br/>
			Keywords: <xsl:value-of select="KEYWORDS"/><br/>
			Category: <xsl:value-of select="CAT"/><br/>
			Size:  <xsl:value-of select="SIZE"/><br/>
			Taken:  <xsl:value-of select="TAKEN"/><br/>
			Added: <xsl:value-of select="TS"/> by
			<xsl:value-of select="ADDEDBY"/><br/>

			<blockquote>
			<xsl:value-of select="DESCR"/>
				</blockquote>
			<hr/>
		</xsl:for-each> <!-- Individual result -->
	</xsl:for-each> <!-- row of results -->
</xsl:template>

<!-- For displaying an individual image -->
<xsl:template match="show_image">
	<center>
		<img src="{/page/meta_stuff/self_uri}?func=getimage&amp;photo_id={IMAGE}"/>
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
	<ul>
		<xsl:for-each select="cat_view_item">
			<li>
				<xsl:value-of select="category"/>:
				<a href="{link}">
					<xsl:value-of select="count"/>
					<xsl:value-of select="qualifier"/>
				</a>
			</li>
		</xsl:for-each>
	</ul>
</xsl:template>

<!-- Change password form -->
<xsl:template match="change_password_form">

	<form method="POST" action="{/page/meta_stuff/self_uri}">
		<input type="hidden" name="func" value="changepw"/>

		Old Password:  <input type="password" name="oldpw"/><br/>
		New Password:  <input type="password" name="newpw1"/><br/>
		New Password (confirm):  <input type="password" name="newpw2"/><br/>
		<input type="submit" value="Set Password"/>
	</form>

</xsl:template>

<xsl:template match="changed_password">
	<xsl:if test="error">
		Error saving password:  <xsl:value-of select="error"/>
	</xsl:if>
	<xsl:if test="ok">
		Password save complete.
	</xsl:if>
</xsl:template>

<!-- Authenticate form -->
<xsl:template match="auth_form">
	<form method="POST" action="{/page/meta_stuff/self_uri}">
		<input type="hidden" name="func" value="setcred"/>

		Username:  <input name="username" size="8"/><br/>
		Password:  <input type="password" name="password"/><br/>
		<input type="submit" value="Authenticate"/><br/>
		<input type="reset" value="Clear"/><br/>
	</form>
	<p/>
	<a href="{/page/meta_stuff/self_uri}?func=setadmin">Request
		Administrative Privileges</a>
	<br/>
	<a href="{/page/meta_stuff/self_uri}?func=changepwform">Change Password</a>
</xsl:template>

</xsl:stylesheet>
