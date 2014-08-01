package fr.univmobile.backend.it;

import org.junit.Test;

import fr.univmobile.it.commons.DeviceNames;
import fr.univmobile.it.commons.Scenario;
import fr.univmobile.it.commons.Scenarios;
import fr.univmobile.it.commons.SeleniumEnabledTest;

@Scenarios("Scénarios simples")
@DeviceNames({ "*firefox", "*safari" })
public class Scenarios001 extends SeleniumEnabledTest {

	// / @Override
	// /public String getDefaultBrowser() {
	// /
	// /return "*custom /usr/bin/chromium";
	// /}

	private static final int PAUSE = 2000;

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

		/*
		 * elementById("link-useradd").click();
		 * 
		 * pause(PAUSE);
		 * 
		 * takeScreenshot("useradd.png");
		 * 
		 * elementById("button-cancel").click();
		 * 
		 * pause(PAUSE);
		 * 
		 * takeScreenshot("home2.png");
		 */
	}
}
