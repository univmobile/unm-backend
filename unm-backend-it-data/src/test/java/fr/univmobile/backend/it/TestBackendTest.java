package fr.univmobile.backend.it;

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
}
