#!/usr/local/bin/perl
#
# Copyright (c) 1997  Dustin Sallings
#
# $Id: editcat.cgi,v 1.1 1997/11/02 12:12:49 dustin Exp $

use Postgres;
use CGI;

$q=CGI->new;

print $q->header;

$dbh=db_connect('photo');

if($ARGV[0]>0)
{
    $query="select * from cat where id=$ARGV[0];";
    if(!($s=$dbh->execute($query)))
    {
        print "ERROR:  $Postgres::error<br>\n$query\n";
        exit(0);
    }

    if(!(@r=$s->fetchrow()))
    {
        print "No such id $ARGV[0]\n";
        exit(0);
    }
}
else
{
    @r=(0, '');
}

print $q->start_html(-title=>"Editing $r[1]", -bgcolor=>'#fFfFfF');

print <<EOF;
<form method="POST" action="/cgi-bin/dustin/photo/admin/savecat.cgi">

<table border="3">

<input type="hidden" name="id" value="$r[0]">

<tr>
    <td>ID:</td>
    <td>$r[0]</td>
</tr>

<tr>
    <td>Name:</td>
    <td><input name="name" value="$r[1]"></td>
</tr>

</table>

<br>

<input type="submit" value="Save"> <input type="reset" value="Reset">

</form>
EOF
