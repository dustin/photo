#!/usr/local/bin/perl
#
# Copyright (c) 1997  Dustin Sallings
#
# $Id: listcats.cgi,v 1.1 1997/11/02 12:12:51 dustin Exp $

use CGI;
use Postgres;

$q=CGI->new;
print $q->header, $q->start_html(-title=>'Category List', -bgcolor=>'#fFfFfF');

print "<h2>Category List</h2>\nClick on the id # to edit the entry.\n";

$dbh=db_connect('photo');

$query="select * from cat order by name;";

if(!($s=$dbh->execute($query)))
{
    print "Error:  $Postgres::error<br>\n$query\n";
    exit(0);
}

print <<EOF;
<table border="3">
<tr>
    <th>ID</th>
    <th>OEM</th>
</tr>
EOF

while(@r=$s->fetchrow())
{
     print "<tr>";
     print "<td><a href=\"/cgi-bin/dustin/photo/admin/editcat.cgi?$r[0]\">";
     print "$r[0]</a></td>\n";
     print "<td>$r[1]</td>\n";
     print "<tr>";
}

print <<EOF;
</table>

<br>
<a href="/cgi-bin/dustin/photo/admin/editcat.cgi?0">Add a new category</a>

</body>
</html>
EOF
