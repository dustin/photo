#!/usr/local/bin/perl
# Copyright (c) 1997  Dustin Sallings
#
# $Id: listcats.cgi,v 1.6 1998/04/25 03:14:46 dustin Exp $

use DCache;
use Photo;

sub getlist
{
    my($ret, $query, $ok, $s, $r, $p);

    $p=Photo->new;

    $ret="";

    $query="select cat from wwwacl where username='$ENV{REMOTE_USER}'";

    $s=$p->doQuery($query);

    while($ok=$s->fetch) { $ok[$ok->[0]]=1 }

    $query="select * from cat order by name";

    $s=$p->doQuery($query);

    while($r=$s->fetch)
    {
        $ret.="    <option value=\"$r->[0]\">$r->[1]\n" if($ok[$r->[0]]);
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
