#!/usr/bin/env python
"""
Build a set of HTML files for all of the images dropped by
net.spy.photo.tools.MakeStaticSite.

Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
$Id: makesite.py,v 1.3 2003/12/02 04:55:36 dustin Exp $
"""

import os
import xml.dom.minidom

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
    os.mkdir("pages")
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
        f.write('<li><a href="%04d%02d.html">%02d%s</a></li>\n' % (y, m, m, s))
    f.write("</ul>")
    f.write('<div id="footer">')
    f.write("<a href=\"../index.html\">[Index]</a>")
    f.write("</div>")
    f.write("</body></html>\n")

def makeMonthPage(y, m, photos):
    f=file("pages/%04d%02d.html" % (y, m), "w")
    month="%04d%02d" % (y, m)
    f.write(header("Photos from %02d %04d" % (m, y))
        + """<link rel="stylesheet" href="../style.css" />
        </head>
        <body>
        <div class="monthList">\n""")
    for photo in photos:
        f.write('<a href="%s/%d.html">' % (month, photo.id))
        (yr,mn,dt)=photo.dateParts()
        f.write('<img alt="%d" src="../images/%04d%02d%02d/%d_t.%s"/></a>\n' \
                % (photo.id, yr, mn, dt, photo.id, "jpg"))
    f.write("</div>\n")
    f.write('<div id="footer">')
    f.write("<a href=\"../index.html\">[Index]</a>")
    f.write(' / <a href="%04d.html">[%04d]</a>' % (y, y))
    f.write("</div>")
    f.write("</body></html>\n")

def makePageForPhoto(dir, photo):
    f=file(dir + "/" + `photo.id` + ".html", "w")
    f.write(header("Image " + `photo.id`)
        + """<link rel="stylesheet" href="../../style.css" />
        </head>
        <body>\n<div class="idPage">\n""")
    f.write("""<div id="imgView">\n""")
    f.write("<a href=\"http://bleu.west.spy.net/photo/display.do?id=")
    f.write(`photo.id` + "\">")
    (y,m,d)=photo.dateParts()
    shortpath="%04d%02d%02d/%d.%s" % (y, m, d, photo.id, "jpg")
    f.write('<img alt="%d" src="../../images/%s"/>' % (photo.id, shortpath))
    f.write("</a>\n")

    f.write("<dl>")
    f.write("<dt>Taken</dt><dd>" + photo.taken + "</dd>\n")

    f.write("<dt>Keywords</dt><dd>")
    f.write(photo.keywords)
    f.write("</dd>\n")

    f.write("<dt>Description</dt><dd>")
    f.write(photo.descr)
    f.write("</dd>\n")

    f.write("<dt>Photo album link</dt><dd>")
    f.write("<a href=\"http://bleu.west.spy.net/photo/display.do?id=")
    f.write(`photo.id` + "\">")
    f.write("image " + `photo.id`)
    f.write("</a></dd>")
    f.write("</dl>")

    f.write("""</div>\n</div>\n""")

    # Navigation
    f.write('<div id="footer">')
    f.write("<a href=\"../../index.html\">[Index]</a>")
    f.write(' / <a href="../%04d.html">[%04d]</a>' % (y, y))
    f.write(' / <a href="../%04d%02d.html">[%02d]</a>' % (y, m, m))
    f.write("</div>")
    f.write("</body></html>\n")

class Photo(object):

    def __init__(self, el):
        for col in ['id', 'size', 'width', 'height', 'tnwidth', 'tnheight']:
            self.__dict__[col]=int(self.__getText(el, col))
        for col in ['addedby', 'taken', 'ts', 'keywords', 'descr']:
            self.__dict__[col]=self.__getText(el, col)

    def __getText(self, e, which):
        rv=""
        try:
            rv=e.getElementsByTagName(which)[0].childNodes[0].data
        except IndexError:
            pass
        return rv

    def dims(self):
        return "%dx%d" % (self.width, self.height)

    def dateParts(self):
        """Return the date parts (year, month, date) as integers"""
        return [int(x) for x in self.taken.split('-')]

    def __repr__(self):
        return "<Photo id=%d, dims=%s>" % (self.id, self.dims())

def parseIndex():
    album={}
    print "Parsing index..."
    d=xml.dom.minidom.parse("index.xml")
    for pic in d.getElementsByTagName("photo"):
        photo=Photo(pic)
        (year, month, day)=photo.dateParts()
        if not album.has_key(year):
            album[year]={}
        if not album[year].has_key(month):
            album[year][month]=[]
        album[year][month].append(photo)
    del d
    print "Parsed"
    return album

# Start

if __name__ == '__main__':

    idx=parseIndex()

    years=idx.keys()
    years.sort()

    makeIndex(idx, years)
    makeStylesheet()
    
    for y in years:
        makeYearPage(idx, y)

        for m in idx[y].keys():
            print "%04d/%02d ------" % (y, m)
            photos=idx[y][m]
            makeMonthPage(y, m, photos)
            dir="pages/%04d%02d" % (y, m)
            os.mkdir(dir)
            for photo in photos:
                makePageForPhoto(dir, photo)
