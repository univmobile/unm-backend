package fr.univmobile.backend.it;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.CR;
import static org.apache.commons.lang3.StringUtils.LF;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.avcompris.binding.annotation.Namespaces;
import net.avcompris.binding.annotation.XPath;
import net.avcompris.binding.dom.helper.DomBinderUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.collect.Iterables;

import fr.univmobile.backend.core.Indexation;
import fr.univmobile.backend.core.impl.ConnectionType;
import fr.univmobile.backend.core.impl.IndexationImpl;

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
	public static void setUpData(final String testDataId, final File destDir,
			final ConnectionType dbType, final Connection cxn)
			throws IOException, SQLException, ParserConfigurationException,
			SAXException {

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

			if (!resourcePath.startsWith(prefix)) {
				continue;
			}

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

		if (count == 0) {
			throw new FileNotFoundException("testDataId not found: "
					+ testDataId + ", prefix: " + prefix);
		}

		final String message = "Copied " + count //
				+ " XML Resource" + (count > 1 ? "s" : "") //
				+ " from: " + prefix + " to: " + destDir.getCanonicalPath();

		FileUtils.write(reportFile, "# " + message + CR + LF, UTF_8, true);

		System.out.println(message);

		System.out.println("Indexation...");

		final Indexation indexation = new IndexationImpl(destDir, dbType, cxn);

		indexation.indexData(null);
	}

	private static String[] loadResourcePaths() throws IOException {

		final InputStream is = getResourceAsStream("resource.paths");

		if (is == null) {
			System.err.println("Cannot load resource.paths from classLoader.");
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

		System.err.println( //
				"Fallback: Trying local files in src/main/resources/");

		resourcePaths.addAll(loadLocalResourcePaths("", new File(
				"src/main/resources")));

		System.err.println( //
				"Fallback: Trying local files in ../unm-backend-it-data/src/main/resources/");

		resourcePaths.addAll(loadLocalResourcePaths("", new File(
				"../unm-backend-it-data/src/main/resources")));

		System.err.println( //
				"Fallback: Trying local files in ../../../workspace/unm-backend/unm-backend-it-data/src/main/resources/");

		resourcePaths
				.addAll(loadLocalResourcePaths(
						"",
						new File(
								"../../../workspace/unm-backend/unm-backend-it-data/src/main/resources")));

		return Iterables.toArray(resourcePaths, String.class);
	}

	private static Collection<String> loadLocalResourcePaths(
			final String prefix, final File dir) throws IOException {

		if (!dir.isDirectory()) {
			// throw new FileNotFoundException("Not a directory: "
			// + dir.getCanonicalPath());
			System.err.println("Not a directory: " + dir.getCanonicalPath());
			return Collections.emptyList();
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

		final BackendAppWebXml webDescriptor = DomBinderUtils.xmlContentToJava(
				webXmlFile, BackendAppWebXml.class);

		return webDescriptor.getBackendAppBaseURL();
	}

	@Test
	public static String readBackendAppDataDir(final File webXmlFile)
			throws IOException {

		final BackendAppWebXml webDescriptor = DomBinderUtils.xmlContentToJava(
				webXmlFile, BackendAppWebXml.class);

		return webDescriptor.getBackendAppDataDir();
	}

	@Namespaces("xmlns:j2ee=http://java.sun.com/xml/ns/j2ee")
	@XPath("/j2ee:web-app")
	private interface BackendAppWebXml {

		@XPath("j2ee:servlet/j2ee:init-param[j2ee:param-name = 'baseURL']/j2ee:param-value")
		String getBackendAppBaseURL();

		@XPath("j2ee:servlet/j2ee:init-param[j2ee:param-name = 'dataDir']/j2ee:param-value")
		String getBackendAppDataDir();
	}

	public static String readLog4jLogFile(final File log4jXmlFile)
			throws IOException, SAXException, ParserConfigurationException {

		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();

		documentBuilderFactory.setNamespaceAware(true);

		final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

		documentBuilderFactory.setFeature(LOAD_EXTERNAL_DTD, false);

		final DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();

		final Document document = documentBuilder.parse(log4jXmlFile);

		final Log4JXml log4jConfig = DomBinderUtils.xmlContentToJava(document,
				Log4JXml.class);

		return log4jConfig.getLogFile();
	}

	@Namespaces("xmlns:log4j=http://jakarta.apache.org/log4j/")
	@XPath("/log4j:configuration")
	private interface Log4JXml {

		@XPath("appender/param[@name = 'File']/@value")
		String getLogFile();
	}

	public static String readMobilewebAppBaseURL(final File webXmlFile)
			throws IOException {

		final MobilewebAppWebXml webDescriptor = DomBinderUtils
				.xmlContentToJava(webXmlFile, MobilewebAppWebXml.class);

		return webDescriptor.getMobilewebAppBaseURL();
	}

	@Test
	public static String readMobilewebAppLocalDataDir(final File webXmlFile)
			throws IOException {

		final MobilewebAppWebXml webDescriptor = DomBinderUtils
				.xmlContentToJava(webXmlFile, MobilewebAppWebXml.class);

		final String dataDirRegions = webDescriptor
				.getMobilewebAppDataDirRegions();

		if (isBlank(dataDirRegions)) {
			throw new IllegalArgumentException("No dataDir in: "
					+ webXmlFile.getCanonicalPath());
		}

		return substringBefore(dataDirRegions, ("/regions"));
	}

	@Namespaces("xmlns:j2ee=http://java.sun.com/xml/ns/j2ee")
	@XPath("/j2ee:web-app")
	private interface MobilewebAppWebXml {

		@XPath("j2ee:servlet/j2ee:init-param[j2ee:param-name = 'baseURL']/j2ee:param-value")
		String getMobilewebAppBaseURL();

		@XPath("j2ee:servlet/j2ee:init-param"
				+ "[j2ee:param-name = 'inject:File ref:dataDirRegions']/j2ee:param-value")
		String getMobilewebAppDataDirRegions();
	}
}
