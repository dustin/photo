#!/usr/bin/env python
"""
Build a set of HTML files for all of the images dropped by
net.spy.photo.tools.MakeStaticSite.

Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
$Id: makesite.py,v 1.1 2003/06/02 00:40:32 dustin Exp $
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

def makeIndex(years, dates):
	f=file("index.html", "w")
	f.write("""<html><head><title>Photos</title>
		<body bgcolor="#fFfFfF">
		Years:
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

def makeYearPage(year, dates, months):
	f=file("pages/" + year + ".html", "w")
	f.write("""<html><head><title>Photos from """ + year + """</title>
		<body bgcolor="#fFfFfF">
		Months: in """ + year + """<ul>\n""")
	for m in months:
		ids=getIDs(m, dates)
		nimgs=len(ids)
		if nimgs == 1:
			s=" (1 image)"
		else:
			s=" (" + `nimgs` + " images)"
		f.write("<li><a href=\"" + m + ".html\">" + m[4:] + s + "</a></li>\n")
	f.write("</ul>")
	f.write("<p>")
	f.write("<a href=\"../index.html\">[Index]</a>")
	f.write("</p>")
	f.write("</body></html>\n")

def makeMonthPage(month, ids):
	f=file("pages/" + month + ".html", "w")
	y=month[0:4]
	m=month[4:]
	f.write("""<html><head><title>Photos from """ + m + " " + y + """</title>
		<body bgcolor="#fFfFfF">\n""")
	for stuff in ids:
		i=stuff[0]
		e=stuff[1]
		theido=i.rindex("/")
		theid=i[theido+1:]
		f.write("<a href=\"" + month + "/" + theid + ".html\">"
			+ "<img border=\"0\" src=\"../images/"
				+ i + "_t" + e + "\"" + "></a>\n")
	f.write("</ul>")
	f.write("<p>")
	f.write("<a href=\"../index.html\">[Index]</a>")
	f.write(" / <a href=\"" + y + ".html\">[" + y + "]</a>")
	f.write("</p>")
	f.write("</body></html>\n")

def makePageForId(month, stuff):
	id=stuff[0]
	e=stuff[1]
	theido=id.rindex("/")
	theid=id[theido+1:]
	f=file("pages/" + month + "/" + theid + ".html", "w")
	f.write("""<html><head><title>Image """ + theid + """</title>
		<body bgcolor="#fFfFfF">\n""")
	f.write("""<div align="center">\n""")
	f.write("<a href=\"http://bleu.west.spy.net/photo/display.do?id=")
	f.write(theid + "\">")
	f.write("<img border=\"0\" src=\"../../images/" + id + e + "\">")
	f.write("</a>")

	takents=id[0:id.index("/")]
	taken=takents[0:4] + "/" + takents[4:6] + "/" + takents[6:]

	f.write("<p>Taken: " + taken + "</p>\n")

	f.write("<p>Keywords: ")
	kf=file("images/" + id + "_k.txt")
	for l in kf.readlines():
		f.write(l)
	kf.close()
	f.write("</p>\n")

	f.write("<p>Description: ")
	kf=file("images/" + id + "_d.txt")
	for l in kf.readlines():
		f.write(l)
	kf.close()
	f.write("</p>\n")

	f.write("<p>Photo album link:  ")
	f.write("<a href=\"http://bleu.west.spy.net/photo/display.do?id=")
	f.write(theid + "\">")
	f.write("image " + theid)
	f.write("</a></p>")

	f.write("""</div>\n""")

	# Navigation
	y=takents[0:4]
	m=takents[4:6]
	f.write("<p>")
	f.write("<a href=\"../../index.html\">[Index]</a>")
	f.write(" / <a href=\"../" + y + ".html\">[" + y + "]</a>")
	f.write(" / <a href=\"../" + y + m + ".html\">[" + m + "]</a>")
	f.write("</p>")
	f.write("</body></html>\n")

# Start

dates=os.listdir("images")
years=extractYears(dates)
months=extractMonths(dates)

makeIndex(years, dates)

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
