<%@ page import="java.util.Iterator" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="net.spy.photo.Comment" %>
<%@ page import="net.spy.photo.PhotoImageData" %>
<%@ page import="net.spy.photo.Keyword" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%
	PhotoImageData image=(PhotoImageData)request.getAttribute("image");
	Integer searchId=(Integer)request.getAttribute("search_id");
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

	String keywordString="";
	for(Iterator i=image.getKeywords().iterator(); i.hasNext(); ) {
		Keyword kw=(Keyword)i.next();
		keywordString += kw.getKeyword() + " ";
	}
	keywordString=keywordString.trim();
%>

	<table width="100%">
		<tr valign="top">
			<c:if test="{not empty searchId}">
				<td align="left" width="10%">
					<photo:imgLink id="<%= 0 %>" relative="prev"
							searchId="<%= String.valueOf(searchId) %>">
						<photo:imgsrc alt="<<<" url="/images/l_arrow.gif"/>
					</photo:imgLink>
				</td>
			</c:if>

			<td align="center">
				<div class="displayBrief"><%= image.getDescr() %></div>
			</td>

			<c:if test="{not empty searchId}">
				<td align="right" width="10%">
					<photo:imgLink id="<%= 0 %>" relative="next"
							searchId="<%= String.valueOf(searchId) %>">
						<photo:imgsrc alt=">>>" url="/images/r_arrow.gif"/>
					</photo:imgLink>
				</td>
			</c:if>
		</tr>
	</table>

	<div align="center">
		<photo:imgSrc id="<%= "" + image.getId() %>" showOptimal="true" />
	</div>

	<html:form action="/admeditimage">
		<html:hidden property="id" value="<%= "" + image.getId() %>"/>

	<p>
		<b>Category</b>:
		<html:select property="category"
			value="<%= "" + image.getCatId() %>" size="5">

			<photo:getCatList showAddable="true">
				<logic:iterate id="cat" type="net.spy.photo.Category" name="catList"> 
					<html:option value="<%= "" + cat.getId() %>">
						<%= cat.getName() %></html:option>
				</logic:iterate>
			</photo:getCatList>
		</html:select>

		<br/>
		<b>Keywords</b>:
			<html:text property="keywords"
				value="<%= keywordString %>"/><br/>
		<b>Size</b>:  <%= image.getDimensions() %>
			(<%= image.getSize() %> bytes)<br/>
		<b>Taken</b>:  <html:text property="taken"
				value="<%= sdf.format(image.getTaken()) %>"/><br/>
			<b>Added</b>:
			<%= image.getTimestamp() %>
		by <%= image.getAddedBy().getRealname() %>
			(<%= image.getAddedBy().getUsername() %>)<br/>
		<b>Info</b>:
		<html:textarea cols="60" rows="5" property="info"
			value="<%= image.getDescr() %>"/>

	</p>

	<html:submit>Save Info</html:submit>
	<html:reset>Restore to Original</html:reset>

	</html:form>

[<a href="logView.do?id=<%= "" + image.getId() %>"> Who's seen this?</a>] |
[<photo:imgLink id='<%= "" + image.getId() %>'>Linkable image</photo:imgLink>] |
[<a href="PhotoServlet?photo_id=<%= "" + image.getId() %>">Full
Size Image</a>]

<p class="comments">

	<h1>Comments</h1>

	<logic:iterate id="comment"
		type="net.spy.photo.Comment"
		collection="<%= Comment.getCommentsForPhoto(image.getId()) %>">

		<div class="comments">
			<div class="commentheader">
				At <%= comment.getTimestamp() %>,
					<%= comment.getUser().getRealname() %> said the following:
			</div>
			<div class="commentbody">
				<%= comment.getNote() %>
			</div>
		</div>
	</logic:iterate>

</p>

<p>

	Submit a comment:<br/>

	<logic:notPresent role="guest">
		<html:form action="/addcomment">
			<html:errors/>
			<html:hidden property="imageId" value="<%= "" + image.getId() %>"/>
			<html:textarea property="comment" cols="50" rows="2"/>
			<br/>
			<html:submit>Add Comment</html:submit>
		</html:form>
	</logic:notPresent>
