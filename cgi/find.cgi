#!/usr/local/bin/perl
# Copyright (c) 1997  Dustin Sallings
#
# $Id: find.cgi,v 1.10 1997/12/07 04:09:43 dustin Exp $

use CGI;
use Postgres;

$idir="/~dustin/photo/album";

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

    if($h{order}=~/[A-z0-9]/)
    {
	$order=$h{order};
    }
    else
    {
	$order="a.taken";
    }

    $query.="\n    order by $order $h{sdirection};\n";

    return($query);
}

$q=CGI->new;

print $q->header;

$dbh=db_connect('photo');

print $q->start_html(
    -title=>'Find results',
    -bgcolor=>'#FfFfFf');

$query ="select a.username, a.cat, b.id, b.name from wwwacl a, cat b\n";
$query.="    where a.cat=b.id and a.username='$ENV{REMOTE_USER}'\n";

print "\n<!-- Auth query:\n$query\n-->\n";

if(!($s=$dbh->execute($query)))
{
    print "Database Error:  $Postgres::error\n";
    exit(0);
}

while(@r=$s->fetchrow())
{
    $ok{$r[3]}=1;
}

$query=buildQuery($q);

print "<!--\n$query\n-->\n";

if(!($s=$dbh->execute($query)))
{
    print "Database Error:  $Postgres::error\n";
    exit(0);
}

$n=$s->ntuples;
$i=0;

$start=$q->param('qstart');  # Find the desired starting point
$start+=0;                   # make it a number
$q->delete('qstart');        # Delete it so we can add it later

$max=$q->param('maxret');    # Find the desired max return
$max+=0;                     # make it a number

print "<h2>Found $n matches:</h2><br><ul>\n";

while(($oid, $keywords, $descr, $cat, $size, $taken, $ts, $image)
    =$s->fetchrow())
{
    next if(!defined($ok{$cat}));
    next if($i++<$start);

    if($max>0)
    {
        last if($i-$start>$max);
    }

    print "    <li>\n<table>\n<tr>\n<td valign=\"top\">\n";
    print "\nKeywords: $keywords<br>\n";
    print "Category:  $cat<br>\n";
    print "Size:  $size bytes<br>\n";
    print "Taken:  $taken<br>\n";
    print "Added:  $ts<br>\n";
    print "</td><td><a href=\"/cgi-bin/dustin/photo/display.cgi?$oid\">";
    print "<img border=\"0\" src=\"$idir/tn/$image\"></a>\n";
    print "</td></tr></table>\n";
    print "<blockquote>\n$descr\n</blockquote>\n</li>\n";
}

print "</ul>\n";

# Add a link to the next matches.
if( (($start+$max) < $n) && $max>0)
{
    if(($n-($start+$max))<$max)
    {
	$nn=($n-($start+$max));

	if($nn==1)
	{
	    $nn="match";
	}
	else
	{
	    $nn="$nn matches";
	}
    }
    else
    {
	$nn="$max matches";
    }

    print $q->startform(-method=>'POST');
    print $q->hidden(-name=>'qstart', -value=>$start+$max);

    for($q->param)
    {
	print $q->hidden(-name=>$_, -value=>$q->param($_)) . "\n";;
    }

    print $q->submit(-value=>"Next $nn");

    print $q->endform;
}

print $q->end_html;
