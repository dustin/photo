#!/usr/local/bin/perl
#
# Copyright (c) 1997  Dustin Sallings
#
# $Id: listusers.cgi,v 1.1 1997/12/06 09:42:29 dustin Exp $

use CGI;
use Postgres;

$q=CGI->new;
print $q->header, $q->start_html(-title=>'User List', -bgcolor=>'#fFfFfF');

print "<h2>User List</h2>\nClick on the id # to edit the entry.\n";
print "<ul>\n";

$dbh=db_connect('photo');

$query="select username from wwwusers order by username;";

if(!($s=$dbh->execute($query)))
{
    print "Error:  $Postgres::error<br>\n$query\n";
    exit(0);
}

print <<EOF;
EOF

while(@r=$s->fetchrow())
{
     print "    <li>\n\t";
     print "<a href=\"/cgi-bin/dustin/photo/admin/edituser.cgi?$r[0]\">";
     print "$r[0]</a></li>\n";
}

print <<EOF;
</ul>

<br>
<a href="/cgi-bin/dustin/photo/admin/edituser.cgi">Add a new user</a>

</body>
</html>
EOF
