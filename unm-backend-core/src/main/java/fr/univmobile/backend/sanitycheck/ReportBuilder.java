package fr.univmobile.backend.sanitycheck;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import fr.univmobile.commons.datasource.Entry;

public interface ReportBuilder {

	CheckReport build();

	void startEntryCheck(File file, Class<? extends Entry<?>> clazz)
			throws IOException;

	void endEntryCheck();

	void errorWhileInvokingAccessor(Method method, Exception e);
}
