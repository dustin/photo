#!/usr/bin/env python
"""
Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
"""

import sys
import xmlrpclib

if __name__ == '__main__':

    s=xmlrpclib.Server(sys.argv[1])
    username=sys.argv[2]
    password=sys.argv[3]
    for cat in s.getCategories.getAddable(username, password):
        print cat
