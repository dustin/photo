#!/usr/local/bin/perl
#
# Copyright (c) 1997  Dustin Sallings
#
# $Id: display.cgi,v 1.4 1998/01/19 05:00:05 dustin Exp $

use CGI::Carp;
use Postgres;

require 'photolib.pl';

$query ="select a.oid,a.fn,a.keywords,a.descr,\n";
$query.="    a.size,a.taken,a.ts,b.name,a.cat,b.id\n";
$query.="    from album a, cat b ";
$query.="    where a.cat=b.id and a.oid=$ARGV[0];";

$s=doQuery($query);

@r=$s->fetchrow();
@mapme=qw(OID IMAGE KEYWORDS INFO SIZE TAKEN TIMESTAMP CAT CATNUM);
map { $p{$mapme[$_]}=$r[$_]} (0..$#r);

$query ="select count(*) from wwwacl where username='$ENV{REMOTE_USER}'\n";
$query.="    and cat=$p{CATNUM}\n";

$s=doQuery($query);

($n)=$s->fetchrow();

if($n==0)
{
    print "Content-type: text/html\n\n";
    print "ACL ERROR!!!  We don't want your type here.\n";
    exit(0);
}

print "Content-type: text/html\n\n";
showTemplate("$includes/display.inc", %p);
&addTail;
