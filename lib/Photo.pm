# Photo library routines
# Copyright(c) 1997-1998  Dustin Sallings
#
# $Id: Photo.pm,v 1.3 1998/04/25 23:27:18 dustin Exp $

package Photo;

use CGI;
use DBI;
use strict;

use vars qw($cgidir $imagedir $uriroot $Itop $includes $adminc);

# Global stuffs
$Photo::cgidir="/perl/dustin/photo";
$Photo::imagedir="/~dustin/images/";
$Photo::uriroot="/~dustin/photo";
$Photo::Itop="$Photo::uriroot/album";
$Photo::includes="/usr/people/dustin/public_html/photo/inc";
$Photo::adminc="/usr/people/dustin/public_html/photo/admin/inc";

sub new
{
    my $self = {};
    bless($self);
    return($self);
}

sub openDB
{
    my($self)=shift;
    $self->{'dbh'}=DBI->connect("dbi:Pg:dbname=photo", 'dustin','')
         || die("Cannot connect to database\n");
}

sub doQuery
{
    my $self=shift;
    my($query)=@_;
    my($s,$dbh);

    $self->openDB unless($self->{'dbh'});

    $s=$self->{'dbh'}->prepare($query)
	  || die("Database Error:  $DBI::errstr\n<!--\n$query\n-->\n");

    $s->execute
	  || die("Database Error:  $DBI::errstr\n<!--\n$query\n-->\n");

    return($s);
}

sub addTail
{
    my $self=shift;
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

    $self->showTemplate("$includes/tail.inc", %p);
}

sub myself
{
    my $self=shift;
    my($s);
    $s=$ENV{REQUEST_URI};
    $s=~s/(.*?)\?.*/$1/;
    return($s);
}

sub showTemplate
{
    my $self=shift;
    my($fn, %p)=@_;
    my($q);

    $q=CGI->new;
    map { $p{uc($_)}=$q->param($_) unless(defined($p{uc($_)}))} $q->param;
    map { $p{$_}=$ENV{$_} unless(defined($p{uc($_)})) } keys(%ENV);

    $p{'URIROOT'}=$Photo::uriroot;
    $p{'CGIDIR'}=$Photo::cgidir;
    $p{'IMAGEDIR'}=$Photo::imagedir;
    $p{'ITOP'}=$Photo::Itop;
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

sub deleteImage
{
    my($self, $oid)=@_;
    my($query, $s, $r, %p);

    $query ="select a.id,a.name,b.keywords,b.descr,b.fn,b.cat,b.oid\n";
    $query.="    from cat a, album b\n";
    $query.="    where a.id=b.cat and b.oid=$oid;";

    $p{oid}=$oid;

    $s=$self->doQuery($query);

    if($r=$s->fetch)
    {
	($p{AID}, $p{CAT}, $p{KEYWORDS}, $p{DESCR}, $p{IMAGE})=@{$r};
    }

    $query="delete from album where oid=$oid;\n";
    if($self->doQuery($query))
    {
	unlink("$Photo::Itop/$p{IMAGE}");
	unlink("$Photo::Itop/tn/$p{IMAGE}");
    }

    $self->showTemplate("$Photo::includes/admin/killimage.inc", %p);
}

1;
