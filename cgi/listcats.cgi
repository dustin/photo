#!/usr/local/bin/perl
# Copyright (c) 1997  Dustin Sallings
#
# $Id: listcats.cgi,v 1.5 1998/04/24 17:46:56 dustin Exp $

use DCache;

require 'photolib.pl';

sub getlist
{
    my($ret, $query, $ok, @ok, $s, @r);

    $ret="";

    $query="select cat from wwwacl where username='$ENV{REMOTE_USER}'";

    $s=doQuery($query);

    while(($ok)=@{$s->fetch})
    {
        $ok[$ok]=1;
    }

    $query="select * from cat order by name";

    $s=doQuery($query);

    while(@r=@{$s->fetch})
    {
        $ret.="    <option value=\"$r[0]\">$r[1]\n" if($ok[$r[0]]);
    }

    return($ret);
}

my($c, $key, $data);

$c=DCache->new;

$key="$ENV{SCRIPT_NAME}.$ENV{REMOTE_USER}";

if( ! ($c->checkcache($key, 900))) {
    $data=&getlist;
    $c->cache($key, "Content-type: text/html", $data);
}

print $c->getcache($key);
