#!/usr/bin/env python
"""
Library of tools for handling the xml index from
net.spy.photo.tools.MakeStaticSite.

Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
"""

import os
import urllib2
import xml.sax
try:
    import cookielib
    cookieJar = cookielib.CookieJar()
    cookieProcessor=urllib2.HTTPCookieProcessor(cookieJar)
    openerFactory = urllib2.build_opener
except ImportError:
    import ClientCookie
    cookieJar = ClientCookie.MozillaCookieJar()
    cookieProcessor=ClientCookie.HTTPCookieProcessor(cookieJar)
    openerFactory = ClientCookie.build_opener

urlopener=openerFactory(cookieProcessor)
"""Abstracted URL opener that will handle cookies."""

class Photo(object):
    """Object representing an individual photo entry from the xml dump"""

    def __init__(self, d):
        for col in ['id', 'size', 'width', 'height', 'tnwidth', 'tnheight']:
            self.__dict__[col]=int(d[col])
        for col in ['addedby', 'taken', 'ts', 'keywords', 'descr', 'extension']:
            self.__dict__[col]=d[col]

    def dims(self):
        return "%dx%d" % (self.width, self.height)

    def dateParts(self):
        """Return the date parts (year, month, date) as integers"""
        return [int(x) for x in self.taken.split('-')]

    def __repr__(self):
        return "<Photo id=%d, dims=%s>" % (self.id, self.dims())

class StaticIndexHandler(xml.sax.handler.ContentHandler):
    """Sax handler for pulling out photo entries and putting them in a dict"""

    def __init__(self):
        xml.sax.handler.ContentHandler.__init__(self)
        self.current = None
        self.el = None

    def startElement(self, name, attrs):
        if name == 'photo':
            self.current = {}
        self.el=str(name)
        if self.current is not None:
            self.current[self.el]=""

    def endElement(self, name):
        if name == 'photo':
            # Finished a photo, store it
            photo=Photo(self.current)
            self.gotPhoto(Photo(self.current))

            # Reset the current entry
            self.current = None

    def characters(self, content):
        if self.current is not None:
            self.current[self.el] = self.current[self.el] + content.strip()

    def gotPhoto(self, photo):
        raise NotImplemented

def parseIndex(path, handler):
    """Parse the index at the given path."""
    d=xml.sax.parse(path, handler)

def fetchIndex(baseurl):
    """Fetch the export index, return a reader"""
    if baseurl[-1] != '/':
        baseurl = baseurl + "/"
    url=baseurl + "export"
    print "Fetching index from", url
    req=urllib2.Request(baseurl + "export")
    rv=urlopener.open(req)
    return rv

def fetchImage(baseurl, id, size=None, thumbnail=False):
    """Fetch an image"""

    imgurl="%sPhotoServlet?id=%d" % (baseurl, id)
    if size is not None:
        imgurl = imgurl + "&scale=" + size
    elif thumbnail:
        imgurl = imgurl + "&thumbnail=1"

    req=urllib2.Request(imgurl)
    rv=urlopener.open(req)
    return rv
