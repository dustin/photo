#!/usr/local/bin/perl
# Copyright (c) 1997  Dustin Sallings
#
# $Id: addimg.cgi,v 1.1 1997/11/02 10:50:45 dustin Exp $

use CGI;
use Postgres;

umask 22;

$q=CGI->new;

print $q->header;

if(!($dbh=db_connect('photo')))
{
    print "Database error\n";
}

print $q->start_html(
    -title=>'Adding image',
    -bgcolor=>'#fFfFfF');

@elements=qw(category keywords picture info);

$img=$q->param('picture');

$keywords=lc($q->param('keywords'));
$keywords=~s/\'/\\\'/g;

$info=$q->param('info');
$info=~s/\'/\\\'/g;

if($img=~/jpg$/i)
{
    $ext="jpg";
}
elsif($img=~/gif$/i)
{
    $ext="gif";
}
else
{
    print "Invalid file, must be .gif or .jpg\n";
    exit(0);
}

$fn=time()."$$.$ext";

$query ="insert into album values('$fn',\n\t'$keywords',\n\t'$info');";

if(!($dbh->execute($query)))
{
    print "Database Error:  $Postgres::error\n<!--\n$query\n-->\n";
    exit(0);
}


$f=$q->param('picture');
open(OUT, ">/usr/people/dustin/public_html/photo/album/$fn");
while(<$f>)
{
    print OUT;
}
close(OUT);

print "<pre>\n$query\n</pre>\n";

print $q->end_html;
