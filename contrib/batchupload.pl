#!/usr/local/bin/perl
# photo servlet batch upload script
# author : thanatos@incantations.net
# Jason Hudgins 
# $Id: batchupload.pl,v 1.1 2002/01/03 09:25:16 dustin Exp $

# do this right.
use strict;
use LWP::UserAgent;
use HTTP::Cookies;
use HTTP::Request::Common qw(POST);

# this is the struct we use for holding our image upload information.
my %upload = (
  servlet_url => "http://bleu.west.spy.net/servlet/PhotoServlet",
  category => "",
  date_taken => `date +"%m/%d/%Y"`,
  keywords => "",
  descr => "", 
  username => "",
  password => "",
);

# if we don't have any ARGV's... output our usage instructions.
if(!$ARGV[0]) {
	die "Usage: uploader.pl -h <servlet url> -c <category> -u <username> -p <password> -k <keywords> -s <description> -d <datestring> [-t] [file ...]\n -t : test mode, don't actually perform an upload.\n";
}

# strip out crap from the datestring.
$upload{date_taken} =~ s/\s//g;

# define an interator for the upcoming while loops.
my $i = 0;

# define an array that contains files to UP.
my @files_to_up;
# define our test flag.
my $test_flag = 0;

# parse the command line args and look for all
# our kick ass arguments.
while($i <= $#ARGV) {
	if ($ARGV[$i] eq "-c") {
		$upload{category} = $ARGV[++$i];
	} elsif ($ARGV[$i] eq "-h") {
		$upload{servlet_url} = $ARGV[++$i];
	} elsif ($ARGV[$i] eq "-p") {
		$upload{password} = $ARGV[++$i];
	} elsif ($ARGV[$i] eq "-d") {
		$upload{date_taken} = $ARGV[++$i];
	} elsif ($ARGV[$i] eq "-k") {
		$upload{keywords} = $ARGV[++$i];
	} elsif ($ARGV[$i] eq "-u") {
		$upload{username} = $ARGV[++$i];
	} elsif ($ARGV[$i] eq "-s") {
		$upload{descr} = $ARGV[++$i];
	} elsif ($ARGV[$i] eq "-t") {
		$test_flag = 1;
	} elsif ($ARGV[$i] =~ /jpg$/i) {
		push @files_to_up, $ARGV[$i];
	}
	$i++;
}

# die if we don't have certain things defined
# die without category
if(!$upload{category}) {
	die "You must specify an upload category!\n";
} elsif(!$upload{username}) {
	# die without a username
	die "You must specify a username!\n";
} elsif(!$upload{password}) {
	# die without a password
	die "You must specify a password\n";
} elsif($#files_to_up < 1) {
	die "No files to upload!\n";
}


# display the upload hash elements
print "uploading to servlet engine : $upload{servlet_url}\n";
print "using category : $upload{category}\n";
print "using date taken : $upload{date_taken}\n";
print "using username : $upload{username}\n";
print "using keywords : $upload{keywords}\n";
print "using description : $upload{descr}\n";

# init a useragent object
my $ua = new LWP::UserAgent;
# init a cookie object
my $jar = HTTP::Cookies->new;

# first we need to authenticate and get a cookie from the
# servlet engine.

# create our request
my $req = new HTTP::Request POST => $upload{servlet_url};
# specify our content type
$req->content_type('application/x-www-form-urlencoded');
# send our auth info. 
$req->content("func=setcred&username=$upload{username}&password=$upload{password}");

# pass the request
my $res = $ua->request($req);

# try an extract our cookie info
$jar->extract_cookies($res);

my $cookie_data =  $jar->as_string();

# make sure we can authenticate
if($cookie_data !~ /JSESSIONID/g) {
	die "Failed to Authenticate!\n";
}

# now we have to attempt to match up our categories with the
# internal category id.
# hack through the output and try to find the category id.
# this is REALLY ugly and highly dependent on the linefeeds
# that the servlet engine returns.  It should be made
# more robust.

# create our addform request
my $treq = new HTTP::Request POST => $upload{servlet_url};

# specify our content type
$treq->content_type('application/x-www-form-urlencoded');
# specify some more content. 
$treq->content("func=addform&xmlraw=1");
# stick our session cookie in the header. 
$jar->add_cookie_header($treq);
# perform the request
my $res = $ua->request($treq);

my $cat_line;
my @raw_output = split(/\n/, $res->content);
foreach my $line (@raw_output) {
	if($line =~ /value/g && $line =~ /$upload{category}/) {
		$cat_line = $line; 
	}
}

# quit if we don't find a matching category.
if($cat_line eq "") {
	die "Couldn't find a category that matched $upload{category}\n";    
}

# snag the cat_id
my ($blah, $cat_id, @blah) = split(/"/, $cat_line);

if(!$cat_id) {
	die "Couldn't find cat_id!\n";
}

# iterate through the files_to_up and upload each one.
foreach my $current (@files_to_up) {
	if(! -f $current) {
		print "Can't find $current\n";
	} else {
		if(!$test_flag) {
  			&upload_image($current);
		} else {
			print "Would upload $current\n"; 
		} 
	}
}

sub upload_image {
	my ($filename) = @_;

	# slurp up the image;
	my $image_data = `cat $filename`;

	my $add_req = POST(
		$upload{servlet_url},
		Content_Type => 'form-data',
		Content => [ func => "addimage",
			category => $cat_id,
			taken => $upload{date_taken},
			keywords => $upload{keywords},
			picture => [ undef, $filename,
				Content_Type => "image/jpeg",
				Content => $image_data,
			],
			info => $upload{descr},
		],
	);

	# try to add our session cookies to the header.
	$jar->add_cookie_header($add_req);

	# perform the request

	# print $add_req->as_string;

	my $res = $ua->request($add_req);

	# Check the outcome of the response
	if ($res->is_success) {
		if($res->content =~ /Succesful/) {
			my $size = length $image_data;
			print "Uploaded $filename : $size bytes\n";
			return 1;
		} else {
			print "Upload of $filename failed\n"; 
			return 0;
		}
	} else {
		print "Upload of $filename failed\n"; 
		return 0;
	}
}
