#!/usr/bin/env python

import os
import sys
import base64
import getpass

import couchdb

import libphoto

class Parser(libphoto.AlbumParser):

    def __init__(self, couch, base):
        self.couch = couch
        self.base = base

    def encode_annotation(self, a):
        return {'x': a.x, 'y': a.y, 'width': a.width, 'height': a.height,
                'title': a.title, 'addedby': a.addedby, 'ts': a.ts}

    def attachment(self, p):
        img = libphoto.fetchImage(self.base, p.id)
        t = p.extension
        if t == 'jpg':
            t = 'jpeg'

        t = 'image/' + t

        class F(object):

            def __init__(self):
                self.bytes = []

            def write(self, b):
                self.bytes.append(b.replace("\n", ""))

        f = F()
        base64.encode(img, f)

        return {'photo.jpg': { 'content_type': t,
                               'data': ''.join(f.bytes)}}

    def gotPhoto(self, p):
        print "Got photo", p
        # if p.md5 in self.couch:
        #     print "Already have", p
        #     return
        try:
            self.couch[p.md5] = {
                'old_id': p.id,
                'type': 'photo',
                'size': p.size,
                'width': p.width,
                'height': p.height,
                'tnwidth': p.tnwidth,
                'tnheight': p.tnheight,
                'cat': p.cat,
                'addedby': p.addedby,
                'taken': p.taken,
                'ts': p.ts,
                'descr': p.descr,
                'extension': p.extension,
                'keywords': list(p.keywords),
                'annotations': [self.encode_annotation(a) for a in p.annotations],
                '_attachments': self.attachment(p)}
        except couchdb.client.ResourceConflict:
            print "Got a resource conflict on %s" % (p.md5,)

if __name__ == '__main__':
    base, u=sys.argv[1:]
    fn = 'index.xml'

    pw=os.getenv('PHOTO_PW')
    if pw is None:
        pw=getpass.getpass()

    c = couchdb.Server('http://localhost:5984')

    libphoto.authenticate(base, u, pw)

    libphoto.parseIndex(libphoto.fetchIndex(base), Parser(c['photo'], base))
