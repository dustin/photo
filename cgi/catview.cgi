#!/usr/local/bin/perl
# Copyright (c) 1997  Dustin Sallings
#
# $Id: catview.cgi,v 1.2 1997/12/06 09:42:55 dustin Exp $

use CGI;
use Postgres;

$q=CGI->new;

print $q->header;

$dbh=db_connect('photo');

print $q->start_html(
    -title=>'View Images by Category',
    -bgcolor=>'#fFfFfF') . "\n";

print "<h2>Category List</h2>\n";

$query="select cat from wwwacl where username='$ENV{REMOTE_USER}'";

$s=$dbh->execute($query);

while(($ok)=$s->fetchrow())
{
    $ok[$ok]=1;
}


$query="select name,id,catsum(id) as cs from cat order by cs desc";

print "<!--\n$query\n-->\n";

if(!($s=$dbh->execute($query)))
{
    print "Database Error:  $Postgres::error\n";
    exit(0);
}

print "<ul>\n";

while(@r=$s->fetchrow())
{
    next if($ok[$r[1]]!=1);
    next if($r[2]==0);

    if($r[2]==1)
    {
	$t="image";
    }
    else
    {
	$t="images";
    }

    print "<li>$r[0]:  <a href=\"/cgi-bin/dustin/photo/find.cgi?";
    print "searchtype=advanced&cat=$r[1]\">$r[2] $t</a></li>\n";
}

print "</ul>\n" . $q->end_html . "\n";
