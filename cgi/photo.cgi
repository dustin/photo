#!/usr/local/bin/perl -w
# Copyright (c) 1997  Dustin Sallings
#
# $Id: photo.cgi,v 1.10 1998/04/30 05:57:12 dustin Exp $

use CGI;
use Photo;
use strict;

sub doFind
{
    my($q, $p)=@_;
    $p->doFind($q);
}

sub doDisplay
{
    my($q, $p)=@_;
    $p->doDisplay($q);
}

sub doCatView
{
    my($q, $p)=@_;
    $p->doCatView($q);
}

sub badFunc
{
    my($cgi, $funcname)=@_;

    print $cgi->start_html(
        -title=>'Bad Function Name',
        -bgcolor=>'#fFfFfF') ."\n";

    print "<h2>ERROR</h2>\nThe function you requested, <i>$funcname</i>\n";
    print "is not valid.\n";
    return;
}

sub doAddImage
{
    my($q, $p)=@_;

    $p->addImage($q);
}

my %funcs=(
    'search' => \&doFind,
    'display' => \&doDisplay,
    'catview' => \&doCatView,
    'addimage' => \&doAddImage,
);

my($func, $q, $p);
$q=CGI->new;
$p=Photo->new;

print $q->header;

$func=$q->param('func');

if(defined($funcs{$func})) {
    &{ $funcs{$func} }($q, $p);
} else {
    badFunc($q, $q->param('func'));
}
$p->addTail();
print $q->end_html;
