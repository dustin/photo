#!/usr/local/bin/perl
#
# Copyright (c) 1997  Dustin Sallings
#
# $Id: listrecent.cgi,v 1.3 1998/04/24 07:41:08 dustin Exp $

use Postgres;

require 'photolib.pl';

sub displayrow
{
    my($aid, $cat, $oid, $bcat, $keywords, $taken, $ts, $image)=@_;

    print <<EOF;
    <tr>
	<td><img src="$Itop/tn/$image"></tr>
	<td><a href="/cgi-bin/dustin/photo/admin/deleteimg.cgi?$oid">
	     $oid</a></td>
	<td>$cat</td>
	<td><a href="/cgi-bin/dustin/photo/admin/display.cgi?$oid">
	     $keywords</a></td>
	<td>$taken</td>
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

$query ="select a.id,a.name,b.oid,b.cat,b.keywords,b.taken,b.ts,b.fn\n";
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
    <th>Image</th><th>OID</th><th>Category</th><th>Keywords</th><th>Taken</th>
    <th>Timestamp</th>
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
