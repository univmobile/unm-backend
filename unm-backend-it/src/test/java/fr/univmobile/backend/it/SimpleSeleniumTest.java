package fr.univmobile.backend.it;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

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

		final RemoteWebDriver driver = new FirefoxDriver();

		final String url = "http://localhost:"
				+ PropertiesUtils.getTestProperty("tomcat.port");

		driver.get(url + "/unm-backend" //
				+ "?NO_SHIB_uid=tformica" //
				+ "&NO_SHIB_eppn=tformica@univ-paris1.fr" //
				+ "&NO_SHIB_displayName=Toto+Formica" //
				+ "&NO_SHIB_remoteUser=tformica@univ-paris1.fr" //
		);

		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(final WebDriver driver) {
				return driver.getTitle().length() != 0;
			}
		});
	}

	private WebDriver driver = new FirefoxDriver();

	@After
	public void tearDown() throws Exception {

		driver.quit();
	}

	@Test
	public void testHomePage() throws Exception {

		final File file = // ((TakesScreenshot) augmentedDriver)
		((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

		FileUtils.copyFile(file, new File("target", "home.png"));

		System.out.println("Deleting: " + file.getCanonicalPath() + "...");

		final String pageSource = driver.getPageSource();

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
