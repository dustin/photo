#!/usr/local/bin/perl -w
# Copyright (c) 1997  Dustin Sallings
#
# $Id: img.cgi,v 1.3 1998/09/04 04:05:42 dustin Exp $

use Photo;
use strict;

my($p, $img);
$p=Photo->new;

$img=$ENV{PATH_INFO};
$img=~s/^\///;
$img=~s/[^\/0-9\.tnjpgif]//g;

$p->displayImage($img);
