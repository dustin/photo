#!/usr/local/bin/perl -w
# Copyright (c) 1997  Dustin Sallings
# Approved by Jason Hudgins =)	
# $Id: admin.cgi,v 1.12 1998/11/13 07:32:16 dustin Exp $

use CGI;
use Photo;
use strict;

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

sub deleteImage
{
    my($q, $p)=@_;
    $p->deleteImage($q->param('id'));
    listRecent($q,$p);
}

sub doDisplay
{
    my($q, $p)=@_;
    my($query, $s, @r, @mapme, %p, $n, $r, $tmp);
    %p=();

    $query ="select a.id,a.keywords,a.descr,\n";
    $query.="    a.size,a.taken,a.ts,b.name,a.cat,b.id\n";
    $query.="    from album a, cat b\n";
    $query.="    where a.cat=b.id and a.id=" . $q->param('id');

    print "<!-- Query:\n$query\n-->\n";

    $s=$p->doQuery($query);

    @r=@{$s->fetch};
    $s->finish;
    @mapme=qw(IMAGE KEYWORDS INFO SIZE TAKEN TIMESTAMP CAT CATNUM);
    map { $p{$mapme[$_]}=$r[$_] } (0..$#r);

    print $q->start_html(-title=>"Editing $p{'IMAGE'}", -bgcolor=>'#fFfFfF');

    $p{'CATS'}="";
    $query ="select * from cat order by name";
    $s=$p->doQuery($query);
    while($r=$s->fetch) {
	$tmp=($r->[0]==$p{'CATNUM'})?"selected":"";
	$p{'CATS'}.="    <option value=\"$r->[0]\" $tmp>$r->[1]\n";
    }
    $s->finish;

    $p->showTemplate("$Photo::includes/admin/display.inc", %p);
}

sub editCat
{
    my($q, $p)=@_;
    my($cat, $query, $s, $r, %p);

    $cat=$q->param('cat');

    print $q->start_html(-title=>"Editing category $cat", -bgcolor=>"#fFfFfF");

    if($cat>0) {
	$query="select * from cat where id=$cat;\n";
	$s=$p->doQuery($query);
	$r=$s->fetch;
	$s->finish;
    } else {
	$r=[0,''];
    }

    %p=('CATID' => $r->[0], 'CATNAME' => $r->[1]);

    $p->showTemplate("$Photo::includes/admin/editcat.inc", %p);
}

sub search
{
    my($q, $p)=@_;
    $p->doFind($q);
}

sub listRecent
{
    my($q, $p)=@_;
    my($query, $s, $r, %p, $start, $i, $maxret);

    print $q->start_html(-title=>'Image Administration',
		         -bgcolor=>'#fFfFfF');

    $query ="select a.id,a.name,b.id,b.cat,b.keywords,b.taken,b.ts\n";
    $query.="    from cat a, album b where a.id=b.cat\n";
    $query.="\tand b.cat=$i\n" if(defined($i=$q->param('cat')));
    $query.="    order by a.name, b.ts desc;";

    print "<!-- $query -->\n";

    $s=$p->doQuery($query);

    %p=();

    $p->showTemplate("$Photo::includes/admin/recent.inc", %p);

    $i=0;
    if(defined($start=$q->param('qstart'))) {
	while($i<$start) {$s->fetch; $i++}
    }
    if(!defined($maxret=$q->param('maxret'))) {
	$maxret=20;
    }
    $i=$maxret;

    while($r=$s->fetch) {
        ($p{'AID'},$p{'CAT'},$p{'IMAGE'},$p{'BCAT'},$p{'KEYWORDS'},
	 $p{'TAKEN'},$p{'TS'})=@{$r};
         $p->showTemplate("$Photo::includes/admin/recent_row.inc", %p);

	 last if(--$i==0);
    }
    $s->finish;

    print "</table>\n";

    map { $q->delete($_) } qw(qstart);

    if( (($start+$maxret) < $s->rows) && $maxret>0) {
        print $q->startform(-method=>'POST',-action=>$q->url);
        print $q->hidden(-name=>'qstart', '-value'=>($start+$maxret));
        print $q->hidden(-name=>'func');
        print $q->hidden(-name=>'cat');
	print $q->hidden(-name=>'maxret', '-value'=>$maxret);

	if(($s->rows-($start+$maxret))<$maxret) {
	    $i=($s->rows-($start+$maxret));
	    $i=($i==1)?"match":"$i matches";
	} else {
	    $i="$maxret matches";
	}
        print $q->submit(-value=>"Next $i");
        print $q->endform;
    }
}

sub listUsers
{
    my($q, $p)=@_;
    my($query, $r, $s);

    print $q->start_html(-title=>'User List', -bgcolor=>'#fFfFfF');

    print "<h2>User List</h2>\nClick on the id # to edit the entry.\n";
    print "<ul>\n";

    $query="select username from wwwusers order by username";
    $s=$p->doQuery($query);

    while($r=$s->fetch) {
         print "    <li>\n\t";
         print "<a href=\"/cgi-bin/dustin/photo/admin/admin.cgi".
	       "?func=edituser&user=$r->[0]\">";
         print "$r->[0]</a></li>\n";
    }
    $s->finish;

    print <<EOF;
</ul>

<br>
<a
href="$Photo::cgidir/admin/admin.cgi?func=edituser">Add a new user</a>
</body>
</html>
EOF
}

sub editUser
{
    my($q, $p)=@_;
    my($query, $r, $s, $user, %p, @ok);
    my($checkedon, $checkedoff, $color, $id);

    $user=$q->param('user');


    if($user ne "") {
        $query="select * from wwwusers where username='$user';";
        $s=$p->doQuery($query);

        $r=$s->fetch;
	$s->finish;
    } else {
        $r=['', '', '', '', ''];
    }

    ($p{'USERID'}, $p{'USER'}, $p{'PASS'}, $p{'EMAIL'}, $p{'REALNAME'},
		$p{'ADDVAL'})=@{$r};
	if($p{'ADDVAL'}==1) {
		$p{'CANADD'}='CHECKED';
		$p{'CANNOTADD'}='';
	} else {
		$p{'CANADD'}='';
		$p{'CANNOTADD'}='CHECKED';
	}

    print $q->start_html(-title=>"Editing $r->[0]", -bgcolor=>'#fFfFfF');

    if($r->[0] eq '')
    {
	$p{'USERPART'}=
        "<tr><td>Username:</td><td><input name=\"username\"></td></tr>\n" .
        "<input type=\"hidden\" name=\"newuser\" value=\"1\">\n";
    }
    else
    {
	$p{'USERPART'}=
        "<input type=\"hidden\" name=\"username\" value=\"$r->[0]\">\n" .
        "<input type=\"hidden\" name=\"newuser\" value=\"0\">\n";
    }

    $p->showTemplate("$Photo::includes/admin/userform.inc", %p);

	$id=getuid($user);
    $query="select cat from wwwacl where userid=$id";

    $s=$p->doQuery($query);

    while($r=$s->fetch) { $ok[$r->[0]]=1; }

    $query="select * from cat order by name";

    $s=$p->doQuery($query);

    while($r=$s->fetch) {
        if($ok[$r->[0]]==1) {
	    $checkedon="checked";
	    $checkedoff="";
	    $color="007f00";
        }
        else
        {
	    $checkedon="";
	    $checkedoff="checked";
	    $color="ff0000";
        }
        print "<tr><td><font color=\"#$color\">$r->[1]</font></td><td>";

        print "Yes <input type=\"radio\" ";
        print "name=\"cat$r->[0]\" $checkedon value=\"1\">\n";
        print "No <input type=\"radio\" ";
        print "name=\"cat$r->[0]\" $checkedoff value=\"0\">\n";
        print "</td></tr>\n";
    }
    $s->finish;

    print <<EOF;
</table>
<input type="submit" value="Save"> <input type="reset" value="Reset">

</form>
EOF
}

sub listCats
{
    my($q, $p)=@_;
    my($query, $r, $s);

    print $q->start_html(-title=>'Category List', -bgcolor=>'#fFfFfF');

    print "<h2>Category List</h2>\nClick on the id # to edit the entry.\n";

    $query="select * from cat order by name;";
    $s=$p->doQuery($query);

    print <<EOF;
<table border="3">
<tr>
    <th>ID</th>
    <th>Name</th>
</tr>
EOF

    while($r=$s->fetch) {
        print "<tr>";
        print "<td><a href=\"$Photo::cgidir/admin/admin.cgi";
	print "?func=editcat&cat=$r->[0]\">";
        print "$r->[0]</a></td>\n";
        print "<td>$r->[1]</td>\n";
        print "<tr>";
    }
    $s->finish;

    print <<EOF;
</table>

<br>
<a
href="$Photo::cgidir/admin/admin.cgi?func=editcat&cat=0">Add a new category</a>

</body>
</html>
EOF
}

sub saveCat
{
    my($q,$p)=@_;
    my($query, %in, $word);
    %in=map { $_, $q->param($_) } $q->param;

    if($in{'id'}>0) {
	$query ="update cat set name='$in{'name'}'\n";
	$query.="    where id=$in{'id'};";
	$word="Modifed";
    } else {
	$query ="insert into cat (name) values('$in{'name'}')";
	$word="Added";
    }

    $p->doQuery($query);

    print $q->start_html(-title=>"$word category $in{'name'}",
			 -bgcolor=>"#fFfFfF");

    print "$word category $in{'name'}<br><hr>\n";
    listCats($q,$p);
}

sub saveUser
{
    my($q,$p)=@_;
    my(%in, $query, $s, $r);
    %in=map { $_, $q->param($_) } $q->param;

    print $q->start_html(-title=>"Saving $in{'username'}",
			 -bgcolor=>"#fFfFfF");
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
        my @vars=('username', 'realname', 'email', 'canadd');
        $query ="update wwwusers set\n";
        map {
            $query.="    $_='$in{$_}',\n";
        } @vars;
        $query.="    password=$in{'password'}\n";
        $query.="    where username='$in{'username'}';";

        print "Saving $in{'username'}\n<br><hr>\n";
    }
    else
    {
        $query ="insert into wwwusers (username, password, email, realname)\n";
        $query.="    values('$in{'username'}', $in{'password'},\n";
        $query.="           '$in{'email'}', '$in{'realname'}')";

        print "Adding $in{'username'}\n<br><hr>\n";
    }

    print "<!-- $query -->\n";
    $p->doQuery($query);

    $query="delete from wwwacl where userid=$in{'username'}\n";
    print "<!-- $query -->\n";
    $p->doQuery($query);

    $query="select id from cat\n";
    print "<!-- $query -->\n";
    $p->doQuery($query);
    $s=$p->doQuery($query);

    while($r=$s->fetch) {
	if($in{"cat$r->[0]"}==1) {
	    $query="insert into wwwacl values($in{'username'}, $r->[0])\n";
	    print "<!-- $query -->\n";
	    $p->doQuery($query);
	}
    }
    $s->finish;

    listUsers($q,$p);
}

sub editText
{
    my($q,$p)=@_;
    my(%in, $query);
    %in=map { $_, $q->param($_) } $q->param;
    print $q->start_html(-title=>"Updating ID $in{'id'}",
			 -bgcolor=>"#fFfFfF");
    $in{'info'}=~s/\'/\\'/g;
    $in{'keywords'}=~s/\'/\\\'/g;
    $in{'taken'}=~s/\'/\\\'/g;

    $query ="update album set cat=$in{cat}, keywords='$in{keywords}',\n";
    $query.="    descr='$in{info}', taken='$in{taken}'\n";
    $query.="    where id=$in{'id'};";

    $p->doQuery($query);

    doDisplay($q,$p);
}

my %funcs=(

    'deleteImage' => \&deleteImage,
    'display'     => \&doDisplay,
    'editcat'     => \&editCat,
    'edittext'    => \&editText,
    'edituser'    => \&editUser,
    'listcats'    => \&listCats,
    'listrecent'  => \&listRecent,
    'listusers'   => \&listUsers,
    'search'      => \&search,
    'savecat'     => \&saveCat,
    'saveuser'    => \&saveUser,

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
