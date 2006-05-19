#!/usr/bin/env python
#
# arch-tag: C504C8CD-5D6E-11D9-9483-000A957659CC

from sys import argv
import urllib
import urllib2
import httplib
import base64

class ImageUpload:

    def __init__(self, url='http://bleu.west.spy.net/photo/api/photo/new'):
        self.url=url

        class EH(urllib2.HTTPDefaultErrorHandler):
            def http_error_default(self, req, fp, code, msg, hdrs):
                if code == 202:
                    return urllib.addinfourl(fp, hdrs, req.get_full_url())
                else:
                    urllib2.HTTPDefaultErrorHandler.http_error_default(
                        self, req, fp, code, msg, hdrs)

        self.opener=urllib2.build_opener(EH())

    def addImage(self, username=None, password=None, keywords=None,
        info=None, category=None, taken=None, image=None):

        args=urllib.urlencode({ 
            'keywords':keywords,
            'info':info,
            'category':category,
            'taken':taken,
            });

        req=urllib2.Request(self.url + "?" + args, image,
            {'Content-type': 'application/octet-stream',
             'Authorization': 'Basic ' + 
                base64.encodestring(username + ':' + password).strip() })
        response=self.opener.open(req)
        data=response.read()
        response.close()

        return(data)


if __name__ == '__main__':
    if len(argv) < 8:
        theroof="Usage:  " + argv[0] + " url username password keywords info " \
            + "category taken filename ... "
        raise theroof
    url=argv[1]
    username=argv[2]
    password=argv[3]
    keywords=argv[4]
    info=argv[5]
    category=argv[6]
    taken=argv[7]

    # Rest of the arguments are images
    uploader=ImageUpload(url)

    for filename in argv[8:]:
        f=open(filename)
        imageData=f.read()
        f.close()

        print "Image data is " + str(len(imageData)) + " bytes"
        rv=uploader.addImage(username, password, keywords, info, category,
            taken, imageData)

        print "Added image", rv
