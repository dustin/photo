# Photo library routines
# Copyright(c) 1997-1998  Dustin Sallings
#
# $Id: Photo.pm,v 1.8 1998/06/29 05:54:34 dustin Exp $

package Photo;

use CGI;
use DBI;
use MIME::Base64;
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

sub buildQuery
{
    my($self, $q)=@_;
    my(%h, $query, $needao, $sub, @tmp, $tmp, $snao, $ln, $order);
    my(@a);

    for($q->param)
    {
        $h{$_}=$q->param($_);
        $h{$_}=~s/\'/\\\'/g;
    }


    $query ="select a.oid,a.keywords,a.descr,b.name,\n";
    $query.="    a.size,a.taken,a.ts,a.fn,a.cat,b.id\n";
    $query.="    from album a, cat b\n    where a.cat=b.id\n";
    $query.="        and a.cat in (select cat from wwwacl\n";
    $query.="                       where username='$ENV{REMOTE_USER}')";

    if($h{searchtype} eq "simple") {
        $query.="\n    and $h{field} ~* '$h{what}'";
    } else {
        $needao=0;
        $sub="";

        @tmp=$q->param('cat');

        if(@tmp) {
            $sub.=" and" if($needao++>0);
            $tmp="";

	    map {
                $tmp.=" or" if($snao++>0);
                $tmp.="\n          a.cat=$_";
	    } @tmp;

            if(@tmp>1) {
                $sub.="\n      ($tmp\n      )";
            } else {
                $sub.="\n    $tmp";
            }
        }

        if($h{what} ne "") {
            my($a, $b, $c);
            $c=0;

            $sub.=" $h{fieldjoin}" if($needao++>0);

            @a=split(/\s+/, $h{what});
	    $b=($h{keyjoin} eq "and")?"and":"or";

            if(@a>1) {
                $sub.="\n      (";
                map {
                    $sub.=" $b" if($c++>0);
                    $sub.="\n\t$h{field} ~* '$_'";
                } @a;
                $sub.="\n      )";
            } else {
                $sub.="\n      $h{field} ~* '$h{what}'";
            }
        }

        if($h{tstart}) {
            $sub.=" $h{tstartjoin}" if($needao++>0);
            $sub.="\n      a.taken>='$h{tstart}'";
        }

        if($h{tend}) {
            $sub.=" $h{tendjoin}" if($needao++>0);
            $sub.="\n      a.taken<='$h{tend}'";
        }

        if($h{start}) {
            $sub.=" $h{startjoin}" if($needao++>0);
            $sub.="\n      a.ts>='$h{start}'";
        }

        if($h{end}) {
            $sub.=" $h{endjoin}" if($needao++>0);
            $sub.="\n      a.ts<='$h{end}'";
        }

        if(length($sub)>0) {
            $query.=" and\n    ($sub\n    )";
        }
    }

    if(defined($h{order}) && $h{order}=~/[A-z0-9]/) {
        $order=$h{order};
    } else {
        $order="a.taken";
    }

    $h{sdirection}|="";
    $query.="\n    order by $order $h{sdirection};\n";

    return($query);
}

sub saveSearch
{
    my($self, $q)=@_;
    my($query, $name, %p);

    print $q->start_html(
	-title=>'Saving your Search',
	-bgcolor=>'#fFfFfF'
    );

    $name=$q->param('name');

    $query ="insert into searches (name, addedby, search) ";
    $query.="values(\n\t'$name', '$ENV{REMOTE_USER}',\n'",
    $query.=$q->param('search');
    $query.="');\n";

    $self->doQuery($query);

    %p=('QUERY', $query);
    $self->showTemplate("$Photo::includes/savedsearch.inc", %p);
}

sub showSaved
{
    my($self, $q)=@_;
    my($query, $name, $param, $s, $r, $cgi, %p, $out);

    $query="select * from searches order by name,addedby\n";

    $s=$self->doQuery($query);

    # $r is id, name, addedby, search

    $cgi =$Photo::cgidir;
    $cgi.="/photo.cgi";

    while($r=$s->fetch) {
	$out.= "    <li><a href=\"$cgi?" . decode_base64($r->[3])
	       . "\">$r->[1]</a></li>\n";
    }

    return($out);
}

