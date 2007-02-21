#!/usr/bin/env python
"""
Library of tools for handling the xml index from
net.spy.photo.tools.MakeStaticSite.

Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
"""

import os
import sets
import urllib
import urllib2
import xml.sax
import unittest
import exceptions

import saxkit

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

class Photo(saxkit.ElementHandler):
    """Object representing an individual photo entry from the xml dump"""

    def __init__(self):
        saxkit.ElementHandler.__init__(self)
        def r(n):
            self.parsers[(None, n)]=saxkit.SimpleValueParser()
        self.ints=['id', 'size', 'width', 'height', 'tnwidth', 'tnheight']
        self.strings=['md5', 'cat', 'addedby', 'taken', 'ts', 'descr',
            'extension']

        for t in self.ints + self.strings:
            r(t)
        self.parsers[(None, 'keywords')]=KeywordParser()
        self.parsers[(None, 'annotations')]=saxkit.SimpleListParser(Annotation)
        self.annotations=[]

    def addChild(self, name, val):
        if isinstance(val, saxkit.SimpleValueParser):
            if name[1] in self.ints:
                self.__dict__[name[1]]=int(val.getValue())
            else:
                self.__dict__[name[1]]=val.getValue()
        elif name == (None, 'keywords'):
            self.keywords=sets.ImmutableSet(val.keywords)
        elif name == (None, 'annotations'):
            self.annotations=val.getValues()
        else:
            self.__dict__[name[1]]=val

    def dims(self):
        return "%dx%d" % (self.width, self.height)

    def dateParts(self):
        """Return the date parts (year, month, date) as integers"""
        return [int(x) for x in self.taken.split('-')]

    def __repr__(self):
        return "<Photo id=%d, dims=%s, kws=%s, annotations=%s>" \
            % (self.id, self.dims(), repr(self.keywords),
            repr(self.annotations))

class KeywordParser(saxkit.ElementHandler):

    def __init__(self):
        saxkit.ElementHandler.__init__(self)
        self.keywords=[]

    def addChild(self, name, val):
        self.keywords.append(val[(None, 'word')])

    def getParser(self, name):
        return saxkit.SimpleValueParser()

class Annotation(saxkit.ElementHandler):

    def __init__(self):
        saxkit.ElementHandler.__init__(self)

        def r(n):
            self.parsers[(None, n)]=saxkit.SimpleValueParser()

        self.ints = ['x', 'y', 'width', 'height']
        strings = ['title', 'addedby', 'ts']

        for k in self.ints + strings:
            r(k)

        self.parsers[(None, 'keywords')]=KeywordParser()

    def addChild(self, name, val):
        if isinstance(val, saxkit.SimpleValueParser):
            if name[1] in self.ints:
                self.__dict__[name[1]]=int(val.getValue())
            else:
                self.__dict__[name[1]]=val.getValue()
        elif name == (None, 'keywords'):
            self.keywords=sets.ImmutableSet(val.keywords)
        else:
            self.__dict__[name[1]]=val

    def __repr__(self):
        return "<Annotation %dx%d @ %d,%d title=%s>" \
            % (self.width, self.height, self.x, self.y, `self.title`)

class AlbumParser(saxkit.ElementHandler):
    """AlbumParser receives Photo instances when they're parsed."""
    def getParser(self, name):
        assert name == (None, 'photo')
        return Photo()

    def addChild(self, name, val):
        assert name == (None, 'photo')
        self.gotPhoto(val)

    def gotPhoto(self, photo):
        """Invoked whenever the AlbumParser finished parsing a photo."""
        raise exceptions.NotImplementedError()

class ExportParser(saxkit.ElementHandler):

    def __init__(self, albumParser=AlbumParser()):
        saxkit.ElementHandler.__init__(self)
        self.albumParser=albumParser

    def getParser(self, name):
        assert name == (None, 'album')
        return self.albumParser

def parseIndex(path, handler=AlbumParser()):
    """Parse the index at the given path."""

    baseh=saxkit.StackedHandler((None, u'photoexport'), ExportParser(handler))
    parser=xml.sax.make_parser()
    parser.setFeature(xml.sax.handler.feature_namespaces, True)
    parser.setContentHandler(baseh)
    d=parser.parse(path)

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

class ParseTest(unittest.TestCase):

    def setUp(self):
        class P(AlbumParser):
            photos={}
            def gotPhoto(self, p):
                self.photos[p.id]=p

        p=P()
        parseIndex("exporttest.xml", p)
        self.photos=p.photos

    def testCount(self):
        self.assertEquals(len(self.photos), 12)

    def testAddedBy(self):
        for v in self.photos.values():
            self.assertEquals(v.addedby, "demouser")

    def testCats(self):
        self.assertEquals(self.photos[1].cat, "Public")
        self.assertEquals(self.photos[2].cat, "Private")

    def testKeywords(self):
        expected=sets.ImmutableSet(['license', 'mom', 'plate', 'your'])
        self.assertEquals(self.photos[1].keywords, expected)

        expected=sets.ImmutableSet(['dustin', 'christmas'])
        self.assertEquals(self.photos[2].keywords, expected)

    def testAnnotations(self):
        self.assertEquals(len(self.photos[1].annotations), 2)
        self.assertEquals(len(self.photos[2].annotations), 0)

        for a in self.photos[1].annotations:
            if a.x == 762:
                expected=sets.ImmutableSet(['license', 'mom', 'plate', 'your'])
                self.assertEquals(a.keywords, expected)
                self.assertEquals(a.y, 462)
                self.assertEquals(a.addedby, 'demouser')
                self.assertEquals(a.ts, '2005-05-31T00:47:21')
                self.assertEquals(a.title, 'We were behind your mom in SF.')
            elif a.x == 90:
                expected=sets.ImmutableSet(['license', 'plate'])
                self.assertEquals(a.keywords, expected)
                self.assertEquals(a.y, 478)
                self.assertEquals(a.addedby, 'demouser')
                self.assertEquals(a.ts, '2005-05-31T00:47:52')
                self.assertEquals(a.title, 'This plate is boring.')
            else:
                self.fail("Unexpected x in annotation:  " + a.x)

if __name__ == '__main__':
    unittest.main()
