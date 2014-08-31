package fr.univmobile.backend.client.http;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.it.TestBackend;

public class AbstractJSONHttpClientTest {

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

		final String logFile = TestBackend.readLog4jLogFile(new File("target",
				"unm-backend-app-noshib/WEB-INF/classes/log4j.xml"));

		System.out.println("Log file: " + logFile);
	}

	private String jsonURL;

	@Test
	public void testGetHomeJSON() throws IOException {

		assertJSON(jsonURL);
	}

	@Test
	public void testGetPoisJSON() throws IOException {

		assertJSON(jsonURL + "/pois");
	}

	@Test
	public void testGetCommentsJSON_poi3792() throws IOException {

		assertJSON(jsonURL + "/comments/poi3792");
	}

	@Test
	public void testGetRegionsJSON() throws IOException {

		assertJSON(jsonURL + "/regions");
	}

	@Test
	public void testGetRegionsJSON_ile_de_france() throws IOException {

		assertJSON(jsonURL + "/regions/ile_de_france");
	}

	@Test
	public void testGetRegionsJSON_bretagne() throws IOException {

		assertJSON(jsonURL + "/regions/bretagne");
	}

	private static void assertJSON(final String url) throws IOException {

		final String json = AbstractJSONHttpClient.wget(url);

		assertNotNull("JSON stream should not be null", json);

		assertNotEquals("JSON stream should not be empty", 0, json.length());

		assertTrue("JSON stream should start with \"{\": \"" + json.charAt(0)
				+ "\"", json.startsWith("{"));
	}
}
