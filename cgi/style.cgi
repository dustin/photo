#!/usr/local/bin/perl -w
# Copyright (c) 1997  Dustin Sallings
#
# $Id: style.cgi,v 1.2 1999/01/30 22:20:28 dustin Exp $

use Photo;
use CGI;
use strict;

my($q, $style, $p);
$q=CGI->new;
$p=Photo->new;

$style=$q->cookie(-name=>'photo_style');

print $q->header('text/css');

if(defined($style)) {
	print $style;
} else {
	open(IN, $p->{'config'}{'includes'}."/style.css");
	print <IN>;
	close(IN);
}

undef($q);
undef($style);
