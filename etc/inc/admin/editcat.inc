<html><head><title>Photo Admin - Edit Category</title></head>

<body bgcolor="#fFfFfF">

<form method="POST" action="%SELF_URI%">

<input type="hidden" name="func" value="admsavecat">

<table border="3">

<input type="hidden" name="id" value="%CATID%">

<tr>
    <td>ID:</td>
    <td>%CATID%</td>
</tr>

<tr>
    <td>Name:</td>
    <td><input name="name" value="%CATNAME%"></td>
</tr>

</table>

<br>

<input type="submit" value="Save"> <input type="reset" value="Reset">

</form>

</body>
</html>
