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
import getopt
import getpass
import libphoto
import threading
import threadpool
import exceptions

# Global config
config={}
tp=threadpool.ThreadPool(num=10)

class UsageError(exceptions.Exception):
    """Exception thrown for invalid usage"""
    pass

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
        f.write('<li><a href="%04d/%02d.html">%02d%s</a></li>\n' \
            % (year, m, m, s))
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

def fetchIndex():
    global config
    print "Beginning index fetch"
    destdir=config['destdir']
    f=libphoto.fetchIndex(config['baseurl'])
    fout=open("index.xml", "w")
    shutil.copyfileobj(f, fout)
    f.close()
    fout.close()

def parseIndex():
    global config
    album={}
    print "Beginning index parse"
    d=libphoto.parseIndex('index.xml', MyHandler(album))
    return album

def status(str):
    """Display a status string"""
    # Clear the current line
    sys.stdout.write(" " * 79)
    # sys.stdout.write("\r" + chr(27) + "K")
    # sys.stdout.flush()
    sys.stdout.write("\r" + str + "\r")
    sys.stdout.flush()

def timefn(f, what, eol='\n'):
    """Time the execution of a function and display the results to the status"""
    tstart=posix.times()
    start=time.time()
    rv=f()
    tend=posix.times()
    end=time.time()
    ut=tend[0] - tstart[0]
    st=tend[1] - tstart[1]
    status("Finished %s in %.2fr/%.2fu/%.2fs%s" \
        % (what, end-start, ut, st, eol))
    return rv

class ImageFetcher(threadpool.Job):
    def __init__(self, baseurl, photo, destpath, size, tn, status):
        self.baseurl=baseurl
        self.photo=photo
        self.destpath=destpath
        self.size=size
        self.tn=tn
        self.status=status

    def run(self):
        f = libphoto.fetchImage(self.baseurl, self.photo.id, self.size, self.tn)
        toWrite=open(self.destpath, "w")
        shutil.copyfileobj(f, toWrite)
        toWrite.close()
        f.close()
        self.status.didOne()

class FetchStatus(object):
    def __init__(self, todo):
        self.done=0
        self.todo=todo
        self.mutex=threading.Lock()

    def didOne(self):
        try:
            self.mutex.acquire()
            self.done += 1
            status("Completed %d of %d " % (self.done, self.todo))
        finally:
            self.mutex.release()

def fetchImage(photo, destdir):
    global config
    global tp

    imgs=[
        ('normal', ("800x600", False)),
        ('tn', (None, True))]
    if config.has_key('-F'):
        imgs.append( ('full', (None, False)) )

    todo=[]
    for ext, p in imgs:
        # print "Fetching %s for %s" % (url, ext)
        destfn="%s/%d_%s.%s" % (destdir, photo.id, ext, photo.extension)
        if not os.path.exists(destfn):
            todo.append((config['baseurl'], photo, destfn, p[0], p[1]))
    return todo

def processMonth(idx, y, m):
    photos=idx[y][m]
    makeMonthPage(y, m, photos)
    dir="pages/%04d/%02d" % (y, m)
    mymkdir(dir)
    i=0
    todo = []
    for photo in photos:
        i = i + 1
        makePageForPhoto(dir, photo)
        todo.extend(fetchImage(photo, dir))
    st=FetchStatus(len(todo))
    for t in todo:
        tp.addTask(ImageFetcher(t[0], t[1], t[2], t[3], t[4], st))
    status("Need to fetch %d images" % (len(todo), ))
    # Wait for the count to get back down to zero
    tp.waitForTaskCount()

def go():

    global config

    # Set up the destination directory
    mymkdir(config['destdir'])
    os.chdir(config['destdir'])

    # Authenticate if we should
    if config.has_key('-a'):
        libphoto.authenticate(config['baseurl'],
            config['-a'], config['passwd'])

    if os.path.exists("index.xml"):
        if config.has_key('-f'):
            timefn(fetchIndex, "index fetch")
    else:
        timefn(fetchIndex, "index fetch")
    idx=timefn(parseIndex, "index parse")

    years=idx.keys()
    years.sort()

    makeIndex(idx, years)
    makeStylesheet()
    
    for y in years:
        makeYearPage(idx, y)

        mymkdir("pages/%04d" % (y, ))
        months=idx[y].keys()
        months.sort()
        for m in months:
            def pm():
                processMonth(idx, y, m)
            timefn(pm, "%04d/%02d" % (y, m))

def parseArgs():
    global config
    try:
        opts, args = getopt.getopt(sys.argv[1:], 'vfFa:')
        config = dict(opts)
    except getopt.GetoptError, e:
        raise UsageError(e)

    try:
        photourl, destdir = args
        if photourl[-1] != '/':
            photourl = photourl + '/'
        config['baseurl']=photourl
        config['destdir']=destdir
    except ValueError, e:
        raise UsageError("Need photourl and destdir")

    if config.has_key('-a'):
        config['passwd']=getpass.getpass("Password for %s: " % config['-a'])

# Start

def usage():
    print "Usage:  %s [-f] [-F] [-a user] photurl destdir" % (sys.argv[0], );
    print " -a authenticate as the given user"
    print " -f fetch index even if we already have one"
    print " -F fetch the full size images in addition to the smaller ones"
    sys.exit(1)

if __name__ == '__main__':

    try:
        # Parse the arguments
        try:
            parseArgs()
            go()
        finally:
            tp.shutdown()
    except UsageError, e:
        print e
        usage()
