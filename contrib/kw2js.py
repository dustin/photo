#!/usr/bin/env python
"""

Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
"""
# arch-tag: B7A32A36-54B0-11D9-A41D-000A957659CC

import sys
import libphoto

class KwHandler(libphoto.StaticIndexHandler):

    def __init__(self):
        libphoto.StaticIndexHandler.__init__(self)
        self.keywords={}
        self.photos=[]
    
    def gotPhoto(self, photo):
        self.photos.append(photo)
        for kw in str(photo.keywords).split():
            if self.keywords.has_key(kw):
                self.keywords[kw].append(photo.id)
            else:
                self.keywords[kw] = [photo.id]

def kwcmp(a, b):
    rv = cmp(len(b[1]), len(a[1]))
    if rv == 0:
        rv = cmp(a[0], b[0])
    return rv

if __name__ == '__main__':
    kwh=KwHandler()
    libphoto.parseIndex("index.xml", kwh)
    for k in kwh.keywords.items():
        if len(k[1]) == 1:
            del kwh.keywords[k[0]]
    keywords=kwh.keywords.items()
    keywords.sort(kwcmp)

    print "keywords=[" + ", ".join(['"' + k[0] + '"' for k in keywords]) + "];"
    i=0
    print "imgs = new Array();"
    for k in keywords:
        sys.stdout.write("imgs[%d] = [" % (i, ))
        sys.stdout.write(", ".join(map(str, k[1])) + "];\n")
        i = i + 1

    print "photloc = new Array();"
    for p in kwh.photos:
        (y, m, d) = p.dateParts()
        loc="%04d/%02d" % (y, m)
        print "photloc[%d]='%s';" % (p.id, loc)
    # print [(k[0], len(k[1])) for k in keywords]
    # print "Found %d keywords" % (len(keywords),)
