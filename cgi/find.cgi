#!/usr/local/bin/perl
# Copyright (c) 1997  Dustin Sallings
#
# $Id: find.cgi,v 1.3 1997/11/04 08:01:43 dustin Exp $

use CGI;
use Postgres;

$idir="/~dustin/photo/album";

sub buildQuery
{
    my($q)=@_;
    my(%h, $query, $needao, $sub, @tmp, $tmp, $snao, $ln, $order);

    for($q->param)
    {
        $h{$_}=$q->param($_);
    }


    $h{what}=~s/\'/\\\'/g;

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
	    $sub.=" $h{fieldjoin}" if($needao++>0);
	    $sub.="\n      $h{field} ~* '$h{what}'";
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

    $query.="\n    order by $order;\n";

    return($query);
}

$q=CGI->new;

print $q->header;

$dbh=db_connect('photo');

print $q->start_html(
    -title=>'Find results',
    -bgcolor=>'#FfFfFf');

$query=buildQuery($q);

print "<!--\n$query\n-->\n";

if(!($s=$dbh->execute($query)))
{
    print "Database Error:  $Postgres::error\n";
    exit(0);
}

$n=$s->ntuples;

print "<h2>Found $n matches:</h2><br><ul>\n";

while(($oid, $keywords, $descr, $cat, $size, $taken, $ts, $image)
    =$s->fetchrow())
{
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

print $q->end_html;
