#!/bin/sh

# Repackage a WAR archive, by updating some servlet init parameters:
#  - log file
#  - data directory

WAR="${1}"
DATADIR="${2}"

test -z "${WAR}" \
	&& echo "*** ERROR: Please specify a .war file as first parameter" \
	&& exit 1

echo "Repackaging .war file: ${WAR}"

test ! -f "${WAR}" \
	&& echo "*** ERROR: The .war file doesn't exist: ${WAR}" \
	&& exit 1

echo "The .war file exists."

test -z "${DATADIR}" \
	&& echo "*** ERROR: Please specify a data directory as second parameter" \
	&& exit 1

echo "Repackaging with data directory = ${DATADIR}"

rm -rf target/unm-backend

unzip -d target/unm-backend/ "${WAR}"

xslt 


	