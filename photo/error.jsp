<%@ page isErrorPage="true"%>
<html>
<head><title>PhotoServlet Error!</title></head>

<body bgcolor="#fFfFfF">


	<center>
	Problem doing your bidding:
	<p>
	<font color="red">
	<%= exception.getMessage() %>
	</font><br/>
	(<font size="-2"><a href="#geek">details below</a></font>)
	</p>
	</center>

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
	<h1>Details</h1>
	<a name="geek">
		<pre>
<% exception.printStackTrace(response.getWriter()); %>
		</pre>
	</a>

</body>
</html>
