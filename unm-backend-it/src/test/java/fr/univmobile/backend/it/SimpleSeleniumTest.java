package fr.univmobile.backend.it;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

import fr.univmobile.testutil.PropertiesUtils;

public class SimpleSeleniumTest {

	@Before
	public void setUp() throws Exception {

		// 1. DATA

		final File dataDir = new File("/tmp/unm-backend/dataDir");

		dataDir.mkdirs();

		FileUtils.deleteDirectory(dataDir);

		FileUtils.copyDirectory(new File("src/test/data"), dataDir);

		// 2. WEBAPP

		final int seleniumPort = 8888;

		final String url = "http://localhost:"
				+ PropertiesUtils.getTestProperty("tomcat.port") + "/";

		selenium = new DefaultSelenium("localhost", seleniumPort, "*firefox",
				url);

		selenium.start();

		selenium.open("/unm-backend" //
				+ "?NO_SHIB_uid=tformica" //
				+ "&NO_SHIB_eppn=tformica@univ-paris1.fr" //
				+ "&NO_SHIB_displayName=Toto+Formica" //
				+ "&NO_SHIB_remoteUser=tformica@univ-paris1.fr" //
		);

		selenium.waitForPageToLoad(Integer.toString(10000));
	}

	private Selenium selenium;

	@After
	public void tearDown() throws Exception {

		selenium.stop();
	}

	@Test
	public void testHomePage() throws Exception {

		final String KWARGS = "";

		final File file = new File("target", "home.png");

		selenium.captureEntirePageScreenshot(file.getCanonicalPath(), KWARGS);

		final String pageSource = selenium.getHtmlSource();

		FileUtils.write(new File("target", "pageSource.html"), pageSource);

		final String SHIBBOLETH = "Shibboleth";

		assertFalse("Page source should not contain text: \"" + SHIBBOLETH
				+ "\"", containsIgnoreCase(pageSource, SHIBBOLETH));

		final String EXCEPTION = "Exception";

		assertFalse("Page source should not contain text: \"" + EXCEPTION
				+ "\"", containsIgnoreCase(pageSource, EXCEPTION));
	}
}
