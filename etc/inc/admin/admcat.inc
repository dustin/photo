<html><head><title>Photo Admin - Category Administration</title></head>

<body bgcolor="#fFfFfF">

<form method="POST" action="%SELF_URI%">
	<input type=hidden name=func value=admcatedit>
	<select name=cat>
	<option value="-1">New Category
	%CATS%
	</select>
	<input type=submit value=Edit>
</form>

</body>
</html>
