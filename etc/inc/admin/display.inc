<html><head><title>Editing %IMAGE%</title>
%STYLESHEET%
</head>

<body bgcolor="#fFfFfF">

<table width="100%"> <!-- % -->
	<tr>
		<td align="left">
			<a href="%SELF_URI%?func=display&search_id=%PREV%">&lt;&lt;&lt;</a>
		</td>
		<td align="right">
			<a href="%SELF_URI%?func=display&search_id=%NEXT%">&gt;&gt;&gt;</a>
		</td>
	</tr>
</table>

<center>
<img width="%SCALED_WIDTH%" height="%SCALED_HEIGHT%"
	src="%SELF_URI%?func=getimage&photo_id=%IMAGE%"><br>
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
<textarea cols="60" rows="5" name="info" wrap="hard">%DESCR%</textarea><br>

<input type="submit" value="Save Info">
<input type="reset" value="Restore to Original">

</form>
</body></html>
