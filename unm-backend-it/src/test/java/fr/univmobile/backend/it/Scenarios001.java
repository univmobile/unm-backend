package fr.univmobile.backend.it;

import static fr.univmobile.backend.core.impl.ConnectionType.MYSQL;
import static fr.univmobile.testutil.PropertiesUtils.getSettingsTestRefProperty;
import static fr.univmobile.testutil.PropertiesUtils.getTestProperty;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.it.commons.BrowserNames;
import fr.univmobile.it.commons.DeviceNames;
import fr.univmobile.it.commons.Scenario;
import fr.univmobile.it.commons.Scenarios;
import fr.univmobile.it.commons.SeleniumEnabledTest;

@Scenarios("Scénarios simples")
@DeviceNames("Firefox")
public class Scenarios001 extends SeleniumEnabledTest {

	@Before
	public void setUpData() throws Exception {

		// 0. ENVIRONMENT

		// "/tmp/unm-backend/dataDir"
		final String dataDir = TestBackend.readBackendAppDataDir(new File(
				"target", "unm-backend-app-noshib/WEB-INF/web.xml"));

		final Connection cxn = DriverManager.getConnection(
				getTestProperty("mysql.url"), //
				getTestProperty("mysql.username"), //
				getSettingsTestRefProperty("mysql.password.ref"));
		try {

			TestBackend.setUpData("001", new File(dataDir), MYSQL, cxn);

		} finally {
			cxn.close();
		}

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

			return getTestProperty("defaultBrowser");

		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final int PAUSE = 10000;

	@Scenario("Aller-retour sur la page « Ajouter un utilisateur »")
	@Test
	public void useradd_001() throws Exception {

		takeScreenshot("home.png");

		savePageSource("pageHome.html");
		elementById("button-myself").shouldBeVisible();
		elementById("div-myself").textShouldContain(
				"Votre connexion est authentifiée");

		elementById("button-myself").click();
		waitForElementById(PAUSE, "div-entered");
		takeScreenshot("entered.png");
		savePageSource("pageEntered.html");

		elementById("link-users").click();
		waitForElementById(PAUSE, "body-users");
		takeScreenshot("users.png");

		elementById("link-useradd").click();
		waitForElementById(PAUSE, "body-useradd");
		takeScreenshot("useradd.png");

		elementById("button-cancel").click();
		waitForElementById(PAUSE, "body-users");
		takeScreenshot("users2.png");
	}

	@Scenario("Voir les POIs de plus haut niveau")
	@Test
	public void geocampus_000() throws Exception {

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

	@Scenario("Voir les POIs de plus haut niveau")
	@Test
	public void pois_000() throws Exception {

		takeScreenshot("home.png");

		elementById("button-myself").click();

		pause(PAUSE);

		takeScreenshot("entered.png");

		elementById("link-pois").click();

		pause(PAUSE);

		takeScreenshot("pois.png");

		elementById("div-resultInfo").shouldBeVisible();

		elementById("link-poi-3792-name").click();

		pause(PAUSE);

		takeScreenshot("ucp.png");

		elementById("button-cancel").click();

		pause(PAUSE);

		takeScreenshot("pois2.png");

		elementById("div-resultInfo").shouldBeVisible();
	}

	@Scenario("Voir les commentaires d’un POI")
	@Test
	public void comments_poi_000() throws Exception {

		takeScreenshot("home.png");

		elementById("button-myself").click();

		pause(PAUSE);

		takeScreenshot("entered.png");

		elementById("link-pois").click();

		pause(PAUSE);

		takeScreenshot("pois.png");

		elementById("div-resultInfo").shouldBeVisible();

		elementById("link-poi-3792-name").click();

		pause(PAUSE);

		takeScreenshot("ucp.png");

		elementById("link-comments").click();

		pause(PAUSE);

		takeScreenshot("comments_poi3792.png");

		elementById("link-poi").click();

		pause(PAUSE);

		takeScreenshot("ucp2.png");

		elementById("link-comments").click();

		pause(PAUSE);

		takeScreenshot("comments2_poi3792.png");

		elementById("button-back").click();

		pause(PAUSE);

		takeScreenshot("ucp3.png");
	}

	@Scenario("Voir les commentaires les plus récents")
	@Test
	public void comments_000() throws Exception {

		takeScreenshot("home.png");

		elementById("button-myself").click();
		waitForElementById(10, "div-entered"); // pause(PAUSE);
		takeScreenshot("entered.png");

		elementById("link-comments").click();
		waitForElementById(10, "div-comments"); // pause(PAUSE);
		takeScreenshot("comments.png");

		elementById("link-adminMenu-home").click();
		waitForElementById(10, "ul-home-data"); // pause(PAUSE);
		takeScreenshot("home2.png");
	}

	@Scenario("Recherche dans les commentaires")
	@Test
	public void comments_001_search_application() throws Exception {

		takeScreenshot("home.png");

		elementById("button-myself").click();
		waitForElementById(10, "div-entered"); // pause(PAUSE);
		takeScreenshot("entered.png");

		elementById("link-comments").click();
		waitForElementById(10, "div-comments"); // pause(PAUSE);
		takeScreenshot("comments1-start.png");

		elementById("text-query").sendKeys("application");
		elementById("button-search").click();
		waitForElementById(10, "div-comments"); // pause(PAUSE);
		takeScreenshot("comments2-3_results.png");
		elementById("text-query").attrShouldEqualTo("value", "application");
		elementById("span-resultCount").textShouldEqualTo("3");

		elementById("text-query").sendKeys("une");
		elementById("button-search").click();
		waitForElementById(10, "div-comments"); // pause(PAUSE);
		takeScreenshot("comments3-1_result.png");
		elementById("text-query").attrShouldEqualTo("value", "une");
		elementById("span-resultCount").textShouldEqualTo("1");

		elementById("text-query").sendKeys("toto");
		elementById("button-search").click();
		waitForElementById(10, "div-comments"); // pause(PAUSE);
		takeScreenshot("comments4-no_result.png");
		elementById("text-query").attrShouldEqualTo("value", "toto");
		// elementById("span-resultCount").s
		elementById("div-noComments").textShouldContain("Aucun commentaire");

		elementById("text-query").sendKeys("");
		elementById("button-search").click();
		waitForElementById(10, "div-comments"); // pause(PAUSE);
		takeScreenshot("comments5-3_results.png");
		// elementById("text-query").attrShouldEqualTo("value", "");
		// elementById("span-resultCount").s
		elementById("span-resultCount").textShouldEqualTo("3");
	}

	@Scenario("Voir les commentaires d’un POI")
	@Test
	public void geocampus_comments000() throws Exception {

		takeScreenshot("home.png");

		elementById("button-myself").click();
		waitForElementById(10, "div-entered"); // pause(PAUSE);
		takeScreenshot("entered.png");

		elementById("link-geocampus").click();
		pause(PAUSE);
		takeScreenshot("pois.png");

		elementById("link-poi-3792").click();
		pause(PAUSE);
		takeScreenshot("ucp.png");

		final String labelledBy = elementById("li-left-bottom-tabs-comments")
				.attr("aria-labelledby");

		elementById(labelledBy).click();
		pause(PAUSE);
		takeScreenshot("ucp-details.png");
	}

	@Scenario("Admin Page: System")
	@Test
	public void system() throws Exception {

		takeScreenshot("home.png");

		elementById("button-myself").click();
		waitForElementById(10, "div-entered"); // pause(PAUSE);
		takeScreenshot("entered.png");

		elementById("link-system").click();
		pause(PAUSE);
		takeScreenshot("system.png");
	}

	@Scenario("Admin Page: Help")
	@Test
	public void help() throws Exception {

		takeScreenshot("home.png");

		elementById("button-myself").click();
		waitForElementById(10, "div-entered"); // pause(PAUSE);
		takeScreenshot("entered.png");

		elementById("link-help").click();
		pause(PAUSE);
		takeScreenshot("help.png");
	}

	@Scenario("Admin Page: Logs techniques")
	@Test
	public void logs() throws Exception {

		takeScreenshot("home.png");

		elementById("button-myself").click();
		waitForElementById(10, "div-entered"); // pause(PAUSE);
		takeScreenshot("entered.png");

		elementById("link-logs").click();
		pause(PAUSE);
		takeScreenshot("logs.png");
	}
}
