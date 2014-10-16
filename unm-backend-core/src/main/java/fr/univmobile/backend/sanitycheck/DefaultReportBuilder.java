package fr.univmobile.backend.sanitycheck;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.univmobile.commons.datasource.Entry;

/**
 * Default implementation of {@link ReportBuilder}, that throws an exception
 * each time an error is encountered during the sanity checks.
 */
public class DefaultReportBuilder implements ReportBuilder {

	@Override
	public CheckReport build() {

		return new CheckReport() {
			
			@Override
			public boolean isSuccess() {
				
				return true;
			}
		};
	}

	@Override
	public void startEntryCheck(final File file,
			final Class<? extends Entry<?>> clazz) throws IOException {

		// currentFile = file;

		// currentFilePath = file.getCanonicalPath();

		currentFilename = file.getName();

		currentDirName = file.getParentFile().getName();
	}

	// private File currentFile;

	// private String currentFilePath;

	private String currentFilename;

	private String currentDirName;

	@Override
	public void endEntryCheck() {

		// do nothing
	}

	@Override
	public void errorWhileInvokingAccessor(final Method method,
			final Exception e) {

		final String message;
		
		if (InvocationTargetException.class.isInstance(e)) {
			
			message = ((InvocationTargetException)e).getTargetException().toString();
			
		} else {
			
			message = e.toString();
		}
		
		throw new SanityCheckException(currentDirName + '/' + currentFilename
				+ ": " + method.getName() + "(): " + message);
	}
}
