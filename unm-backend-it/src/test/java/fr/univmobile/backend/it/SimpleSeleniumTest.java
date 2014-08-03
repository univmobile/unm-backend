package fr.univmobile.backend.it;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class SimpleSeleniumTest {

	@Before
	public void setUp() throws Exception {

		// 0. ENVIRONMENT

		// "http://localhost:8380/unm-backend/"
		final String baseURL = TestBackend.readBackendAppBaseURL(new File(
				"target", "unm-backend-app-noshib/WEB-INF/web.xml"));

		// "/unm-backend/"
		final String contextRoot = "/"
				+ substringAfter(substringAfter(baseURL, "://"), "/");

		// "http://localhost:"
		// + PropertiesUtils.getTestProperty("tomcat.port") + "/";
		final String url = substringBefore(baseURL, contextRoot) + "/";

		// "/tmp/unm-backend/dataDir"
		final String dataDir = TestBackend.readBackendAppDataDir(new File(
				"target", "unm-backend-app-noshib/WEB-INF/web.xml"));

		// 1. INJECT DATA

		TestBackend.setUpData("001", new File(dataDir));

		// 2. WEBAPP

		final int seleniumPort = 8888;

		selenium = new DefaultSelenium("localhost", seleniumPort, "*firefox",
				url);

		selenium.start();

		selenium.open(contextRoot //
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

		final String FORBIDDEN = "FORBIDDEN";

		assertFalse("Page source should not contain text: \"" + FORBIDDEN
				+ "\"", containsIgnoreCase(pageSource, FORBIDDEN));
	}
}
