#!/usr/local/bin/perl -w

use CGI;
use Photo;
use strict;

my($q, $p, %p);

$q=CGI->new;
$p=Photo->new;

print $q->header;

$p{'SAVED'}=$p->showSaved($q);

$p->showTemplate("$Photo::includes/index.inc", %p);
$p->showTemplate("$Photo::includes/tail.inc", %p);
