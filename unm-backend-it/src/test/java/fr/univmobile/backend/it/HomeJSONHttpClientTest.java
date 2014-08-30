package fr.univmobile.backend.it;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.HomeClient;
import fr.univmobile.backend.client.HomeClientFromJSON;
import fr.univmobile.backend.client.http.HomeJSONHttpClient;
import fr.univmobile.backend.client.json.HomeJSONClient;

public class HomeJSONHttpClientTest {

	@Before
	public void setUp() throws Exception {

		// 0. DATA

		// "/tmp/unm-backend/dataDir"
		final String dataDir = TestBackend.readBackendAppDataDir(new File(
				"target", "unm-backend-app-noshib/WEB-INF/web.xml"));

		TestBackend.setUpData("001", new File(dataDir));

		// 1. WEB

		// https://univmobile-dev.univ-paris1.fr/json/regions

		jsonURL =
		// "http://localhost:"
		// + PropertiesUtils.getTestProperty("tomcat.port")
		// + "/unm-backend
		TestBackend.readBackendAppBaseURL(new File("target",
				"unm-backend-app-noshib/WEB-INF/web.xml")) + "json";

		System.out.println("jsonURL: " + jsonURL);

		homeJSONClient = new HomeJSONHttpClient(jsonURL);

		final String logFile = TestBackend.readLog4jLogFile(new File("target",
				"unm-backend-app-noshib/WEB-INF/classes/log4j.xml"));

		System.out.println("Log file: " + logFile);
	}

	private HomeJSONClient homeJSONClient;

	private String jsonURL;

	@Test
	public void testGetHomeJSON() throws IOException {

		homeJSONClient.getHomeJSON();
	}

	@Test
	public void testGetHome() throws IOException {

		final HomeClient client = new HomeClientFromJSON(homeJSONClient);

		final String jsonBaseURL = client.getHome().getUrl();

		System.out.println("URL returned by the JSON client: " + jsonBaseURL);

		assertEquals(jsonURL, jsonBaseURL);
	}
}
