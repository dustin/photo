#!/usr/local/bin/perl
# Copyright (c) 1997  Dustin Sallings
#
# $Id: listcats.cgi,v 1.1 1997/11/02 10:50:45 dustin Exp $

use Postgres;

print "Content-type: text/html\n\n";

$dbh=db_connect('photo');

$query="select * from cat order by name";

$s=$dbh->execute($query);

while(@r=$s->fetchrow())
{
    print "    <option value=\"$r[0]\">$r[1]\n";
}
