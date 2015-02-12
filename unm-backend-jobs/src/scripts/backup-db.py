# -*- coding: utf-8 -*-
"""
Created on Tue Feb  3 11:05:13 2015

@author: mauricio
"""

import os
import subprocess
import datetime
import logging

logging.basicConfig(filename='log backup-db')
        
logger = logging.getLogger('backup-db')
logger.setLevel(logging.INFO)

if not os.path.exists('/home/backups/univmobile/db/'):
    os.makedirs('/home/backups/univmobile/db/')

OUTPUT_FILE = '/home/backups/univmobile/db/univmobile_%s.sql' % datetime.datetime.now().strftime('%Y%m%d%H%M%S')
MYSQLDUMP_CMD = '/usr/bin/mysqldump -p -u root univmobile > %s' % OUTPUT_FILE

p = subprocess.Popen(MYSQLDUMP_CMD , shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

retval = p.wait()

if retval == 0:
    logger.info('Backup executed successfully. Dump created at %s' % OUTPUT_FILE)
else:
    logger.error('Backup failed. Error follows: ' + p.communicate()[1].replace('\n', ''))