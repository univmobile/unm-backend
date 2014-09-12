#!/bin/bash

M2_REPO="${HOME}/.m2/repository"

CP=
CP="${CP}:${HOME}/Documents/workspace/unm-backend/unm-backend-sysadmin/target/classes"
CP="${CP}:${HOME}/Documents/workspace/unm-backend/unm-backend-core/target/classes"
CP="${CP}:${HOME}/Documents/workspace/unm-backend/unm-commons-datasource/target/classes"
CP="${CP}:${HOME}/Documents/workspace/avc-binding-dom/target/classes"
CP="${CP}:${HOME}/Documents/workspace/avc-binding-common/target/classes"
CP="${CP}:${HOME}/Documents/workspace/avc-binding-yaml/target/classes"
CP="${CP}:${HOME}/Documents/workspace/avc-commons-lang/target/classes"
CP="${CP}:${M2_REPO}/com/google/guava/guava/14.0.1/guava-14.0.1.jar"
CP="${CP}:${M2_REPO}/commons-io/commons-io/2.4/commons-io-2.4.jar"
CP="${CP}:${M2_REPO}/org/apache/commons/commons-lang3/3.3.2/commons-lang3-3.3.2.jar"
CP="${CP}:${M2_REPO}/jaxen/jaxen/1.1.4/jaxen-1.1.4.jar"
CP="${CP}:${M2_REPO}/commons-logging/commons-logging/1.1.3/commons-logging-1.1.3.jar"
CP="${CP}:${M2_REPO}/joda-time/joda-time/2.3/joda-time-2.3.jar"
CP="${CP}:${M2_REPO}/net/lingala/zip4j/zip4j/1.3.2/zip4j-1.3.2.jar"
CP="${CP}:${M2_REPO}/com/google/code/findbugs/jsr305/3.0.0/jsr305-3.0.0.jar"
CP="${CP}:${M2_REPO}/com/beust/jcommander/1.30/jcommander-1.30.jar"
CP="${CP}:${M2_REPO}/mysql/mysql-connector-java/5.1.32/mysql-connector-java-5.1.32.jar"
CP="${CP}:${M2_REPO}/log4j/log4j/1.2.17/log4j-1.2.17.jar"
CP="${CP}:${M2_REPO}/jvyaml/jvyaml/0.2.1/jvyaml-0.2.1.jar"
CP="${CP}:${M2_REPO}/xml-apis/xml-apis/1.4.01/xml-apis-1.4.01.jar"
CP="${CP}:${M2_REPO}/org/yaml/snakeyaml/1.12/snakeyaml-1.12.jar"

java -cp "${CP}" fr.univmobile.backend.sysadmin.Main index \
	-data src/test/data/001/ -dburl jdbc:mysql://localhost/toto \
	-dbusername root -dbpassword $@
