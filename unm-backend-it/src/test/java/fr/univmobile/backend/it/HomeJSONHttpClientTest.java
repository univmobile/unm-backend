package fr.univmobile.backend.it;

import static fr.univmobile.backend.core.impl.ConnectionType.MYSQL;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.HomeClient;
import fr.univmobile.backend.client.HomeClientFromJSON;
import fr.univmobile.backend.client.http.HomeJSONHttpClient;
import fr.univmobile.backend.client.json.HomeJSONClient;
import fr.univmobile.testutil.PropertiesUtils;

public class HomeJSONHttpClientTest {

	@Before
	public void setUp() throws Exception {

		// 0. DATA

		// "/tmp/unm-backend/dataDir"
		final String dataDir = TestBackend.readBackendAppDataDir(new File(
				"target", "unm-backend-app-noshib/WEB-INF/web.xml"));

		final Connection cxn = DriverManager.getConnection(
				PropertiesUtils.getTestProperty("mysql.url"),
				PropertiesUtils.getTestProperty("mysql.username"),
				PropertiesUtils.getSettingsTestRefProperty("mysql.password.ref"));
		try {

			TestBackend.setUpData("001", new File(dataDir), MYSQL, cxn);

		} finally {
			cxn.close();
		}

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

		System.out.println("URL returned by the JSON client (jsonBaseURL): "
				+ jsonBaseURL);

		assertEquals(jsonURL, jsonBaseURL);
	}
}
