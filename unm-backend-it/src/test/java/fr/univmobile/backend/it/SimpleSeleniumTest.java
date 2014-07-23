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

		final int seleniumPort = 8888;
		final String url = "http://localhost:"
				+ PropertiesUtils.getTestProperty("tomcat.port") + "/";

		selenium = new DefaultSelenium("localhost", seleniumPort, "*firefox",
				url);

		selenium.start();

		selenium.open("/unm-backend");

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

		final String TEXT = "Shibboleth";

		assertFalse("Page source should not contain text: \"" + TEXT + "\"",
				containsIgnoreCase(pageSource, TEXT));
	}
}
