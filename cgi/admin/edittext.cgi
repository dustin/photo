#!/usr/local/bin/perl
#
# Copyright (c) 1997  Dustin Sallings
#
# $Id: edittext.cgi,v 1.2 1997/11/03 09:31:49 dustin Exp $

use Postgres;
use CGI;

$q=CGI->new;
for($q->param)
{
    $in{$_}=$q->param($_);
}

$dbh=db_connect('photo');

print <<EOF;
Content-type: text/html

<html><head><title>Updating info for OID $in{'oid'}</title></head>
<body bgcolor="#fFfFfF">

EOF

$info=$in{'info'};
$info=~s/\'/\\'/g;

$keywords=$in{'keywords'};
$keywords=~s/\'/\\\'/g;

$taken=$in{'taken'};
$taken=~s/\'/\\\'/g;

$query ="update album set cat=$in{cat}, keywords='$keywords',\n";
$query.="    descr='$info', taken='$taken'\n";
$query.="    where oid=$in{'oid'};";

if( $dbh->execute($query) )
{
    print "Update was successful, info now reads as ";
    print "follows:<p>\n$in{'info'}";
}
else
{
    print "Query failed, the info was not updated.<br>\n";
    print "$Postgres::error<br>\n$query\n";
}
