package fr.univmobile.backend.it;

import static fr.univmobile.backend.core.impl.ConnectionType.MYSQL;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

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

		final Connection cxn = DriverManager.getConnection(
				PropertiesUtils.getTestProperty("mysqlUrl"),
				PropertiesUtils.getTestProperty("mysqlUsername"),
				PropertiesUtils.getTestProperty("mysqlPassword"));
		try {

			TestBackend.setUpData("001", new File(dataDir), MYSQL, cxn);

		} finally {
			cxn.close();
		}

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

		final File file = new File("target", "testHomePage.png");

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

	@Test
	public void testNoHardcodedUnivmobileDevUnivParis1() throws Exception {

		// selenium.click("identifier=button-myself"); // Doesn’t work

		selenium.getEval("window.document.getElementById('button-myself').click();");

		selenium.waitForPageToLoad("10000");

		final String KWARGS = "";

		final File file = new File("target",
				"testNoHardcodedUnivmobileDevUnivParis1.png");

		selenium.captureEntirePageScreenshot(file.getCanonicalPath(), KWARGS);

		final String pageSource = selenium.getHtmlSource();

		FileUtils.write(new File("target", "entered.html"), pageSource);

		// We test that the "entered" page doesn’t contain the hardcoded line:
		// "JSON : https://univmobile-dev.univ-paris1.fr/json/regions"
		// Because univmobile-dev is the integration platform, not the dev / ci
		// one.
		// The "entered" page should only contain a line like this one:
		// "JSON : http://localhost:8380/unm-backend/json/regions"

		final String JSON = "JSON";

		assertTrue("Page source should contain text: \"" + JSON + "\"",
				containsIgnoreCase(pageSource, JSON));

		final String JSON_REGIONS = "/json/regions";

		assertTrue("Page source should contain text: \"" + JSON_REGIONS + "\"",
				containsIgnoreCase(pageSource, JSON_REGIONS));

		final String HTTPS_UNIVMOBILE_DEV = "https://univmobile-dev";

		assertFalse("Page source should not contain text: \""
				+ HTTPS_UNIVMOBILE_DEV + "\"",
				containsIgnoreCase(pageSource, HTTPS_UNIVMOBILE_DEV));

		final String UNIV_PARIS1 = "univ-paris1.fr/json";

		assertFalse("Page source should not contain text: \"" + UNIV_PARIS1
				+ "\"", containsIgnoreCase(pageSource, UNIV_PARIS1));

		final String BASE_URL = "baseURL"; // "${baseURL}"

		assertFalse(
				"Page source should not contain text: \"" + BASE_URL + "\"",
				containsIgnoreCase(pageSource, BASE_URL));
	}
}
