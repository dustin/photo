#!/usr/local/bin/perl
# Copyright (c) 1997  Dustin Sallings
#
# $Id: addimg.cgi,v 1.4 1997/11/04 08:01:42 dustin Exp $

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

$taken=$q->param('taken');
$taken=~s/\'/\\\'/g;

$cat=$q->param('category');

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

$ldir="/usr/people/dustin/public_html/photo/album";

$f=$q->param('picture');
open(OUT, ">$ldir/$fn");
while(<$f>)
{
    print OUT;
}
close(OUT);
@stat=stat("$ldir/$fn");

if($stat[7]==0)
{
    print "Sorry, but the image didn't make it.\n";
    unlink("$ldir/$fn");
    exit(0);
}

system("/usr/local/bin/convert -size 100x100 $ldir/$fn $ldir/tn/$fn");

$query ="insert into album (fn, keywords, descr, cat, size, taken)\n";
$query.="    values('$fn',\n\t'$keywords',\n\t'$info',\n\t$cat,\n";
$query.="\t$stat[7],\n\t'$taken');";

if(!($dbh->execute($query)))
{
    print "Database Error:  $Postgres::error\n<!--\n$query\n-->\n";
    unlink("$ldir/$fn");
    exit(0);
}

print "<pre>\n$query\n</pre>\n";

print $q->end_html;
