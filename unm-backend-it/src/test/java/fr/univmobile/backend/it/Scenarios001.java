package fr.univmobile.backend.it;

import java.io.IOException;

import org.junit.Test;

import fr.univmobile.it.commons.BrowserNames;
import fr.univmobile.it.commons.Scenario;
import fr.univmobile.it.commons.Scenarios;
import fr.univmobile.it.commons.SeleniumEnabledTest;
import fr.univmobile.testutil.PropertiesUtils;

@Scenarios("Scénarios simples")
public class Scenarios001 extends SeleniumEnabledTest {

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
}
