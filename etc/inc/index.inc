<html><head><title>Photo Album</title>
%STYLESHEET%
</head>
<body bgcolor="fFfFfF">

<h1>Dustin's Photo Album</h1>

<ul>
    <li><a href="%SELF_URI%?func=addform">Add an image</a></li>
    <li><a href="%SELF_URI%?func=findform">Find an image</a></li>
    <li><a href="%SELF_URI%?func=catview">
	Category view</a></li>
	<li><a href="%SELF_URI%?func=styleform">Set viewing style</a></li>

</ul>

<h1>Canned Searches</h1>

<ul>
  %SAVED%
</ul>

<form method="POST" action="%SELF_URI%">
<input type="hidden" name="func" value="search">
<input type="hidden" name="maxret" value="5">
<input type="hidden" name="fieldjoin" value="and">
<input type="hidden" name="keyjoin" value="and">
<input type="hidden" name="order" value="a.ts">
<input type="hidden" name="sdirection" value="desc">
<input type="hidden" name="field" value="keywords">
Quick Search:  <input name="what">
<input type="submit" value="Find">
</form>

<h1>Stats</h1>

There are currently about %TOTAL_IMAGES% images in the database, and there
have been at least %TOTAL_IMAGES_SHOWN% images served.

<h1>Credits:</h1>
All pages herein were created using vi.  For more information on the vi
web page publishing system, type <i>man vi</i> at your prompt.

<!--
    Logged in as:  %REMOTE_USER%
-->

</body>
</html>
