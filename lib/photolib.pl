# Photo library routines
# Copyright(c) 1997-1998  Dustin Sallings
#
# $Id: photolib.pl,v 1.8 1998/04/24 17:09:19 dustin Exp $

use CGI;
use DBI;

# Global stuffs
$cgidir="/cgi-bin/dustin/photo";
$imagedir="/~dustin/images/";
$uriroot="/~dustin/photo";
$Itop="$uriroot/album";
$includes="/usr/people/dustin/public_html/photo/inc";
$adminc="/usr/people/dustin/public_html/photo/admin/inc";

# Here we store the persistent database handler.
local($dbh)=0;

sub openDB
{
    $dbh=DBI->connect("dbi:Pg:dbname=photo", 'dustin','')
         || die("Cannot connect to database\n");
}

sub doQuery
{
    my($query)=@_;
    my($s);

    # Open the database if it's not already.
    openDB() unless($dbh);

    $s=$dbh->prepare($query)
	  || die("Database Error:  $DBI::err\n<!--\n$query\n-->\n");

    $s->execute
	  || die("Database Error:  $DBI::err\n<!--\n$query\n-->\n");

    return($s);
}

sub addTail
{
    my(%p, @a, @vars);

    @vars=qw(FILE_DEV FILE_INO FILE_MODE FILE_NLINK FILE_UID FILE_GID
             FILE_RDEV FILE_SIZE FILE_ATIME FILE_MTIME FILE_TIME
             FILE_BLKSIZE FILE_BLOCKS);

    @a=stat($ENV{'SCRIPT_FILENAME'});
    for(0..$#a)
    {
        $p{$vars[$_]}=$a[$_];
    }

    $p{'LAST_MODIFIED'}=localtime($p{FILE_MTIME});

    showTemplate("$includes/tail.inc", %p);
}

sub myself
{
    my($self);
    $self=$ENV{REQUEST_URI};
    $self=~s/(.*?)\?.*/$1/;
    return($self);
}

sub showTemplate
{
    my($fn, %p)=@_;
    my($q);

    $q=CGI->new;
    map { $p{uc($_)}=$q->param($_) unless(defined($p{uc($_)}))} $q->param;
    map { $p{$_}=$ENV{$_} unless(defined($p{uc($_)})) } keys(%ENV);

    $p{'URIROOT'}=$uriroot;
    $p{'CGIDIR'}=$cgidir;
    $p{'IMAGEDIR'}=$imagedir;
    $p{'ITOP'}=$Itop;
    $p{'SELF_URI'}=&myself;

    $p{'ALL_VARS'}=join("\n", sort(keys(%p)));

    open(IN, $fn) || die("Can't open $fn:  $!\n");
    while(<IN>)
    {
        s/%([A-Z0-9_]+)%/$p{$1}/g;
        print;
    }

    close(IN);
}

1;
