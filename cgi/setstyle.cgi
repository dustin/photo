#!/usr/local/bin/perl -w
# Copyright (c) 1997  Dustin Sallings
#
# $Id: setstyle.cgi,v 1.4 1999/01/30 22:20:26 dustin Exp $

use Photo;
use CGI;
use strict;

my($q, $p, %in, $style, $cookie);

$q=CGI->new;
$p=Photo->new;

%in=map { $_, $q->param($_) } $q->param;

if(!defined($in{'font'}) || !defined($in{'h_transform'})) {
	print $q->header;
	print "Damnit, use the form.\n";
	exit;
}

$style=<<EOF;
body,td {font-family: $in{'font'}, Arial; background-color: $in{'bgcolor'};}
EOF

if($in{'d_transform'} ne "none") {
	$style.="blockquote {text-transform: $in{'d_transform'};};";
};

if($in{'h_transform'} ne "none") {
	$style.="h1,h2,h3,h4,h5 {text-transform: $in{'h_transform'};}";
};

$cookie=$q->cookie(-name=>'photo_style',
                   -path=>$p->{'config'}{'cgidir'},
				   -value=>$style,
				   -expires=>'+30d');

print $q->header(-cookie => $cookie);
$p->start_html($q, 'Set Style');

print <<EOF;

<h2>Set Style</h2>

Your new style has been set, you may continue to <a
href="/~dustin/photo/">browse the photo album</a> to
make use of it.

<pre>
$style
</pre>

</body>
</html>
EOF

undef($q);
undef($p);
undef($style);
undef(%in);
undef($cookie);
