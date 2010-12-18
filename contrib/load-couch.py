#!/usr/bin/env python

import os
import sys
import time
import base64
import getpass
import traceback

import threading
import Queue

import couchdb

import libphoto

N_THREADS=5

Q=Queue.Queue()

class Parser(libphoto.AlbumParser):

    def __init__(self, couch, base):
        self.couch = couch
        self.databases = dict([(n, self.couch[n]) for n in self.couch])
        self.base = base

    def encode_annotation(self, a):
        return {'x': a.x, 'y': a.y, 'width': a.width, 'height': a.height,
                'title': a.title, 'addedby': a.addedby, 'ts': a.ts}

    def getDbName(self, db_name):
        return 'photo-' + db_name.replace('/', '-').lower()

    def getDb(self, db_name):
        name = self.getDbName(db_name)
        try:
            if name in self.databases:
                db = self.databases[name]
            else:
                print "Retrieving db", name
                self.databases[name] = self.couch[name]
        except couchdb.ResourceNotFound:
            print "Creating db", name
            self.couch.create(name)
            self.databases[name] = self.couch[name]

        return self.databases[name]

    def gotPhoto(self, p):
        print "Got photo", p
        db = self.getDb(p.cat)
        try:
            doc = {
                '_id': p.md5,
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
            }
            docid, rev = db.save(doc)
            Q.put((p, self.getDbName(p.cat), docid, rev))

        except couchdb.ResourceConflict:
            print "Got a resource conflict on %s" % (p.md5,)

class ImageWorker(threading.Thread):

    def __init__(self, url, pbase):
        threading.Thread.__init__(self)
        self.c = couchdb.Server(url)
        self.pbase = pbase
        self.setDaemon(True)
        self.start()

    def save_attachment(self, p, dbname, docid, img, t, name, rev=None):
        retries = 3
        while True:
            try:
                if rev is None:
                    rev = self.c[dbname][docid]['_rev']
                self.c[dbname].put_attachment({'_id': docid,
                                               '_rev': rev}, img,
                                              filename=name + '.' + p.extension,
                                              content_type=t)
                return
            except:
                traceback.print_exc()
                if retries > 0:
                    retries = retries - 1
                    if retries:
                        print "Retrying", p.id, "with", retries, "remaining tries"
                    time.sleep(1)
                else:
                    sys.exit(1)

    def save_attachments(self, p, dbname, docid, rev):
        print "Processing image for", p.id
        t = p.extension
        if t == 'jpg':
            t = 'jpeg'

        t = 'image/' + t

        self.save_attachment(p, dbname, docid,
                             libphoto.fetchImage(self.pbase, p.id),
                             t, 'original', rev)
        self.save_attachment(p, dbname, docid,
                             libphoto.fetchImage(self.pbase, p.id,
                                                 thumbnail=True),
                             t, 'thumb')
        self.save_attachment(p, dbname, docid,
                             libphoto.fetchImage(self.pbase, p.id,
                                                 size='800x600'),
                             t, '800x600')

    def run(self):
        while True:
            p, dbname, docid, rev = Q.get()
            self.save_attachments(p, dbname, docid, rev)
            Q.task_done()

if __name__ == '__main__':
    base, u=sys.argv[1:]
    fn = 'index.xml'

    pw=os.getenv('PHOTO_PW')
    if pw is None:
        pw=getpass.getpass()

    url = 'http://localhost:5984/'
    c = couchdb.Server(url, full_commit=False)

    libphoto.authenticate(base, u, pw)

    libphoto.parseIndex(libphoto.fetchIndex(base), Parser(c, base))

    threads = [ImageWorker(url, base) for w in range(N_THREADS)]

    Q.join()

    for db in c:
        print "Compacting", db
        c[db].compact()

