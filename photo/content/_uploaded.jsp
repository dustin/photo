<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<h1>Upload Successful</h1>

<%
	Integer idInteger=(Integer)request.getAttribute("net.spy.photo.UploadID");
	String idString=idInteger.toString();
	int id=idInteger.intValue();
%>

Your image has been uploaded.  Its ID is <%= idString %> and you can
see it <photo:imgLink id="<%= idString %>">here</photo:imgLink>.

