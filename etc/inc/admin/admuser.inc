<html><head><title>Photo Admin - User Administration</title>
%STYLESHEET%
</head>

<body bgcolor="#fFfFfF">

<form method="POST" action="%SELF_URI%">
	<input type=hidden name=func value=admuseredit>
	<select name=userid>
	<option value="-1">New User
	%USERS%
	</select>
	<input type=submit value=Edit>
</form>

</body>
</html>
