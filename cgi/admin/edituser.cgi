#!/usr/local/bin/perl
#
# Copyright (c) 1997  Dustin Sallings
#
# $Id: edituser.cgi,v 1.2 1997/12/07 07:03:28 dustin Exp $

use Postgres;
use CGI;

$q=CGI->new;

print $q->header;

$dbh=db_connect('photo');

if($ARGV[0] ne "")
{
    $query="select * from wwwusers where username='$ARGV[0]';";
    if(!($s=$dbh->execute($query)))
    {
        print "ERROR:  $Postgres::error<br>\n$query\n";
        exit(0);
    }

    if(!(@r=$s->fetchrow()))
    {
        print "No such user $ARGV[0]\n";
        exit(0);
    }
}
else
{
    @r=('', '');
}

print $q->start_html(-title=>"Editing $r[0]", -bgcolor=>'#fFfFfF');

print <<EOF;
<h2>Editing user $r[0]</h2>
<form method="POST" action="/cgi-bin/dustin/photo/admin/saveuser.cgi">

<table border="3">
EOF

if($r[0] eq '')
{
    print "<tr><td>Username:</td><td><input name=\"username\"></td></tr>\n";
    print "<input type=\"hidden\" name=\"newuser\" value=\"1\">\n";
}
else
{
    print "<input type=\"hidden\" name=\"username\" value=\"$r[0]\">\n";
    print "<input type=\"hidden\" name=\"newuser\" value=\"0\">\n";
}

print <<EOF;

<tr>
    <td>Password:</td>
    <td><input name="password" value="$r[1]"></td>
</tr>

</table>

<br>

<table border="1">
<tr>
    <th>Category</th>
    <th>Visible</th>
</tr>
EOF

$query="select cat from wwwacl where username='$ARGV[0]'";

if( !($s=$dbh->execute($query)) )
{
    print "Content-type: text/html\n\n";
    print "ERROR!!!  $Postgres::error<br>\n$query<br>\n";
    exit(0);
}

while(($id)=$s->fetchrow()) { $ok[$id]=1; }

$query="select * from cat order by name";

if( !($s=$dbh->execute($query)) )
{
    print "Content-type: text/html\n\n";
    print "ERROR!!!  $Postgres::error<br>\n$query<br>\n";
    exit(0);
}

while(($id, $name)=$s->fetchrow())
{
    if($ok[$id]==1)
    {
	$checkedon="checked";
	$checkedoff="";
	$color="007f00";
    }
    else
    {
	$checkedon="";
	$checkedoff="checked";
	$color="ff0000";
    }
    print "<tr><td><font color=\"#$color\">$name</font></td><td>";

    print "Yes <input type=\"radio\" ";
    print "name=\"cat$id\" $checkedon value=\"1\">\n";
    print "No <input type=\"radio\" ";
    print "name=\"cat$id\" $checkedoff value=\"0\">\n";
    print "</td></tr>\n";
}

print <<EOF;
</table>
<input type="submit" value="Save"> <input type="reset" value="Reset">

</form>
EOF
