#!/usr/local/bin/perl
#
# Copyright (c) 1997  Dustin Sallings
#
# $Id: savecat.cgi,v 1.1 1997/11/02 12:12:54 dustin Exp $

use Postgres;
use CGI;

$q=CGI->new;
for($q->param)
{
    $in{$_}=$q->param($_);
}

print <<EOF;
Content-type: text/html

<html><head><title>Saving $in{'name'}</title></head>
<body bgcolor="fFfFfF">

<h2>Saving $in{'name'}</h2>

EOF

$in{'name'}=~s/\'/\\\'/g;

if($in{'id'}>0)
{
    $query ="update cat set name='$in{'name'}'\n";
    $query.="    where id=$in{'id'};";
}
else
{
    $query ="insert into cat (name) values('$in{'name'}')";
}

$dbh=db_connect('photo');

if($dbh->execute($query))
{
    print "Successfully stored.";
}
else
{
    print "ERROR:  $Postgres::error<br>\n$query\n";
}
