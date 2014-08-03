package fr.univmobile.backend.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class TestBackendTest {

	@Test
	public void testSetUpData() throws IOException {

		final File reportFile = new File("target/001",
				"TestBackend.setUpData.log");

		FileUtils.deleteQuietly(reportFile);

		TestBackend.setUpData("001", new File("target", "001"));

		assertTrue("reportFile should exist: " + reportFile.getCanonicalPath(),
				reportFile.exists());

		assertNotEquals(
				"reportFile.length() should not be zero: "
						+ reportFile.getCanonicalPath(), 0, reportFile.length());
	}

	@Test
	public void testReadBackendAppBaseURL() throws IOException {

		final String baseURL = TestBackend.readBackendAppBaseURL(new File(
				"src/test/WEB-INF/001-unm-backend-app-noshib_web.xml"));

		assertEquals("http://localhost:8380/unm-backend/", baseURL);
	}

	@Test
	public void testReadBackendAppDataDir() throws IOException {

		final String dataDir = TestBackend.readBackendAppDataDir(new File(
				"src/test/WEB-INF/001-unm-backend-app-noshib_web.xml"));

		assertEquals("/tmp/unm-backend/dataDir", dataDir);
	}

	@Test
	public void testReadBackendLogFile() throws Exception {

		final String logFile = TestBackend
				.readBackendAppLogFile(new File(
						"src/test/WEB-INF/classes/001-unm-backend-app-noshib_log4j.xml"));

		assertEquals("/tmp/unm-backend.log", logFile);
	}

	@Test
	public void testReadMobileWebBaseURL() throws IOException {

		final String baseURL = TestBackend.readMobilewebAppBaseURL(new File(
				"src/test/WEB-INF/002-unm-mobileweb-app-local_web.xml"));

		assertEquals("http://localhost:8380/unm-mobileweb/", baseURL);
	}

	@Test
	public void testReadMobileWebDataDir() throws IOException {

		final String dataDir = TestBackend.readMobilewebAppDataDir(new File(
				"src/test/WEB-INF/002-unm-mobileweb-app-local_web.xml"));

		assertEquals("/tmp/unm-mobileweb/dataDir", dataDir);
	}
}
