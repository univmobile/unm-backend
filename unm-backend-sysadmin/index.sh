#!/bin/bash

M2_REPO="${HOME}/.m2/repository"

CP=
CP="${CP}:${HOME}/workspace-work/unm-backend/unm-backend-sysadmin/target/classes"
CP="${CP}:${HOME}/workspace-work/unm-backend/unm-backend-core/target/classes"
CP="${CP}:${HOME}/workspace-work/unm-backend/unm-commons-datasource/target/classes"
CP="${CP}:${HOME}/project/avc-binding-dom/target/classes"
CP="${CP}:${HOME}/project/avc-binding-common/target/classes"
CP="${CP}:${HOME}/project/avc-binding-yaml/target/classes"
CP="${CP}:${HOME}/project/avc-commons-lang/target/classes"
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
CP="${CP}:${M2_REPO}/net/avcompris/commons/avc-commons-lang/0.4.3/avc-commons-lang-0.4.3.jar"
CP="${CP}:${M2_REPO}/net/avcompris/commons/avc-binding-yaml/0.1.1/avc-binding-yaml-0.1.1.jar"
CP="${CP}:${M2_REPO}/net/avcompris/commons/avc-binding-common/0.1.4/avc-binding-common-0.1.4.jar"
CP="${CP}:${M2_REPO}/net/avcompris/commons/avc-binding-dom/0.1.6/avc-binding-dom-0.1.6.jar"

java -cp "${CP}" fr.univmobile.backend.sysadmin.Main index \
	-data /home/mauricio/var/univmobile -dburl jdbc:mysql://localhost:3306/univmobile \
	-dbusername root -dbpassword $@
