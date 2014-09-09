package fr.univmobile.backend.sysadmin;

import java.io.File;
import java.io.IOException;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

/**
 * validator for command-line parameter: "-data" (= dataDir)
 */
public class DataDirValidator implements IValueValidator<File> {

	@Override
	public void validate(final String name, final File dataDir) {

		try {

			validateDataDir(dataDir);

		} catch (final IOException e) {
			throw new ParameterException(e);
		}
	}

	private static void validateDataDir(final File dataDir) throws IOException {

		final String canonicalPath;

		canonicalPath = dataDir.getCanonicalPath();

		if (!dataDir.isDirectory()) {
			throw new ParameterException("-data should be a directory: "
					+ canonicalPath);
		}

		validateCategoryExists(dataDir, "users");
		validateCategoryExists(dataDir, "regions");
		validateCategoryExists(dataDir, "pois");
		validateCategoryExists(dataDir, "comments");
	}

	private static void validateCategoryExists(final File dataDir,
			final String category) throws IOException {

		final File dir = new File(dataDir, category);

		final String canonicalPath;

		try {

			canonicalPath = dir.getCanonicalPath();

		} catch (final IOException e) {
			throw new ParameterException(e);
		}

		if (!dir.isDirectory()) {
			throw new ParameterException("-data: Category \"" + category
					+ "\" should be a directory: " + canonicalPath);
		}
	}
}
