# Photo library routines
# Copyright(c) 1997-1998  Dustin Sallings
#
# $Id: Photo.pm,v 1.4 1998/04/30 05:17:42 dustin Exp $

package Photo;

use CGI;
use DBI;
use strict;

use vars qw($cgidir $imagedir $uriroot $Itop $includes $adminc $ldir);

# Global stuffs
$Photo::cgidir="/perl/dustin/photo";
$Photo::imagedir="/~dustin/images/";
$Photo::uriroot="/~dustin/photo";
$Photo::Itop="$Photo::uriroot/album";
$Photo::includes="/usr/people/dustin/public_html/photo/inc";
$Photo::adminc="/usr/people/dustin/public_html/photo/admin/inc";
$Photo::ldir="/usr/people/dustin/public_html/photo/album";

sub new
{
    my $self = {};
    bless($self);
    return($self);
}

sub openDB
{
    my($self)=shift;
    $self->{'dbh'}=DBI->connect("dbi:Pg:dbname=photo", '','')
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

sub addImage
{
    my($self, $q)=@_;
    my(@elements, %in, %tmp, $query, $ext, $fn, $f, @stat, $s);

    @elements=qw(category keywords picture info);
    %tmp=map{$_,1}@elements;
    %in=map{
	  $_,defined($tmp{$_})?$self->{dbh}->quote($q->param($_)):$q->param($_)
        }$q->param;

    print $q->start_html(-title=>'Adding image',-bgcolor=>'#fFfFfF');

    if($in{'img'}=~/jpg$/i) {
	$ext="jpg";
    } elsif($in{'img'}=~/gif$/i) {
	$ext="gif";
    } else {
	%tmp=('FILENAME',$in{'img'});
	$self->showTemplate("$Photo::includes/add_badfiletype.inc", %tmp);
	return;
    }

    $fn=time()."$$.$ext";
    $f=$q->param('picture');
    open(OUT, ">$Photo::ldir/$fn");
    print OUT <$f>;
    close(OUT);
    @stat=stat("$Photo::ldir/$fn");
    if($stat[7]==0)
    {
	unlink("$Photo::ldir/$fn");
	%tmp=('FILENAME',$in{'img'});
	$self->showTemplate("$Photo::includes/add_badfiletype.inc", %tmp);
	return;
    }

    system('/usr/local/bin/convert', '-size', '100x100',
	   "$Photo::ldir/$fn", "$Photo::ldir/tn/$fn");

    $query ="insert into album (fn, keywords, descr, cat, size, taken)\n";
    $query.="    values('$fn',\n\t'$in{'keywords'}',\n\t'$in{'$info'}',\n";
    $query.="\t$in{'cat'},\n\t$stat[7],\n\t'$in{'$taken'}');";

    eval { $s=$self->doQuery($query); };

    if($@) {
	%tmp=('QUERY', $query, 'ERRSTR', $DBI::errstr);
	$self->showTemplate("$Photo::includes/add_dbfailure.inc", %tmp);
    } else {
	%tmp=(
	    'OID' => $s->{'pg_oid_status'},
	    'QUERY' => $query
	);
	$self->showTemplate("$Photo::includes/add_success.inc", %tmp);
    }
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
