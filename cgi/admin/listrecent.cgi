#!/usr/local/bin/perl
#
# Copyright (c) 1997  Dustin Sallings
#
# $Id: listrecent.cgi,v 1.1 1997/11/02 12:12:52 dustin Exp $

use Postgres;

$Itop="/~dustin/photo/album";

sub displayrow
{
    my($aid, $cat, $oid, $bcat, $keywords, $ts)=@_;

    print <<EOF;
    <tr>
	<td><a href="/cgi-bin/dustin/photo/admin/deleteimg.cgi?$oid">
	     $oid</a></td>
	<td>$cat</td>
	<td><a href="/cgi-bin/dustin/photo/admin/display.cgi?$oid">
	     $keywords</a></td>
	<td>$ts</td>
    </tr>
EOF
}

print <<EOF;
Content-type: text/html

<html><head><title>Image Administration</title></head>
<body bgcolor="#fFfFfF">

<h2>Image Administration</h2>

To delete an entry, click on the OID.  <i>Please</i> be careful with
this.  :)
<p>

EOF

$dbh=db_connect('photo');

$query ="select a.id,a.name,b.oid,b.cat,b.keywords,b.ts\n";
$query.="    from cat a, album b where a.id=b.cat ";
$query.="    order by a.name, b.ts;";

# Let's see if we can successfully do the query first.

if( !($s=$dbh->execute($query)) )
{
    print "Error!!!  $Postgres::error<br>\n$query<br>\n";
    exit(0);
}

# Now start building the table.

print <<EOF;
<table border="1">
<tr>
    <th>OID</th><th>Category</th><th>Keywords</th><th>Timestamp</th>
</tr>
EOF

while(@r=$s->fetchrow())
{
    &displayrow(@r);
}

print <<EOF;
</table>
</body></html>
EOF
