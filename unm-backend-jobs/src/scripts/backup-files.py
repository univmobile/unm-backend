# -*- coding: utf-8 -*-
"""
Created on Tue Feb  3 14:38:05 2015

@author: mauricio
"""

import os
import subprocess
import datetime
import logging

logging.basicConfig(filename='log backup-files')
        
logger = logging.getLogger('backup-files')
logger.setLevel(logging.INFO)

if not os.path.exists('/home/backups/univmobile/assests/'):
    os.makedirs('/home/backups/univmobile/assests/')
    
CONFIGURE_PATH_TO_WEBAPPS = '/home/backups/univmobile/files/'
    
BACKUP_LIST = [
    '/qrcodes',
    '/imagemaps',
    '/universitieslogos',
    '/categoriesicons'
]
OUTPUT_TARFILE = '/home/backups/univmobile/assests/univmobile_%s.tgz' % datetime.datetime.now().strftime('%Y%m%d%H%M%S')
TAR_CMD = '/usr/bin/tar cpfz %s %s' % (OUTPUT_TARFILE, ' '.join(BACKUP_LIST))

p = subprocess.Popen(TAR_CMD , shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

retval = p.wait()

if retval == 0:
    logger.info('Backup executed successfully. Tar file created at %s' % OUTPUT_TARFILE)
else:
    logger.error('Backup failed. Error follows: ' + p.communicate()[1].replace('\n', ''))