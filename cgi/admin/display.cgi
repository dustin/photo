#!/usr/local/bin/perl
#
# Copyright (c) 1997  Dustin Sallings
#
# $Id: display.cgi,v 1.3 1997/11/03 09:31:48 dustin Exp $

use Postgres;

$dbh=db_connect('photo');
$Itop="/~dustin/photo/album";

$query ="select a.id,a.name,b.oid,b.keywords,b.descr,b.fn,\n";
$query.="    b.taken,b.size,b.cat\n";
$query.="    from cat a, album b\n";
$query.="    where a.id=b.cat and b.oid=$ARGV[0];";

print "Content-type: text/html\n\n";

if( !($s=$dbh->execute($query)) )
{
    print "ERROR!!!  $Postgres::error<br>\n$query<br>\n";
    exit(0);
}

($aid, $cat, $oid, $keywords, $info, $image, $taken, $size)=$s->fetchrow();

$query ="select * from cat order by name";

if( !($s=$dbh->execute($query)) )
{
    print "ERROR!!!  $Postgres::error<br>\n$query<br>\n";
    exit(0);
}

$tmp="";
while(@r=$s->fetchrow())
{
    $tmp2="";
    if($r[0]==$aid)
    {
	$tmp2="selected";
    }
    $tmp.="    <option value=\"$r[0]\" $tmp2>$r[1]\n";
}

print <<EOF;
<html><head><title>Editing Image</title></head>
<body bgcolor="#fFfFfF">

<center>
<img src="$Itop/$image"><br>
</center>
<form method="POST" action="/cgi-bin/dustin/photo/admin/edittext.cgi">
<input type="hidden" name="oid" value="$oid">

Size:  $size<br>

Category:
<select name="cat">
$tmp
</select><br>

Taken:
<input name="taken" value="$taken"><br>

Keywords:
<input name="keywords" value="$keywords"><br>

Info:
<textarea cols="60" rows="5" name="info" wrap="hard">$info</textarea><br>

<input type="submit" value="Save Info">
<input type="reset" value="Restore to Original">

</form>

<hr>
</body></html>
EOF
