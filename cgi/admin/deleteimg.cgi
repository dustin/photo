#!/usr/local/bin/perl
#
# Copyright (c) 1997  Dustin Sallings
#
# $Id: deleteimg.cgi,v 1.1 1997/11/02 12:12:47 dustin Exp $

use Postgres;

$dbh=db_connect('photo');
$Itop="/~dustin/photo/album";
$Iroot="/usr/people/dustin/public_html/photo/album";

$query ="select a.id,a.name,b.keywords,b.descr,b.fn,b.cat,b.oid\n";
$query.="    from cat a, album b\n";
$query.="    where a.id=b.cat and b.oid=$ARGV[0];";

if( !($s=$dbh->execute($query)) )
{
    print "Content-type: text/html\n\n";
    print "ERROR!!!  $Postgres::error<br>\n$query<br>\n";
    exit(0);
}

($aid, $cat, $keywords, $descr, $image)=$s->fetchrow();

print <<EOF;
Content-type: text/html

<html><head><title>$killing $ARGV[0]</title></head>
<body bgcolor="#fFfFfF">

<h2>$ARGV[0]</h2>

Keywords:<br>
$keywords
<p>

Info:<br>
$descr

<hr>
EOF

$query="delete from album where oid=$ARGV[0];";
if($dbh->execute($query))
{
    unlink("$Iroot/$image");
    print "Entry has been deleted from the database, and $Iroot/$image ";
    print "has been deleted\n";
}
else
{
    print "There was an error, nothing was deleted:<br>\n";
    print "$Postgres::error<br>\n$query\n";
}

print "</body></html>";
