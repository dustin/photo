#!/usr/local/bin/perl -w
# Copyright (c) 1997  Dustin Sallings
#
# $Id: photo.cgi,v 1.13 1998/06/29 05:08:08 dustin Exp $

use CGI;
use Photo;
use strict;

sub badFunc
{
    print $_[0]->start_html(-title=>'Bad Function Name', -bgcolor=>'#fFfFfF').
          "\n<h2>ERROR</h2>\nThe function you requested,".
	  "<i>``@{[$_[0]->param('func')]}''</i> is not valid.\n";
}

my(%funcs, $q, $p);
$q=CGI->new;
$p=Photo->new;

%funcs=(
    'search'     =>  sub{$p->doFind($q)},
    'display'    =>  sub{$p->doDisplay($q)},
    'catview'    =>  sub{$p->doCatView($q)},
    'addimage'   =>  sub{$p->addImage($q)},
    'savesearch' =>  sub{$p->saveSearch($q)},
);

print $q->header;
defined($funcs{$q->param('func')})?&{$funcs{$q->param('func')}}:badFunc($q);
$p->addTail();
print $q->end_html;
