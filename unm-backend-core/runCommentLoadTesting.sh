#!/bin/sh

VERSION=0.0.4-SNAPSHOT

export M2_REPO="${HOME}/.m2/repository"

CP=
CP="${CP}:target/classes"
CP="${CP}:target/test-classes"
CP="${CP}:${M2_REPO}/org/apache/commons/commons-lang3/3.3.2/commons-lang3-3.3.2.jar"
CP="${CP}:${M2_REPO}/commons-io/commons-io/2.4/commons-io-2.4.jar"
CP="${CP}:${M2_REPO}/fr/univmobile/unm-commons-datasource/${VERSION}/unm-commons-datasource-${VERSION}.jar"
CP="${CP}:${M2_REPO}/net/avcompris/commons/avc-binding-dom/0.1.4-SNAPSHOT/avc-binding-dom-0.1.4-SNAPSHOT.jar"
CP="${CP}:${M2_REPO}/net/avcompris/commons/avc-binding-common/0.1.3-SNAPSHOT/avc-binding-common-0.1.3-SNAPSHOT.jar"
CP="${CP}:${M2_REPO}/net/avcompris/commons/avc-commons-lang/0.4.2/avc-commons-lang-0.4.2.jar"
CP="${CP}:${M2_REPO}/xml-apis/xml-apis/1.4.01/xml-apis-1.4.01.jar"
CP="${CP}:${M2_REPO}/commons-logging/commons-logging/1.1.3/commons-logging-1.1.3.jar"
CP="${CP}:${M2_REPO}/log4j/log4j/1.2.17/log4j-1.2.17.jar"
CP="${CP}:${M2_REPO}/jaxen/jaxen/1.1.4/jaxen-1.1.4.jar"
CP="${CP}:${M2_REPO}/joda-time/joda-time/2.3/joda-time-2.3.jar"
CP="${CP}:${M2_REPO}/com/google/guava/guava/17.0/guava-17.0.jar"
CP="${CP}:${M2_REPO}/com/google/code/findbugs/jsr305/3.0.0/jsr305-3.0.0.jar"

java -cp "${CP}" \
	-Dlog4j.configuration=file:src/test/java/fr/univmobile/backend/core/loadtesting/log4j.xml \
	fr.univmobile.backend.core.loadtesting.CommentLoadTesting \
		100000 taskCreateComment_existing_thread_415