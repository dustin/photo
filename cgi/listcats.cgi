#!/usr/local/bin/perl
# Copyright (c) 1997  Dustin Sallings
#
# $Id: listcats.cgi,v 1.2 1997/12/06 09:42:58 dustin Exp $

use Postgres;

print "Content-type: text/html\n\n";

$dbh=db_connect('photo');

$query="select cat from wwwacl where username='$ENV{REMOTE_USER}'";

$s=$dbh->execute($query);

while(($ok)=$s->fetchrow())
{
    $ok[$ok]=1;
}

$query="select * from cat order by name";

$s=$dbh->execute($query);

while(@r=$s->fetchrow())
{
    print "    <option value=\"$r[0]\">$r[1]\n" if($ok[$r[0]]);
}
