<%@ page import="net.spy.photo.Category" %>
<%@ page import="net.spy.photo.PhotoUtil" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<%
	// Check to see if there was already an add here.
	Integer idInteger=(Integer)request.getAttribute("net.spy.photo.UploadID");
	String idString=null;
	if(idInteger!=null) {
		idString=idInteger.toString();
	}
%>

<% if(idInteger!=null) { %>
<p>
	Your image has been uploaded.  Its ID is <%= idString %> and you can
	see it <photo:imgLink id="<%= idString %>">here</photo:imgLink> before
	too long.
</p>
<% } %>

<h1>Add a Photo</h1>

<html:form action="upload.do" enctype="multipart/form-data">

	<html:errors/>

	<table border="0" width="100%">

	<tr>
		<td align="left">
			<table border="0">
			<tr>
				<td>Category:</td>
				<td>
					<html:select property="category" size="5">
					<photo:getCatList showAddable="true">
						<logic:iterate type="net.spy.photo.Category" id="i" name="catList">
							<html:option value="<%= "" + i.getId() %>">
								<%= i.getName() %></html:option>
						</logic:iterate>
					</photo:getCatList>
					</html:select>
				</td>
			</tr>
			</table>
		</td>

		<td align="right">
			<table border="0">
				<tr>
					<td>Date Taken:</td>
					<td>
						<html:text property="taken"/>
					</td>
				</tr>
				<tr>
					<td>Keywords:</td>
					<td><html:text property="keywords"/></td>
				</tr>
				<tr>
					<td>Picture:</td>
					<td><html:file property="picture"/></td>
				</tr>
			</table>
		</td>
	</tr>

	</table>

	<div class="centered">
		<table>
			<tr>
			<td align="left">
				Short Description:<br/>
				<html:textarea property="info" cols="60" rows="5"/>
			</td>
			</tr>
		</table>
		<html:submit>Add Image</html:submit>
		<html:reset>Clear</html:reset>
	</div>

</html:form>

<%-- arch-tag: A2410B18-5D6F-11D9-AF66-000A957659CC --%>
