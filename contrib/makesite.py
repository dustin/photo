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
tp=None

class UsageError(exceptions.Exception):
    """Exception thrown for invalid usage"""
    pass

def mymkdir(path):
    if not os.path.isdir(path):
        os.mkdir(path)

def header(title):
    return """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html><head><title>""" + title + """</title>"""


def makeIndex(idx, years):
    f=file("index.html", "w")
    f.write(header("Photos") + 
        """<link rel="stylesheet" href="offline.css" />
        </head>
        <body>
        <a href="search.html">Search</a>
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
        + """<link rel="stylesheet" href="../offline.css" />
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
        + """<link rel="stylesheet" href="../../offline.css" />
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
    f.write("""<link rel="stylesheet" href="../../../offline.css" />
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
        {'id': photo.id,
            'shortpath': shortpath,
            'taken': photo.taken,
            'keywords': ' '.join(photo.keywords),
            'descr': photo.descr,
            'year': y,
            'month': m})

class MyHandler(libphoto.AlbumParser):
    """Sax handler for pulling out photo entries and putting them in a dict"""

    def __init__(self, album):
        libphoto.AlbumParser.__init__(self)
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
    f=libphoto.fetchIndex(config['baseurl'], config.get('-u'))
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
        global config
        if config.has_key("-i"):
            if not self.__fetchLocalImage():
                self.__fetchRemoteImage()
        else:
            self.__fetchRemoteImage()
        self.status.didOne()

    def __fetchLocalImage(self):
        rv = False
        srcpath = os.path.join(config['-i'], self.destpath)
        if os.path.exists(srcpath):
            shutil.copy(srcpath, self.destpath)
            rv = True
        else:
            status("No local image for %d at %s\n" % (self.photo.id, srcpath))
        return rv

    def __fetchRemoteImage(self):
        f = libphoto.fetchImage(self.baseurl, self.photo.id, self.size, self.tn)
        toWrite=open(self.destpath, "w")
        shutil.copyfileobj(f, toWrite)
        toWrite.close()
        f.close()

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
    global config
    photos=idx[y][m]
    makeMonthPage(y, m, photos)
    dir="pages/%04d/%02d" % (y, m)
    mymkdir(dir)
    i=0
    todo = []
    for photo in photos:
        i = i + 1
        makePageForPhoto(dir, photo)
        # If we're not ignoring images, go get them.
        if not config.has_key("-I"):
            todo.extend(fetchImage(photo, dir))
    st=FetchStatus(len(todo))
    for t in todo:
        tp.addTask(ImageFetcher(t[0], t[1], t[2], t[3], t[4], st))
    status("Need to fetch %d images" % (len(todo), ))
    # Wait for the count to get back down to zero
    if tp is not None:
        tp.waitForTaskCount()

def writeSearchIndex(idx):
    """Write out the search index"""
    f=open("searchdata.js", "w")
    allPhotos=[]
    for yd in idx.itervalues():
        for md in yd.itervalues():
            allPhotos.extend(md)
    kwmap={}
    # Index all of the keywords
    for photo in allPhotos:
        for kw in photo.keywords:
            if kwmap.has_key(kw):
                kwmap[kw].append(photo.id)
            else:
                kwmap[kw]=[photo.id]

    # Sorting function
    def kwcmp(a, b):
        rv = cmp(len(b[1]), len(a[1]))
        if rv == 0:
            rv = cmp(a[0], b[0])
        return rv

    # Sort all of the IDs in the keywords and the keywords
    for k in kwmap.items():
        kwmap[k[0]].sort()
    keywords=kwmap.items()
    keywords.sort(kwcmp)

    # OK, let's figure out which images we actually need
    photomapsrc={}
    photomap2={}
    for p in allPhotos:
        photomapsrc[p.id]=p
    for k in kwmap.items():
        for id in k[1]:
            photomap2[id] = photomapsrc[id]
    del photomapsrc

    # Map out the keywords
    f.write("keywords=[" + ", ".join(['"' + k[0] + '"' for k in keywords])
        + "];\n")
    i=0
    f.write("imgs = new Array();\n")
    for k in keywords:
        f.write("imgs[%d] = [" % (i, ))
        f.write(", ".join(map(str, k[1])) + "];\n")
        i = i + 1
    # Map out the locations
    f.write("photloc = new Array();\n")
    for p in photomap2.itervalues():
        (y, m, d) = p.dateParts()
        loc="%04d/%02d" % (y, m)
        f.write("photloc[%d]='%s';\n" % (p.id, loc))

    f.close()

def copyContrib(which):
    thePath=None
    for p in sys.path:
        tmp=os.path.join(p, which)
        if os.path.exists(tmp):
            thePath = tmp
            break
    if thePath is None:
        print "Could not find", which, "in", sys.path
        sys.exit(1)
    shutil.copy(thePath, which)

def writePages(idx):
    years=idx.keys()
    years.sort()

    makeIndex(idx, years)
    
    for y in years:
        makeYearPage(idx, y)

        mymkdir("pages/%04d" % (y, ))
        months=idx[y].keys()
        months.sort()
        for m in months:
            def pm():
                processMonth(idx, y, m)
            timefn(pm, "%04d/%02d" % (y, m))

    copyContrib("search.html")
    copyContrib("searchfun.js")
    copyContrib("prototype.js")
    copyContrib("offline.css")

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

    if not config.has_key('-p'):
        # Write out all of the pages and get images and stuff
        writePages(idx)

    # Write out the search index
    writeSearchIndex(idx)

def parseArgs():
    global config
    try:
        opts, args = getopt.getopt(sys.argv[1:], 'a:u:i:fFiIp')
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
        print "opts", opts
        print "args", args
        raise UsageError("Need photourl and destdir")

    if config.has_key('-a'):
        config['passwd']=getpass.getpass("Password for %s: " % config['-a'])

# Start

def usage():
    print "Usage:  %s [-fFip] [-a user] [-u user] [-i imgpath] " \
        "photurl destdir" % (sys.argv[0], )
    print " -f fetch index even if we already have one"
    print " -F fetch the full size images in addition to the smaller ones"
    print " -i allows you to specify an alt image path to fetch local copies"
    print "    (relative to destdir)"
    print " -a authenticate as the given user"
    print " -u get photo album for the specified user (must be authed as admin)"
    print " -I do not process images"
    print " -p do not process pages"
    sys.exit(1)

if __name__ == '__main__':

    try:
        # Parse the arguments
        parseArgs()
        try:
            # If we will be processing images, start a thread pool
            if not config.has_key("-I"):
                tp=threadpool.ThreadPool(num=10)
            go()
        finally:
            if tp is not None:
                print "Shutting down pool"
                tp.shutdown()
    except UsageError, e:
        print e
        usage()
