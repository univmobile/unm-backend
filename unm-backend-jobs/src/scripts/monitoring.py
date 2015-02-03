# -*- coding: utf-8 -*-
"""
Created on Mon Feb  2 08:04:46 2015

@author: mauricio
"""

import sys
import requests
import logging

url = sys.stdin
if len(sys.argv) > 1:
    try:
        url = sys.argv[1]
    except IOError:
        sys.exit(str(sys.exc_info()[1]) + '\n')

logging.basicConfig(filename='log')
        
logger = logging.getLogger('log monitoring')
logger.setLevel(logging.INFO)

try:
    content = requests.get(url)
except IOError:
    logger.critical(url + " Exception trying to connect to " + str(sys.exc_info()[1]))
    sys.exit()
    
status_code = content.status_code
text_type = content.headers['content-type']
text = content.text

if status_code == 200:
    if "application/json" in text_type:
        logger.info(url + " OK")
    else:
        logger.warning(url + " Connection established but output is not JSON")
else:
    logger.error(url + ' ' + status_code + ' ' + text_type)
