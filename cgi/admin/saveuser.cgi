#!/usr/local/bin/perl -w
#
# Copyright (c) 1997  Dustin Sallings
#
# $Id: saveuser.cgi,v 1.2 1997/12/07 07:10:36 dustin Exp $

use Postgres;
use CGI;

$q=CGI->new;
for($q->param)
{
    $in{$_}=$q->param($_);
}

print <<EOF;
Content-type: text/html

<html><head><title>Saving $in{'username'}</title></head>
<body bgcolor="fFfFfF">

<h2>Saving $in{'username'}</h2>

EOF

print "<!-- Variables:\n";

for $key (sort(keys(%in)))
{
    print "$key:  $in{$key}\n";
}

print "-->\n";

$in{'username'}=~s/\'/\\\'/g;

if(!($in{'password'}=~/[A-z0-9]/))
{
    $in{'password'}="NULL";
}
else
{
    $in{'password'}="'$in{'password'}'";
}

if($in{'newuser'} == 0)
{
    my @vars=('username', 'realname', 'email');
    $query ="update wwwusers set\n";
    for(@vars)
    {
	$query.="    $_='$in{$_}',\n";
    }
    $query.="    password=$in{'password'}\n";
    $query.="    where username='$in{'username'}';";

    print "<!-- $query -->\n";
}
else
{
    $query ="insert into wwwusers (username, password, email, realname)\n";
    $query.="    values('$in{'username'}', $in{'password'},\n";
    $query.="           '$in{'email'}', '$in{'realname'}')";

    print "<!-- $query -->\n";
}

$dbh=db_connect('photo');

if($dbh->execute($query))
{
    print "Successfully stored user info.<br>\n";
}
else
{
    print "ERROR:  $Postgres::error<br>\n$query\n";
}

$query="delete from wwwacl where username='$in{'username'}'\n";

print "<!-- $query -->\n";

if(!($dbh->execute($query)))
{
    print "ERROR:  $Postgres::error<br>\n$query\n";
}

$query="select id from cat\n";

print "<!-- $query -->\n";

if(!($s=$dbh->execute($query)))
{
    print "ERROR:  $Postgres::error<br>\n$query\n";
}

while(($id)=$s->fetchrow())
{
    if( $in{"cat$id"} == 1 )
    {
	$query="insert into wwwacl values('$in{'username'}', $id)\n";

        print "<!-- $query -->\n";

        if(!($dbh->execute($query)))
        {
            print "ERROR:  $Postgres::error<br>\n$query\n";
        }
    }
}
