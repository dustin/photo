<%@ page isErrorPage="true"%>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>
<html>
	<head>
		<title>PhotoServlet Error!</title>
		<photo:stylesheet url="/css/layout.css"/>
		<photo:stylesheet url="/css/colors.css"/>
	</head>

<body bgcolor="#fFfFfF">


	Problem doing your bidding:
	<p>
	<div class="errors">
	<%= exception.getMessage() %>
	</div><br/>
	(<span class="errormagic"><a href="#geek">details below<span
		class="hiddenmagic"><%
		exception.printStackTrace(response.getWriter()); %></span></a></span>)
	</p>

	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<div style="text-align: left">
	<h1>Details</h1>
	<a name="geek">
		<pre>
<% exception.printStackTrace(response.getWriter()); %>
		</pre>
		<%
			Throwable strutsT=
				(Throwable)request.getAttribute("org.apache.struts.action.EXCEPTION");

			if(strutsT != null) {
				strutsT.printStackTrace();
		%>

		Struts exception:

<pre>
<% strutsT.printStackTrace(response.getWriter()); %>
</pre>

		<% } %>
	</a>
	</div>

</body>
</html>
