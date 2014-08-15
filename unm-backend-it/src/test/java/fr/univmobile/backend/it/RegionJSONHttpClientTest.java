package fr.univmobile.backend.it;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.RegionClientFromJSON;
import fr.univmobile.backend.client.University;
import fr.univmobile.backend.client.http.RegionJSONHttpClient;
import fr.univmobile.backend.client.json.RegionJSONClient;

public class RegionJSONHttpClientTest {

	@Before
	public void setUp() throws Exception {

		// 0. DATA

		// "/tmp/unm-backend/dataDir"
		final String dataDir = TestBackend.readBackendAppDataDir(new File(
				"target", "unm-backend-app-noshib/WEB-INF/web.xml"));

		TestBackend.setUpData("001", new File(dataDir));

		// 1. WEB

		// https://univmobile-dev.univ-paris1.fr/json/regions

		final String url =
		// "http://localhost:"
		// + PropertiesUtils.getTestProperty("tomcat.port")
		// + "/unm-backend
		TestBackend.readBackendAppBaseURL(new File("target",
				"unm-backend-app-noshib/WEB-INF/web.xml")) + "json/regions";

		System.out.println("url: " + url);

		regionJSONClient = new RegionJSONHttpClient(url);

		final String logFile = TestBackend.readLog4jLogFile(new File("target",
				"unm-backend-app-noshib/WEB-INF/classes/log4j.xml"));

		System.out.println("Log file: " + logFile);
	}

	private RegionJSONClient regionJSONClient;

	@Test
	public void testGetRegionsJSON() throws IOException {

		regionJSONClient.getRegionsJSON();
	}

	@Test
	public void testGetRegions() throws IOException {

		final RegionClient client = new RegionClientFromJSON(regionJSONClient);

		final Region[] regions = client.getRegions();

		assertEquals(3, regions.length);

		assertEquals("Île de France", regions[1].getLabel());
	}

	@Test
	public void testGetUniversities_bretagne() throws IOException {

		final RegionClient client = new RegionClientFromJSON(regionJSONClient);

		final University[] universities = client
				.getUniversitiesByRegion("bretagne");

		assertEquals(4, universities.length);

		assertEquals("Université Rennes 2", universities[1].getTitle());
	}

	@Test
	public void testGetUniversities_ile_de_france() throws IOException {

		final RegionClient client = new RegionClientFromJSON(regionJSONClient);

		final University[] universities = client
				.getUniversitiesByRegion("ile_de_france");

		assertEquals(18, universities.length);

		assertEquals("Cergy-Pontoise", universities[1].getTitle());
	}

	@Test
	public void testGetUniversities_unrpcl() throws IOException {

		final RegionClient client = new RegionClientFromJSON(regionJSONClient);

		final University[] universities = client
				.getUniversitiesByRegion("unrpcl");

		assertEquals(5, universities.length);

		assertEquals("ISAE-ENSMA", universities[1].getTitle());
	}
}
