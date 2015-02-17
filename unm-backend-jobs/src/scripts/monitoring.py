# -*- coding: utf-8 -*-
"""
Created on Mon Feb  2 08:04:46 2015

@author: mauricio
"""

import sys
import logging
import urllib2
import json

url = sys.stdin
if len(sys.argv) > 1:
    try:
        url = sys.argv[1]
    except IOError:
        sys.exit(str(sys.exc_info()[1]) + '\n')

logging.basicConfig(filename='unm-monitoring.log', format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        
logger = logging.getLogger('unm-monitoring')
logger.setLevel(logging.INFO)

try:
    res = urllib2.urlopen(url)
except urllib2.HTTPError as e:
    logger.critical("%s HTTP Exception trying to connect: %d - %s" % (url, e.code, str(e.read())))
    sys.exit()
except:
    logger.critical("%s Unexpected Exception trying to connect: %s" % (url, str(sys.exc_info()[1])))
    sys.exit()

text = res.read() 
status_code = 200
text_type = res.info().getheader('Content-Type')

if status_code == 200:
    if text_type in ("application/json", "application/hal+json"):
        try:
            parsed_json = json.loads(text)
            logger.info(url + " OK")
        except:
            print sys.exc_info()
            logger.error(url + " Connection established but output cannot be parsed as a valid JSON")
    else:
        logger.warning(url + " Connection established but output is not JSON")
else:
    logger.error(url + ' ' + status_code + ' ' + text_type)
