<html><head><title>Photo Admin - Editing %USER%</title>
%STYLESHEET%
</head>

<body bgcolor="#fFfFfF">

<h2>Editing user %USER%</h2>
<form method="POST" action="%SELF_URI%">

<input type="hidden" name="func" value="admsaveuser">
<input type="hidden" name="userid" value="%USERID%">

<table border="3">

<tr>
    <td>Username:</td>
    <td><input name="username" value="%USER%"></td>
</tr>

<tr>
    <td>Real Name:</td>
    <td><input name="realname" value="%REALNAME%"></td>
</tr>

<tr>
    <td>E-mail address:</td>
    <td><input name="email" value="%EMAIL%"></td>
</tr>

<tr>
    <td>Password:</td>
    <td><input name="password" type="password" value="%PASS%"></td>
</tr>

<tr>
    <td>Can Add:</td>
    <td>
	Yes <input type="radio" name="canadd" %CANADD% value="t">
	No  <input type="radio" name="canadd" %CANNOTADD% value="f">
    </td>
</tr>

</table>

<br>

<table border="1">
<tr>
    <th>Category</th>
    <th>Visible</th>
</tr>
%ACLTABLE%
</table>

<p>

<input type=submit value="Save User">

</form>

</body>
</html>
