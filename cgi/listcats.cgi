#!/usr/local/bin/perl
# Copyright (c) 1997  Dustin Sallings
#
# $Id: listcats.cgi,v 1.7 1998/05/01 06:58:34 dustin Exp $

use DCache;
use Photo;

sub getlist
{
    my($ret, $query, $s, $r, $p);

    $p=Photo->new;

    $ret="";

    $query="select * from cat where id in\n" .
	   "(select cat from wwwacl where username='$ENV{REMOTE_USER}')\n".
	   "order by name;\n";

    $s=$p->doQuery($query);

    while($r=$s->fetch)
    {
        $ret.="    <option value=\"$r->[0]\">$r->[1]\n";
    }

    return($ret);
}

my($c, $key, $data, $p);

$c=DCache->new;

$key="$ENV{SCRIPT_NAME}.$ENV{REMOTE_USER}";

if( ! ($c->checkcache($key, 900))) {
    $data=&getlist;
    $c->cache($key, "Content-type: text/html", $data);
}

print $c->getcache($key);
