<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<!--
 Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 $Id: common.xsl,v 1.6 2001/01/06 07:19:08 dustin Exp $
 -->

<!-- Default Section handling -->
<xsl:template match="sections">
	<xsl:apply-templates select="section"/>
</xsl:template>

<!-- This is called with a parameter to produce a section header. -->
<xsl:template name="section_header">
	<font size="+1">
		<b>
			<xsl:value-of select="$title"/>
		</b>
	</font>
	<br/><br/>
</xsl:template>

<xsl:template match="section">
	<p>
		<xsl:call-template name="section_header">
			 <xsl:with-param name="title" select="title"/>
		</xsl:call-template>
		<xsl:apply-templates select="content"/>
	</p>
</xsl:template>

<!-- Stuff that should be ignored -->

<xsl:template match="heading">
	<!-- Don't do title again -->
</xsl:template>

<xsl:template match="meta_stuff">
	<!-- Don't process meta_stuff -->
</xsl:template>

<xsl:template match="list">
	<center>
		<b><xsl:value-of select="@title"/></b>
	</center>
	<ul>
		<xsl:apply-templates/>
	</ul>
</xsl:template>

<xsl:template match="ul">
	<xsl:copy>
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="li">
	<xsl:copy>
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="item">
	 <li><a href="{@link}"><xsl:apply-templates/></a></li>
</xsl:template>

<!-- Normal HTML tags (really probably shouldn't be in the XML) -->
<xsl:template match="p">
	<xsl:copy/>
</xsl:template>
<xsl:template match="a">
	<a href="{@href}"><xsl:apply-templates/></a>
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

<xsl:template match="mselect">
	<select name="{@name}" size="{@size}" multiple="">
		<xsl:apply-templates/>
	</select>
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

<xsl:template match="exception">
	<html>
		<head><title>PhotoServlet Error!</title></head>
		<body bgcolor="#fFfFfF">
			<h1>Error!</h1>
			Photoservlet has encounted an error in doing your bidding.  The
			error is as follows:

			<p/>

			<tt><xsl:value-of select="text"/></tt>

			<p/>

			Here's the stack:

			<ol>
				<xsl:for-each select="stack/stack_entry">
					<li>
						<xsl:value-of select="."/>
					</li>
				</xsl:for-each>
			</ol>
		</body>
	</html>
</xsl:template>

</xsl:stylesheet>
