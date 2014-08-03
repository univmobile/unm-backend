package fr.univmobile.backend.it;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.CR;
import static org.apache.commons.lang3.StringUtils.LF;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.avcompris.binding.annotation.Namespaces;
import net.avcompris.binding.annotation.XPath;
import net.avcompris.binding.dom.helper.DomBinderUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.Iterables;

/**
 * Utilities to load data, extract packaging information, etc.
 * 
 * @author David Andrianavalontsalama
 */
public abstract class TestBackend {

	/**
	 * @param testDataId
	 *            the testDataID, corresponding to a given set of directories in
	 *            src/test/data. For instance, "<code>001</code>".
	 * @param destDir
	 *            the local destination directory where to install the XML, for
	 *            instance "<code>/tmp/unm-backend/dirData</code>".
	 */
	public static void setUpData(final String testDataId, final File destDir)
			throws IOException {

		System.out.println("Copying XML Resources to: "
				+ destDir.getCanonicalPath() + "...");

		destDir.mkdirs();

		FileUtils.deleteDirectory(destDir);

		final String[] resourcePaths = loadResourcePaths();

		final String prefix = "data/" + testDataId + "/";

		int count = 0;

		final File reportFile = new File(destDir, "TestBackend.setUpData.log");

		FileUtils.write(reportFile, "# " + new DateTime() + CR + LF, UTF_8);
		FileUtils.write(reportFile, "# TestBackend.setUp():" + CR + LF, //
				UTF_8, true);

		for (final String resourcePath : resourcePaths) {

			if (resourcePath.startsWith(prefix)) {

				final String destPath = substringAfter(resourcePath, prefix);

				InputStream is = getResourceAsStream(resourcePath);

				if (is == null) {

					System.err.println( //
							"Cannot load resource from classLoader: "
									+ resourcePath);
					System.err.println( //
							"Fallback: Trying local file in src/main/resources/");

					is = new FileInputStream(new File("src/main/resources",
							resourcePath));
				}

				try {

					final File outFile = new File(destDir, destPath)
							.getCanonicalFile();

					FileUtils.forceMkdir(outFile.getParentFile());

					final OutputStream os = new FileOutputStream(outFile);
					try {

						IOUtils.copy(is, os);

					} finally {
						os.close();
					}

				} finally {
					is.close();
				}

				FileUtils.write(reportFile, "#   " + destPath + CR + LF, //
						UTF_8, true);

				++count;
			}
		}

		if (count == 0) {
			throw new FileNotFoundException("testDataId not found: "
					+ testDataId + ", prefix: " + prefix);
		}

		final String message = "Copied " + count //
				+ " XML Resource" + (count > 1 ? "s" : "") //
				+ " from: " + prefix + " to: " + destDir.getCanonicalPath();

		FileUtils.write(reportFile, "# " + message + CR + LF, UTF_8, true);

		System.out.println(message);
	}

	private static String[] loadResourcePaths() throws IOException {

		final InputStream is = getResourceAsStream("resource.paths");

		if (is == null) {
			System.err.println("Cannot load resource.paths from classLoader.");
			System.err
					.println("Fallback: Trying local files in src/main/resources/");
			return loadLocalResourcePaths();
		}

		final List<String> resourcePaths;

		try {

			resourcePaths = IOUtils.readLines(is, UTF_8);

		} finally {
			is.close();
		}

		return Iterables.toArray(resourcePaths, String.class);
	}

	private static String[] loadLocalResourcePaths() throws IOException {

		final List<String> resourcePaths = new ArrayList<String>();

		resourcePaths.addAll(loadLocalResourcePaths("", new File(
				"src/main/resources")));

		return Iterables.toArray(resourcePaths, String.class);
	}

	private static Collection<String> loadLocalResourcePaths(
			final String prefix, final File dir) throws IOException {

		if (!dir.isDirectory()) {
			throw new FileNotFoundException("Not a directory: "
					+ dir.getCanonicalPath());
		}

		final List<String> resourcePaths = new ArrayList<String>();

		for (final File file : dir.listFiles()) {

			final String filename = file.getName();

			if (file.isDirectory()) {

				resourcePaths.addAll(loadLocalResourcePaths(prefix + filename
						+ "/", file));

			} else if (file.isFile() && filename.endsWith(".xml")) {

				resourcePaths.add(prefix + filename);
			}
		}

		return resourcePaths;
	}

	@Nullable
	private static InputStream getResourceAsStream(final String path) {

		checkNotNull(path, "path");

		final InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(path);

		if (is == null) {
			System.err.println("Resource not found at path: " + path);
		}

		return is;
	}

	public static String readBackendAppBaseURL(final File webXmlFile)
			throws IOException {

		return DomBinderUtils.xmlContentToJava(webXmlFile,
				BackendAppWebXml.class).getBackendAppBaseURL();
	}

	@Test
	public static String readBackendAppDataDir(final File webXmlFile)
			throws IOException {

		return DomBinderUtils.xmlContentToJava(webXmlFile,
				BackendAppWebXml.class).getBackendAppDataDir();
	}

	@Namespaces("xmlns:j2ee=http://java.sun.com/xml/ns/j2ee")
	@XPath("/j2ee:web-app")
	private interface BackendAppWebXml {

		@XPath("j2ee:servlet/j2ee:init-param[j2ee:param-name = 'baseURL']/j2ee:param-value")
		String getBackendAppBaseURL();

		@XPath("j2ee:servlet/j2ee:init-param[j2ee:param-name = 'dataDir']/j2ee:param-value")
		String getBackendAppDataDir();
	}
}
