#!/usr/bin/env python
# arch-tag: B7A32A36-54B0-11D9-A41D-000A957659CC
"""

Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
"""

import sys
import libphoto

class KwHandler(libphoto.StaticIndexHandler):

    def __init__(self):
        libphoto.StaticIndexHandler.__init__(self)
        # Keyword to photo map
        self.kwmap={}
        # All photos
        self.photos=[]
    
    def gotPhoto(self, photo):
        self.photos.append(photo)
        # Map in the photo to the keyword
        for kwid in photo.keywords:
            kw=self.keywords[kwid]
            if self.kwmap.has_key(kw):
                self.kwmap[kw].append(photo.id)
            else:
                self.kwmap[kw] = [photo.id]

def kwcmp(a, b):
    rv = cmp(len(b[1]), len(a[1]))
    if rv == 0:
        rv = cmp(a[0], b[0])
    return rv

if __name__ == '__main__':
    kwh=KwHandler()
    libphoto.parseIndex("index.xml", kwh)
    # Sort all of the keywords
    for k in kwh.kwmap.items():
        kwh.kwmap[k[0]].sort()
    keywords=kwh.kwmap.items()
    keywords.sort(kwcmp)

    # OK, let's figure out which images we actually need
    photomapsrc={}
    photomap2={}
    for p in kwh.photos:
        photomapsrc[p.id]=p
    for k in kwh.kwmap.items():
        for id in k[1]:
            photomap2[id] = photomapsrc[id]
    del photomapsrc

    # Map out the keywords
    print "keywords=[" + ", ".join(['"' + k[0] + '"' for k in keywords]) + "];"
    i=0
    print "imgs = new Array();"
    for k in keywords:
        sys.stdout.write("imgs[%d] = [" % (i, ))
        sys.stdout.write(", ".join(map(str, k[1])) + "];\n")
        i = i + 1

    # Map out the locations
    print "photloc = new Array();"
    for p in photomap2.itervalues():
        (y, m, d) = p.dateParts()
        loc="%04d/%02d" % (y, m)
        print "photloc[%d]='%s';" % (p.id, loc)
    # print [(k[0], len(k[1])) for k in keywords]
    # print "Found %d keywords" % (len(keywords),)
