#!/usr/local/bin/perl -w
# Copyright (c) 1997  Dustin Sallings
#
# $Id: setstyle.cgi,v 1.1 1998/10/17 22:55:28 dustin Exp $

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
body,td {
        font-family: $in{'font'};
        background-color: $in{'bgcolor'};
}

h1,h2,h3,h4,h5 {
        text-transform: $in{'h_transform'};
}

EOF

$cookie=$q->cookie(-name=>'photo_style',
                   -path=>$Photo::cgidir,
				   -value=>$style);

print $q->header(-cookie => $cookie);
$p->start_html($q, 'Set Style');

print <DATA>;

undef($q);
undef($p);
undef($style);
undef(%in);
undef($cookie);

__END__
<h2>Set Style</h2>

Your new style has been set, you may continue to <a
href="/~dustin/photo/">browse the photo album</a> to
make use of it.

</body>
</html>
