#!/usr/local/bin/perl
# Copyright (c) 1997  Dustin Sallings
#
# $Id: listcats.cgi,v 1.12 1998/11/13 07:32:13 dustin Exp $

use DCache;
use Photo;

sub getlist
{
    my($ret, $query, $s, $r, $p, $id);

    $p=Photo->new;
	$id=$p->getuid();

    $ret="";

    $query="select * from cat where id in\n" .
	   "(select cat from wwwacl where\n" .
	   "    userid=$id)\n".
	   "order by name;\n";

    $s=$p->doQuery($query);

    while($r=$s->fetch) {
        $ret.="    <option value=\"$r->[0]\">$r->[1]\n";
    }
    $s->finish;

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

undef($c);
undef($key);
undef($data);
undef($p);
