#!/usr/bin/env python
"""
Library of tools for handling the xml index from
net.spy.photo.tools.MakeStaticSite.

Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
"""

import os
import urllib
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
            try:
                self.__dict__[col]=int(d[col])
            except ValueError, e:
                print "Problem parsing", col, "from", d
                raise e
        for col in ['addedby', 'taken', 'ts', 'keywords', 'descr', 'extension']:
            self.__dict__[col]=d[col]

    def mapKeywords(self, d):
        self.keywordStrings=[d[k] for k in self.keywords]

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
        self.lastwasspace = False
        self.el = None
        self.keywords={}

    def startElement(self, name, attrs):
        if name == 'photo':
            self.current = {}
        elif name == 'keywords':
            self.current['keywords']=[]
        elif name == 'keyword':
            if attrs.has_key("id"):
                self.keywords[int(attrs['id'])] = attrs['word']
            else:
                self.current['keywords'].append(int(attrs['kwid']))
        else:
            self.el=str(name)
        if self.current is not None and self.el is not None:
            self.current[self.el]=None

    def endElement(self, name):
        if name == 'photo':
            # Finished a photo, store it
            photo=Photo(self.current)
            photo.mapKeywords(self.keywords)
            self.gotPhoto(photo)

            # Reset the current entry
            self.current = None
            self.el = None

    def characters(self, content):
        if content[-1] == ' ':
            self.lastwasspace=True
        else:
            self.lastwasspace=False
        if self.el is not None and self.current is not None:
            # Grab the content.  If this looks a little weird, it's because of
            # how I deal with line wrapping and stuff.
            if self.current[self.el] is None:
                self.current[self.el]=content.strip()
            else:
                x = content.strip()
                if x != '':
                    pad = ""
                    # If there's whitespace on either end of the incoming
                    # content, or the previous content ended in a space,
                    # include the pad.
                    if content[0]==' ' or content[-1]==' ' or self.lastwasspace:
                        pad=' '
                    self.current[self.el] += pad + x

    def gotPhoto(self, photo):
        raise NotImplemented

def parseIndex(path, handler):
    """Parse the index at the given path."""
    d=xml.sax.parse(path, handler)

def authenticate(baseurl, username, password):
    if baseurl[-1] != '/':
        baseurl = baseurl + "/"
    req=urllib2.Request(baseurl + "login.do",
        urllib.urlencode({'username': username, 'password': password}))
    res=urlopener.open(req)
    res.read()
    # XXX:  Need to validate the authentication was successful
    res.close()

def fetchIndex(baseurl, foruser=None):
    """Fetch the export index, return a reader"""
    if baseurl[-1] != '/':
        baseurl = baseurl + "/"
    url=baseurl + "export"
    print "Fetching index from", url
    req=urllib2.Request(baseurl + "export")
    # If there's a user specified, request it
    if(foruser is not None):
        d={'user': foruser}
        req=urllib2.Request(baseurl + "export", urllib.urlencode(d))
    rv=urlopener.open(req)
    return rv

def fetchImage(baseurl, id, size=None, thumbnail=False):
    """Fetch an image"""
    if baseurl[-1] != '/':
        baseurl = baseurl + "/"

    imgurl="%sPhotoServlet?id=%d" % (baseurl, id)
    if size is not None:
        imgurl = imgurl + "&scale=" + size
    elif thumbnail:
        imgurl = imgurl + "&thumbnail=1"

    req=urllib2.Request(imgurl)
    rv=urlopener.open(req)
    return rv
