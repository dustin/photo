#!/usr/bin/env python
"""
Build a set of HTML files for all of the images dropped by
net.spy.photo.tools.MakeStaticSite.

Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
"""

import os
import sys
import time
import posix
import shutil
import urllib2
import libphoto

def mymkdir(path):
    if not os.path.isdir(path):
        os.mkdir(path)

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


def makeIndex(idx, years):
    f=file("index.html", "w")
    f.write(header("Photos") + 
        """<link rel="stylesheet" href="style.css" />
        </head>
        <body>
        <h1>Years</h1>
        <ul>\n""")
    mymkdir("pages")
    for y in years:
        n=0
        for d in idx[y]:
            n = n + len(idx[y][d])
        if n == 1:
            s = " (1 image)"
        else:
            s = " (" + `n` + " images)"
        f.write('<li><a href="pages/%04d.html">%d%s</a></li>\n' % (y, y, s))
    f.write("</ul></body></html>\n")
    f.close()

def makeYearPage(idx, year):
    f=file("pages/%04d.html" % (year,), "w")
    f.write(header("Photos from " + `year`)
        + """<link rel="stylesheet" href="../style.css" />
        </head>
        <body>
        <h1>Months: in """ + `year` + """</h1><ul>\n""")
    months=idx[year].keys()
    months.sort()
    for m in months:
        nimgs=len(idx[year][m])
        if nimgs == 1:
            s=" (1 image)"
        else:
            s=" (" + `nimgs` + " images)"
        f.write('<li><a href="%04d/%02d.html">%02d%s</a></li>\n' % (y, m, m, s))
    f.write("</ul>")
    f.write('<div id="footer">')
    f.write('<a href="../index.html">[Index]</a>')
    f.write("</div>")
    f.write("</body></html>\n")

def makeMonthPage(y, m, photos):
    f=file("pages/%04d/%02d.html" % (y, m), "w")
    f.write(header("Photos from %02d %04d" % (m, y))
        + """<link rel="stylesheet" href="../../style.css" />
        </head>
        <body>
        <div class="monthList">\n""")
    for photo in photos:
        f.write('<a href="%02d/%d.html">' % (m, photo.id))
        (yr,mn,dt)=photo.dateParts()
        f.write('<img alt="%d" src="%02d/%d_tn.%s"/></a>\n' \
                % (photo.id, m, photo.id, photo.extension))
    f.write("</div>\n")
    f.write('<div id="footer">')
    f.write('<a href="../../index.html">[Index]</a>')
    f.write(' / <a href="../%04d.html">[%04d]</a>' % (y, y))
    f.write("</div>")
    f.write("</body></html>\n")

def makePageForPhoto(dir, photo):
    (y,m,d)=photo.dateParts()
    shortpath="%d_normal.%s" % (photo.id, photo.extension)

    f=file(dir + "/" + `photo.id` + ".html", "w")

    f.write(header("Image " + `photo.id`))
    f.write("""<link rel="stylesheet" href="../../../style.css" />
</head>
 <body>
 <div class="idPage">
 <div id="imgView">
 <a href="http://bleu.west.spy.net/photo/display.do?id=%(id)d">
 <img alt="Image %(id)d" src="%(shortpath)s"/>
 </a>
 <dl>
    <dt>Taken</dt><dd>%(taken)s</dd>
    <dt>Keywords</dt><dd>%(keywords)s</dd>
    <dt>Description</dt><dd>%(descr)s</dd>
    <dt>Photo Album Link</dt>
    <dd><a href="http://bleu.west.spy.net/photo/display.do?id=%(id)d">
        Image %(id)d</a>
    </dd>
 </dl>
 </div>
 </div>

 <div id="footer">
    <a href="../../index.html">[Index]</a>
    / <a href="../%(year)04d.html">[%(year)04d]</a>
    / <a href="../%(year)04d/%(month)02d.html">[%(month)02d]</a>
 </div>
 </body></html>""" % \
        {'id': photo.id, 'shortpath': shortpath, 'taken': photo.taken,
            'keywords': photo.keywords, 'descr': photo.descr,
            'year': y, 'month': m})

class MyHandler(libphoto.StaticIndexHandler):
    """Sax handler for pulling out photo entries and putting them in a dict"""

    def __init__(self, album):
        libphoto.StaticIndexHandler.__init__(self)
        self.album=album

    def gotPhoto(self, photo):
        (year, month, day)=photo.dateParts()
        if not self.album.has_key(year):
            self.album[year]={}
        if not self.album[year].has_key(month):
            self.album[year][month]=[]
        self.album[year][month].append(photo)

def parseIndex(srcurl):
    if srcurl[-1] != '/':
        srcurl = srcurl + '/'

    album={}
    print "Parsing index..."
    start=time.time()
    f=urllib2.urlopen(srcurl + "export")
    d=libphoto.parseIndex(f, MyHandler(album))
    f.close()
    end=time.time()
    times=posix.times()
    print "Parsed in %.2fr/%.2fu/%.2fs" % (end-start, times[0], times[1])
    return album

def status(str):
    sys.stdout.write(" " * 60)
    sys.stdout.write("\r" + str + "\r")

def fetchImage(srcurl, photo, destdir):
    if srcurl[-1] != '/':
        srcurl = srcurl + '/'

    baseurl="%sPhotoServlet?id=%d" % (srcurl, photo.id)
    normal=baseurl + "&scale=800x600"
    thumbnail=baseurl + "&thumbnail=1"

    for ext,url in (('full', baseurl), ('normal', normal), ('tn', thumbnail)):
        # print "Fetching %s for %s" % (url, ext)
        destfn="%s/%d_%s.%s" % (destdir, photo.id, ext, photo.extension)
        if os.path.exists(destfn):
            status("Already have %s" % destfn)
        else:
            f = urllib2.urlopen(url)
            toWrite=open(destfn, "w")
            shutil.copyfileobj(f, toWrite)
            toWrite.close()
            f.close()

# Start

def usage():
    sys.stderr.write("Usage:  %s photurl [destdir]\n");
    sys.exit(1)

if __name__ == '__main__':

    if sys.argv < 2:
        usage()

    baseurl=sys.argv[1]

    if sys.argv > 2:
        destdir=sys.argv[2]
        mymkdir(destdir)
        os.chdir(destdir)

    idx=parseIndex(baseurl)

    years=idx.keys()
    years.sort()

    makeIndex(idx, years)
    makeStylesheet()
    
    times=posix.times()
    t=time.time()
    for y in years:
        makeYearPage(idx, y)

        mymkdir("pages/%04d" % (y, ))
        for m in idx[y].keys():
            photos=idx[y][m]
            makeMonthPage(y, m, photos)
            dir="pages/%04d/%02d" % (y, m)
            mymkdir(dir)
            for photo in photos:
                makePageForPhoto(dir, photo)
                fetchImage(baseurl, photo, dir)

            newtimes=posix.times()
            ut=newtimes[0] - times[0]
            st=newtimes[1] - times[1]
            newtime=time.time()
            rt=newtime-t
            status("Processed %04d/%02d in %.2fr/%.2fu/%.2fs%s\n" \
                % (y, m, rt, ut, st, ' ' * 20))
            times=newtimes
            t=newtime
