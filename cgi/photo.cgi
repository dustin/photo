#!/usr/local/bin/perl -w
# Copyright (c) 1997  Dustin Sallings
#
# $Id: photo.cgi,v 1.6 1998/04/25 03:10:12 dustin Exp $

use CGI;
use Photo;
use strict;

sub buildQuery
{
    my($q)=@_;
    my(%h, $query, $needao, $sub, @tmp, $tmp, $snao, $ln, $order);
    my(@a);

    for($q->param)
    {
        $h{$_}=$q->param($_);
        $h{$_}=~s/\'/\\\'/g;
    }


    $query ="select a.oid,a.keywords,a.descr,b.name,\n";
    $query.="    a.size,a.taken,a.ts,a.fn,a.cat,b.id\n";
    $query.="    from album a, cat b\n    where a.cat=b.id";

    if($h{searchtype} eq "simple")
    {
        $query.="\n    and $h{field} ~* '$h{what}'";
    }
    else
    {
	$needao=0;
	$sub="";

	@tmp=$q->param('cat');

	if($#tmp>=0)
	{
	    $sub.=" and" if($needao++>0);
	    $tmp="";

	    for $ln (@tmp)
	    {
		$tmp.=" or" if($snao++>0);
	        $tmp.="\n          a.cat=$ln";
	    }

	    if($#tmp>0)
	    {
		$sub.="\n      ($tmp\n      )";
	    }
	    else
	    {
		$sub.="\n    $tmp";
	    }
	}

	if($h{what} ne "")
	{
	    my($a, $b, $c);
	    $c=0;

	    $sub.=" $h{fieldjoin}" if($needao++>0);

	    @a=split(/\s+/, $h{what});
	    if($h{keyjoin} eq "and")
	    {
		$b="and"
	    }
	    else
	    {
		$b="or";
	    }

            if($#a>0)
	    {
		$sub.="\n      (";
	        foreach $a (@a)
	        {
	            $sub.=" $b" if($c++>0);
		    $sub.="\n\t$h{field} ~* '$a'";
	        }
		$sub.="\n      )";
	    }
	    else
	    {
	        $sub.="\n      $h{field} ~* '$h{what}'";
	    }
	}

	if($h{tstart})
	{
	    $sub.=" $h{tstartjoin}" if($needao++>0);
	    $sub.="\n      a.taken>='$h{tstart}'";
	}

	if($h{tend})
	{
	    $sub.=" $h{tendjoin}" if($needao++>0);
	    $sub.="\n      a.taken<='$h{tend}'";
	}

	if($h{start})
	{
	    $sub.=" $h{startjoin}" if($needao++>0);
	    $sub.="\n      a.ts>='$h{start}'";
	}

	if($h{end})
	{
	    $sub.=" $h{endjoin}" if($needao++>0);
	    $sub.="\n      a.ts<='$h{end}'";
	}

        if(length($sub)>0)
	{
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

sub doFind
{
    my($q, $p)=@_;
    my($query, $s, $i, $start, $max, %p, %ok, $n, $nn, $r);

    print $q->start_html(
        -title=>'Find results',
        -bgcolor=>'#FfFfFf');

    $query ="select a.username, a.cat, b.id, b.name from wwwacl a, cat b\n";
    $query.="    where a.cat=b.id and a.username='$ENV{REMOTE_USER}'\n";

    print "\n<!-- Auth query:\n$query\n-->\n";

    $s=$p->doQuery($query);

    while($r=$s->fetch) { $ok{$r->[3]}=1; }

    $query=buildQuery($q);

    print "<!--\n$query\n-->\n";

    $s=$p->doQuery($query);

    $n=$s->rows;
    $i=0;

    $start=$q->param('qstart');  # Find the desired starting point
    $start+=0;                   # make it a number
    $q->delete('qstart');        # Delete it so we can add it later

    $max=$q->param('maxret');    # Find the desired max return
    $max+=0;                     # make it a number

    print "<h2>Found $n matches:</h2><br><ul>\n";

    while($r=$s->fetch)
    {
        ($p{OID}, $p{KEYWORDS}, $p{DESCR}, $p{CAT}, $p{SIZE}, $p{TAKEN},
            $p{TS}, $p{IMAGE})=@{$r};
        next if(!defined($ok{$p{CAT}}));
        next if($i++<$start);

        last if( $max>0 && $i-$start>$max);

        print "<li>\n";
        $p->showTemplate("$Photo::includes/findmatch.inc", %p);
        print "</li>\n";
    }

    print "</ul>\n";

    # Add a link to the next matches.
    if( (($start+$max) < $n) && $max>0) {
        if(($n-($start+$max))<$max) {
	    $nn=($n-($start+$max));

	    if($nn==1) {
	         $nn="match";
	    } else {
	         $nn="$nn matches";
	    }
        } else {
	    $nn="$max matches";
        }

        print $q->startform(-method=>'POST');
	print $q->hidden(-name=>'qstart', -value=>$start+$max);

        map { print $q->hidden(-name=>$_, -value=>$q->param($_)) }
        $q->param;

        print $q->submit(-value=>"Next $nn");

        print $q->endform;
    }

}

sub doDisplay
{
    my($q, $p)=@_;
    my($query, $s, @r, @mapme, %p, $n);
    %p=();

    $query ="select a.oid,a.fn,a.keywords,a.descr,\n";
    $query.="    a.size,a.taken,a.ts,b.name,a.cat,b.id\n";
    $query.="    from album a, cat b\n";
    $query.="    where a.cat=b.id and a.oid=" . $q->param('oid');

    print "<!-- Query:\n$query\n-->\n";

    $s=$p->doQuery($query);

    @r=@{$s->fetch};
    @mapme=qw(OID IMAGE KEYWORDS INFO SIZE TAKEN TIMESTAMP CAT CATNUM);
    map { $p{$mapme[$_]}=$r[$_] } (0..$#r);

    $query ="select count(*) from wwwacl where
    username='$ENV{REMOTE_USER}'\n";
    $query.="    and cat=$p{CATNUM}\n";

    $s=$p->doQuery($query);

    ($n)=@{$s->fetch};

    if($n==0) {
        print "ACL ERROR!!!  We don't want your type here.\n";
    } else {
        $p->showTemplate("$Photo::includes/display.inc", %p);
    }
}

sub doCatView
{
    my($q, $p)=@_;
    my(@ok, $ok, $r, $query, $s, $t);

    print $q->start_html(
	-title=>'View Images by Category',
	-bgcolor=>'#fFfFfF') . "\n";

    print "<h2>Category List</h2>\n";

    $query="select cat from wwwacl where username='$ENV{REMOTE_USER}'";
    $s=$p->doQuery($query);

    while($ok=$s->fetch) { $ok[$ok->[0]]=1 }

    $query="select name,id,catsum(id) as cs from cat order by cs desc";
    print "<!--\n$query\n-->\n";
    $s=$p->doQuery($query);

    print "<ul>\n";

    while($r=$s->fetch)
    {
	next if($ok[$r->[1]]!=1);
	next if($r->[2]==0);

	$t=($r->[2]==1?"image":"images");

	print "<li>$r->[0]:  <a href=\"$Photo::cgidir/photo.cgi?func=search&";
	print "searchtype=advanced&cat=$r->[1]\">$r->[2] $t</a></li>\n";
    }

    print "</ul>\n" . $q->end_html . "\n";
}

sub badFunc
{
    my($cgi, $funcname)=@_;

    print $cgi->start_html(
        -title=>'Bad Function Name',
        -bgcolor=>'#fFfFfF') ."\n";

    print "<h2>ERROR</h2>\nThe function you requested, <i>$funcname</i>\n";
    print "is not valid.\n";
    return;
}
my %funcs=(
    'search' => \&doFind,
    'display' => \&doDisplay,
    'catview' => \&doCatView,
);

my($func, $q, $p);
$q=CGI->new;
$p=Photo->new;

print $q->header;

$func=$q->param('func');

if(defined($funcs{$func})) {
    &{ $funcs{$func} }($q, $p);
} else {
    badFunc($q, $q->param('func'));
}
$p->addTail();
print $q->end_html;
