#!/usr/bin/env python
"""
Backup the images from the photo album.

Copyright (c) 2007  Dustin Sallings <dustin@spy.net>
"""
# arch-tag: B871B26E-29D1-4A20-A917-B36352740CCE

import os
import sys
import md5
import gzip
import time
import shutil
import getpass
import libphoto

def makeIndex(fn):
    f=libphoto.fetchIndex(base)

    fout=gzip.GzipFile(fn + ".tmp", "w")

    shutil.copyfileobj(f, fout)
    fout.close()
    f.close()

    os.rename(fn + ".tmp", fn)

def parseIndex(fn):
    fin=gzip.GzipFile(fn, "r")
    class P(libphoto.AlbumParser):
        photos=[]
        def gotPhoto(self, p):
            self.photos.append( (p.id, p.md5, p.extension) )
    p=P()
    libphoto.parseIndex(fin, p)
    fin.close()
    return p.photos

def makeFn(basedir, img):
    imgid, imgmd5, ext=img
    d=os.path.join(basedir, imgmd5[:2])
    fn=os.path.join(d, imgmd5 + "." + `imgid` + "." + ext)
    return d, fn

def validateMd5(fn, expectedMd5):
    f=open(fn, "rb")
    m=md5.md5()
    m.update(f.read())
    f.close()
    return expectedMd5 == m.hexdigest()

def storeImage(baseurl, d, fn, img):
    print "Fetching", img[0]

    imgid, imgmd5, ext=img

    if not os.path.exists(d):
        os.makedirs(d)

    fout=open(fn + ".tmp", "w")
    fin=libphoto.fetchImage(baseurl, imgid)

    shutil.copyfileobj(fin, fout)

    fin.close()
    fout.close()

    assert validateMd5(fn + ".tmp", imgmd5), "Invalid MD5 for " + `imgid`

    os.rename(fn + ".tmp", fn)

if __name__ == '__main__':
    base, u, s, d=sys.argv[1:]
    backup_name="index-%s.xml.gz" % (time.strftime("%Y%m%dT%H%M"),)
    fn=os.path.join(d, backup_name)

    pw=os.getenv('PHOTO_PW')
    if pw is None:
        pw=getpass.getpass()

    libphoto.authenticate(base, u, pw)

    makeIndex(fn)
    images=parseIndex(fn)
    for img in images:
        d, fn=makeFn(s, img)
        if not os.path.exists(fn):
            storeImage(base, d, fn, img)
