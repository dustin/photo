<%@ page import="net.spy.photo.Category" %>
<%@ page import="net.spy.photo.PhotoUtil" %>
<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>

<template:insert template='/templates/section_header.jsp'>
	<template:put name='title' content='Add a Photo' direct='true'/>
</template:insert>

<form method="POST" enctype="multipart/form-data" action="PhotoServlet">

	<input type=hidden name="func" value="addimage"/>

	<table border="0" width="100%">

	<tr>
		<td align="left" width="50%">
			<table border="0">
			<tr>
				<td>Category:</td>
				<td>
					<select name="category" size="5">
					<photo:getCatList showAddable="true">
						<logic:iterate id="i" name="catList">
							<% Category category=(Category)i; %>
							<option value="<%= "" + category.getId() %>">
								<%= category.getName() %></option>
						</logic:iterate>
					</photo:getCatList>
					</select>
				</td>
			</tr>
			</table>
		</td>

		<td align="right" width="50%">
			<table border="0">
				<tr>
					<td>Date Taken:</td>
					<td>
						<input name="taken"
							value="<%= PhotoUtil.getToday() %>"/>
					</td>
				</tr>
				<tr>
					<td>Keywords:</td>
					<td><input name="keywords"/></td>
				</tr>
				<tr>
					<td>Picture:</td>
					<td><input type="file" name="picture"/></td>
				</tr>
			</table>
		</td>
	</tr>

	</table>

	<center>
		<table>
			<tr>
			<td align="left">
			Short Description:<br/>
			<textarea name="info" cols="60" rows="5" wrap="hard"></textarea>
			</tr>
			</td>
		</table>
		<input type="submit" value="Add Image"/>
		<input type="reset" value="Clear"/>
	</center>

</form>

</p>
