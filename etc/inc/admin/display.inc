<html><head><title>Editing %IMAGE%</title>
%STYLESHEET%
</head>

<body bgcolor="#fFfFfF">

<center>
<img src="%SELF_URI%?func=getimage&photo_id=%IMAGE%"><br>
</center>
<form method="POST" action="%SELF_URI%">
<input type="hidden" name="func" value="admedittext">
<input type="hidden" name="id" value="%IMAGE%">

Size:  %SIZE%<br>

Category:
<select name="cat">
%CATS%
</select><br>

Taken:
<input name="taken" value="%TAKEN%"><br>

Keywords:
<input name="keywords" value="%KEYWORDS%"><br>

Info:
<textarea cols="60" rows="5" name="info" wrap="hard">%INFO%</textarea><br>

<input type="submit" value="Save Info">
<input type="reset" value="Restore to Original">

</form>
</body></html>
