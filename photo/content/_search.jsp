<%@ page import="net.spy.photo.Category" %>
<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>

<template:insert template='/templates/section_header.jsp'>
	<template:put name='title' content='Simple Search' direct='true'/>
</template:insert>

<form method="POST" action="search.do">
	<input type="hidden" name="func" value="search">
	<input type="hidden" name="maxret" value="6">
	<input type="hidden" name="fieldjoin" value="and">
	<input type="hidden" name="keyjoin" value="and">
	<input type="hidden" name="order" value="a.ts">
	<input type="hidden" name="sdirection" value="desc">
	Find all images whose
	<select name="field">
		<option value="keywords">Keywords</option>
		<option value="descr">Info (slow)</option>
	</select>
	contains
	<input name="what"/><br/>
	<input type="submit" value="Find"/>
</form>

</p>

<p>

<template:insert template='/templates/section_header.jsp'>
	<template:put name='title' content='Find image by ID' direct='true'/>
</template:insert>

<form method="GET" action="search.do">
	<input type="hidden" name="func" value="display"/>
	ID:  <input name="id" size="6">
	<input type="submit" value="Lookup">
</form>

</p>

<p>

<template:insert template='/templates/section_header.jsp'>
	<template:put name='title' content='Advanced Search' direct='true'/>
</template:insert>

Only fields that are filled out will be used.  Default search will return
all images.

<form method="POST" action="search.do">

	<input type="hidden" name="func" value="search"/>

	<p>
		Category:<br/>
		<select name="cat" size="5" multiple="">
			<photo:getCatList showViewable="true">
				<logic:iterate id="i" name="catList">
					<% Category cat=(Category)i; %>
					<option value="<%= cat.getId() %>">
						<%= cat.getName() %></option>
				</logic:iterate>
			</photo:getCatList>
		</select>
	</p>
	<p>
		<select name="fieldjoin">
			<option value="and">and</option>
			<option value="or">or</option>
		</select>
		<select name="field">
			<option value="keywords">Keyword</option>
			<option value="descr">Info</option>
		</select>

		contains

		<select name="keyjoin">
			<option value="or">one of</option>
			<option value="and">all of</option>
		</select>

		<input name="what"/><br/>

		<table>
			<tr>
				<td>
					<select name="tstartjoin">
						<option value="and">and</option>
						<option value="or">or</option>
					</select>
					was taken since (date)
					<input name="tstart"/>
				</td>
				
				<td>
					<select name="tendjoin">
						<option value="and">and</option>
						<option value="or">or</option>
					</select>
					was taken before (date)
					<input name="tend"/>
				</td>
			</tr>

			<tr>
				<td>
					<select name="startjoin">
						<option value="and">and</option>
						<option value="or">or</option>
					</select>
					was added since (date)
					<input name="start"/>
				</td>

				<td>
					<select name="endjoin">
						<option value="and">and</option>
						<option value="or">or</option>
					</select>
					was added before (date)
					<input name="end"/>
				</td>
			</tr>
		</table>

		Sort by when the picture was
		<select name="order">
			<option value="a.taken">taken</option>
			<option value="a.ts">added</option>
		</select>
		and show
		<select name="sdirection">
			<option value="">oldest</option>
			<option value="desc">newest</option>
			<option value="hack">hack direction</option>
		</select>
		images first.

		<br/>

		Show
		<select name="maxret">
			<option value="6">6</option>
			<option value="10">10</option>
		</select>
		images per page.

		<br/>

		<input type="submit" value="Find"/>
		<input type="reset" value="Clear Form"/>
	</p>

</form>

</p>
