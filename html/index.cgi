#!/usr/local/bin/perl -w

use CGI;
use Photo;
use strict;

my($q, $p, %p);

$q=CGI->new;
$p=Photo->new;

print $q->header;

$p{'SAVED'}=$p->showSaved($q);

$p->showTemplate("index.inc", %p);
$p->showTemplate("tail.inc", %p);