sub doFind
{
    my($self, $q)=@_;
    my($query, $s, $i, $start, $max, %p, $n, $nn, $r);

    print $q->start_html(
        -title=>'Find results',
        -bgcolor=>'#FfFfFf');

    $query=$self->buildQuery($q);

    print "<!--\n$query\n-->\n";

    $s=$self->doQuery($query);

    $n=$s->rows;
    $i=0;

    $start=$q->param('qstart');  # Find the desired starting point
    $start+=0;                   # make it a number
    $q->delete('qstart');        # Delete it so we can add it later

    if($start==0) { # is this your first time?
	my($selfurl, %stuff);
	$selfurl=encode_base64($q->query_string);

	%stuff=("SEARCH" => $selfurl);
        $self->showTemplate("$Photo::includes/savesearch.inc", %stuff);
    }

    $max=$q->param('maxret');    # Find the desired max return
    $max+=0;                     # make it a number

    print "<h2>Found $n match".(($n==1)?"":"es").":</h2><br><ul>\n";

    while($r=$s->fetch)
    {
        ($p{OID}, $p{KEYWORDS}, $p{DESCR}, $p{CAT}, $p{SIZE}, $p{TAKEN},
            $p{TS}, $p{IMAGE})=@{$r};
        next if($i++<$start);

        last if( $max>0 && $i-$start>$max);

        print "<li>\n";
        $self->showTemplate("$Photo::includes/findmatch.inc", %p);
        print "</li>\n";
    }

    print "</ul>\n";

    # Add a link to the next matches.
    if( (($start+$max) < $n) && $max>0) {
        if(($n-($start+$max))<$max) {
            $nn=($n-($start+$max));
            $nn=($nn==1)?"match":"$nn matches";
        } else {
            $nn="$max matches";
        }

        print $q->startform(-method=>'POST',-action=>$q->url);
        print $q->hidden(-name=>'qstart', -value=>$start+$max);

        print map { $q->hidden(-name=>$_, -value=>$q->param($_)) . "\n" }
        $q->param;

        print $q->submit(-value=>"Next $nn");

        print $q->endform;
    }
}

sub doDisplay
{
    my($self, $q)=@_;
    my($query, $s, @r, @mapme, %p);
    %p=();

    $query ="select a.oid,a.fn,a.keywords,a.descr,\n";
    $query.="    a.size,a.taken,a.ts,b.name,a.cat,b.id\n";
    $query.="    from album a, cat b\n";
    $query.="    where a.cat=b.id and a.oid=" . $q->param('oid');
    $query.="\n    and a.cat in (select cat from wwwacl where ";
    $query.="username='$ENV{REMOTE_USER}');\n";

    print "<!-- Query:\n$query\n-->\n";

    $s=$self->doQuery($query);

    if($s->rows<1) {
        print "ACL ERROR!!!  We don't want your type here.\n";
    } else {
        @r=@{$s->fetch};
        @mapme=qw(OID IMAGE KEYWORDS INFO SIZE TAKEN TIMESTAMP CAT CATNUM);
        map { $p{$mapme[$_]}=$r[$_] } (0..$#r);
        $self->showTemplate("$Photo::includes/display.inc", %p);
    }
}

sub doCatView
{
    my($self, $q)=@_;
    my($r, $query, $s, $t);

    print $q->start_html(
        -title=>'View Images by Category',
        -bgcolor=>'#fFfFfF') . "\n";

    print "<h2>Category List</h2>\n";

    $query ="select name,id,catsum(id) as cs from cat\n";
    $query.="where id in\n";
    $query.="  (select cat from wwwacl where username='$ENV{REMOTE_USER}')\n";
    $query.=" order by cs desc;";
    print "<!--\n$query\n-->\n";
    $s=$self->doQuery($query);

    print "<ul>\n";

    while($r=$s->fetch)
    {
        next if($r->[2]==0);

        $t=($r->[2]==1?"image":"images");

        print "<li>$r->[0]:  <a href=\"$Photo::cgidir/photo.cgi?func=search&".
        "searchtype=advanced&cat=$r->[1]&maxret=10\">$r->[2] $t</a></li>\n";
    }

    print "</ul>\n" . $q->end_html . "\n";
}

sub myquote
{
    my($self, $str)=@_;
    $self->openDB unless($self->{'dbh'});
    return($self->{'dbh'}->quote($str));
}

sub addImage
{
    my($self, $q)=@_;
    my(@elements, %in, %tmp, $query, $ext, $fn, $f, @stat, $s, $r);

    @elements=qw(category keywords picture info taken);
    %tmp=map{$_,1}@elements;
    %in=map{
      $_,defined($tmp{$_})?$self->myquote($q->param($_)):$q->param($_)
    }$q->param;

    print $q->start_html(-title=>'Adding image',-bgcolor=>'#fFfFfF');

    $query="select * from wwwusers where username='$ENV{'REMOTE_USER'}'\n";
    $s=$self->doQuery($query);
    $r=$s->fetch;
    if($r->[4]!=1)
    {
	$self->showTemplate("$Photo::includes/add_denied.inc", ());
	return;
    }

    if($in{'picture'}=~/jpg.$/i) {
	$ext="jpg";
    } elsif($in{'picture'}=~/gif.$/i) {
	$ext="gif";
    } else {
	%tmp=('FILENAME',$in{'picture'});
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
	%tmp=('FILENAME',$in{'picture'});
	$self->showTemplate("$Photo::includes/add_uploadfail.inc", %tmp);
	return;
    }

    system('/usr/local/bin/convert', '-size', '100x100',
         "$Photo::ldir/$fn", "$Photo::ldir/tn/$fn");

    $query ="insert into album (fn, keywords, descr, cat, size, taken)\n";
    $query.="    values('$fn',\n\t$in{'keywords'},\n\t$in{'info'},\n";
    $query.="\t$in{'category'},\n\t$stat[7],\n\t$in{'taken'});";

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