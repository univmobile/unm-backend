package fr.univmobile.backend.it;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.RegionClientFromJSON;
import fr.univmobile.backend.client.http.RegionJSONHttpClient;
import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.testutil.PropertiesUtils;

public class RegionJSONHttpClientTest {

	@Before
	public void setUp() throws Exception {

		// https://univmobile-dev.univ-paris1.fr/json/regions

		final String url = "http://localhost:"
				+ PropertiesUtils.getTestProperty("tomcat.port")
				+ "/unm-backend/json/regions";

		regionJSONClient = new RegionJSONHttpClient(url);
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
}
