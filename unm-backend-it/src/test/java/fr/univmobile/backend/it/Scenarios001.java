package fr.univmobile.backend.it;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.Selenium;

import fr.univmobile.it.commons.BrowserNames;
import fr.univmobile.it.commons.DeviceNames;
import fr.univmobile.it.commons.Scenario;
import fr.univmobile.it.commons.Scenarios;
import fr.univmobile.it.commons.SeleniumEnabledTest;
import fr.univmobile.testutil.PropertiesUtils;

@Scenarios("Scénarios simples")
@DeviceNames("Firefox")
public class Scenarios001 extends SeleniumEnabledTest {

	@Before
	public void setUpData() throws Exception {

		// 0. ENVIRONMENT

		// "/tmp/unm-backend/dataDir"
		final String dataDir = TestBackend.readBackendAppDataDir(new File(
				"target", "unm-backend-app-noshib/WEB-INF/web.xml"));

		TestBackend.setUpData("001", new File(dataDir));

		final String logFile = TestBackend.readLog4jLogFile(new File("target",
				"unm-backend-app-noshib/WEB-INF/classes/log4j.xml"));

		System.out.println("Log file: " + logFile);
	}

	@BrowserNames
	public String getDefaultBrowser() {

		// e.g. "*firefox"
		// e.g. "*safari"
		// e.g. "*custom /usr/bin/chromium";

		try {

			return PropertiesUtils.getTestProperty("defaultBrowser");

		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final int PAUSE = 10000;

	@Scenario("Aller-retour sur la page « Ajouter un utilisateur »")
	@Test
	public void sc001() throws Exception {

		takeScreenshot("home.png");

		savePageSource("pageHome.html");

		elementById("button-myself").shouldBeVisible();

		elementById("div-myself").textShouldContain(
				"Votre connexion est authentifiée");

		elementById("button-myself").click();

		pause(PAUSE);

		takeScreenshot("entered.png");

		savePageSource("pageEntered.html");

		elementById("link-useradd").click();

		pause(PAUSE);

		takeScreenshot("useradd.png");

		elementById("button-cancel").click();

		pause(PAUSE);

		takeScreenshot("home2.png");
	}

	@Scenario("Voir les POIs de plus haut niveau")
	@Test
	public void Geocampus_000() throws Exception {

		takeScreenshot("home.png");

		elementById("button-myself").click();

		pause(PAUSE);

		takeScreenshot("entered.png");

		elementById("link-geocampus").click();

		pause(PAUSE);

		takeScreenshot("geocampus.png");

		elementById("link-poi-3792").click();

		pause(PAUSE);

		takeScreenshot("ucp.png");
	}

	@Scenario("Voir les commentaires d’un POI")
	@Test
	public void comments000() throws Exception {

		takeScreenshot("home.png");

		elementById("button-myself").click();

		pause(PAUSE);

		takeScreenshot("entered.png");

		elementById("link-pois").click();

		pause(PAUSE);

		takeScreenshot("pois.png");

		elementById("link-poi-3792").click();

		pause(PAUSE);

		takeScreenshot("ucp.png");

		final String labelledBy = 
				elementById("li-left-bottom-tabs-comments").attr("aria-labelledby");
		
		elementById(labelledBy).click();
		
		pause(PAUSE);

		takeScreenshot("ucp-details.png");
	}
}
