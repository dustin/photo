#!/usr/local/bin/perl -w
# Copyright (c) 1997  Dustin Sallings
#
# $Id: style.cgi,v 1.1 1998/10/17 22:01:41 dustin Exp $

use Photo;
use CGI;
use strict;

my($q, $style);
$q=CGI->new;

$style=$q->cookie(-name=>'photo_style');

print $q->header('text/css');

if(defined($style)) {
	print $style;
} else {
	open(IN, "$Photo::includes/style.css");
	print <IN>;
	close(IN);
}

undef($q);
undef($style);
