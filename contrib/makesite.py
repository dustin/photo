#!/usr/bin/env python
"""
Build a set of HTML files for all of the images dropped by
net.spy.photo.tools.MakeStaticSite.

Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
$Id: makesite.py,v 1.3 2003/12/02 04:55:36 dustin Exp $
"""

import os

def extractYears(dates):
	rvs={}
	for d in dates:
		rvs[d[0:4]]=1
	l=rvs.keys()
	l.sort()
	return(l)

def extractMonths(dates):
	rvs={}
	for d in dates:
		rvs[d[0:6]]=1
	l=rvs.keys()
	l.sort()
	return(l)

def getMonthsForYear(year, months):
	rv=[]
	for i in months:
		if i.startswith(year):
			rv.append(i)
	return(rv)

def getIDs(month, dates):
	dirs=[]
	for i in dates:
		if i.startswith(month):
			dirs.append(i)
	images=[]
	for d in dirs:
		thedir="images/" + d;
		files=os.listdir(thedir)
		for f in files:
			if f.find("_t") > 0:
				offset=f.find("_t")
				id=f[0:offset]
				e=f[offset+2:]
				images.append( (d + "/" + id, e))
	return(images)

def countImages(year, dates):

	rv=0
	for m in getMonthsForYear(year, months):
		ids=getIDs(m, dates)
		rv = rv + len(ids)
	return rv

def makeStylesheet():
	f=file("style.css", "w")
	f.write("""
body,td,th {
    font-family: Arial, Helvetica, sans-serif;
    background: white;
    color: black;
}

#imgView {
    text-align: center;
}

#imgView dl {
    text-align: left;
    width: 66%;
}

#footer {
    border-top: solid 1px;
    width: 50%;
    font-size: smaller;
}

img {
	border: none;
}
	""")
	f.close()

def header(title):
	return """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html><head><title>""" + title + """</title>"""


def makeIndex(years, dates):
	f=file("index.html", "w")
	f.write(header("Photos") + 
		"""<link rel="stylesheet" href="style.css" />
		</head>
		<body>
		<h1>Years</h1>
		<ul>\n""")
	os.mkdir("pages")
	for y in years:
		n=countImages(y, dates)
		if n == 1:
			s = " (1 image)"
		else:
			s = " (" + `n` + " images)"
		f.write("<li><a href=\"pages/" + y + ".html\">" + y + s + "</a></li>\n")
	f.write("</ul></body></html>\n")
	f.close()

def makeYearPage(year, dates, months):
	f=file("pages/" + year + ".html", "w")
	f.write(header("Photos from " + year)
		+ """<link rel="stylesheet" href="../style.css" />
		</head>
		<body>
		<h1>Months: in """ + year + """</h1><ul>\n""")
	for m in months:
		ids=getIDs(m, dates)
		nimgs=len(ids)
		if nimgs == 1:
			s=" (1 image)"
		else:
			s=" (" + `nimgs` + " images)"
		f.write("<li><a href=\"" + m + ".html\">" + m[4:] + s + "</a></li>\n")
	f.write("</ul>")
	f.write('<div id="footer">')
	f.write("<a href=\"../index.html\">[Index]</a>")
	f.write("</div>")
	f.write("</body></html>\n")

def makeMonthPage(month, ids):
	f=file("pages/" + month + ".html", "w")
	y=month[0:4]
	m=month[4:]
	f.write(header("Photos from " + m + " " + y)
		+ """<link rel="stylesheet" href="../style.css" />
		</head>
		<body>
		<div class="monthList">\n""")
	for stuff in ids:
		i=stuff[0]
		e=stuff[1]
		theido=i.rindex("/")
		theid=i[theido+1:]
		f.write("<a href=\"" + month + "/" + theid + ".html\">"
			+ "<img alt=\"" + theid + "\" src=\"../images/"
				+ i + "_t" + e + "\"" + " /></a>\n")
	f.write("</div>\n")
	f.write('<div id="footer">')
	f.write("<a href=\"../index.html\">[Index]</a>")
	f.write(" / <a href=\"" + y + ".html\">[" + y + "]</a>")
	f.write("</div>")
	f.write("</body></html>\n")

def makePageForId(month, stuff):
	id=stuff[0]
	e=stuff[1]
	theido=id.rindex("/")
	theid=id[theido+1:]
	f=file("pages/" + month + "/" + theid + ".html", "w")
	f.write(header("Image " + theid)
		+ """<link rel="stylesheet" href="../../style.css" />
		</head>
		<body>\n<div class="idPage">\n""")
	f.write("""<div id="imgView">\n""")
	f.write("<a href=\"http://bleu.west.spy.net/photo/display.do?id=")
	f.write(theid + "\">")
	f.write("<img alt=\"" + theid + "\" src=\"../../images/" + id + e + "\" />")
	f.write("</a>\n")

	takents=id[0:id.index("/")]
	taken=takents[0:4] + "/" + takents[4:6] + "/" + takents[6:]

	f.write("<dl>")
	f.write("<dt>Taken</dt><dd>" + taken + "</dd>\n")

	f.write("<dt>Keywords</dt><dd>")
	kf=file("images/" + id + "_k.txt")
	for l in kf.readlines():
		f.write(l)
	kf.close()
	f.write("</dd>\n")

	f.write("<dt>Description</dt><dd>")
	kf=file("images/" + id + "_d.txt")
	for l in kf.readlines():
		f.write(l)
	kf.close()
	f.write("</dd>\n")

	f.write("<dt>Photo album link</dt><dd>")
	f.write("<a href=\"http://bleu.west.spy.net/photo/display.do?id=")
	f.write(theid + "\">")
	f.write("image " + theid)
	f.write("</a></dd>")
	f.write("</dl>")

	f.write("""</div>\n</div>\n""")

	# Navigation
	y=takents[0:4]
	m=takents[4:6]
	f.write('<div id="footer">')
	f.write("<a href=\"../../index.html\">[Index]</a>")
	f.write(" / <a href=\"../" + y + ".html\">[" + y + "]</a>")
	f.write(" / <a href=\"../" + y + m + ".html\">[" + m + "]</a>")
	f.write("</div>")
	f.write("</body></html>\n")

# Start

dates=os.listdir("images")
years=extractYears(dates)
months=extractMonths(dates)

makeIndex(years, dates)
makeStylesheet()

for y in years:
	mfy=getMonthsForYear(y, months)
	print y + " contains " + `mfy`
	makeYearPage(y, dates, mfy)

for m in months:
	print m + " ------"
	ids=getIDs(m, dates)
	makeMonthPage(m, ids)
	os.mkdir("pages/" + m)
	for id in ids:
		makePageForId(m, id)
