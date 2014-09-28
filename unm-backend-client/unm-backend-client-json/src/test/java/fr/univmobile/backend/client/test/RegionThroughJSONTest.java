package fr.univmobile.backend.client.test;

import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.RegionClientFromJSON;
import fr.univmobile.backend.client.RegionClientFromLocal;
import fr.univmobile.backend.client.RegionsUtils;
import fr.univmobile.backend.client.University;
import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.backend.client.json.RegionJSONClientImpl;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class RegionThroughJSONTest {

	@Before
	public void setUp() throws Exception {

		final RegionDataSource regions = BackendDataSourceFileSystem
				.newDataSource(
						RegionDataSource.class,
						copyDirectory(new File("src/test/data/regions/001"),
								new File("target/RegionThroughJSONTest")));

		final PoiTreeDataSource poitrees = BackendDataSourceFileSystem
				.newDataSource(
						PoiTreeDataSource.class,
						copyDirectory(new File("src/test/data/poitrees/001"),
								new File("target/PoiThroughJSONTest_poitrees")));

		final RegionClient regionClient = new RegionClientFromLocal(
				"(dummy baseURL)", regions, poitrees);

		regionJSONClient = new RegionJSONClientImpl(regionClient);

		client = new RegionClientFromJSON(regionJSONClient);
	}

	private RegionJSONClient regionJSONClient;
	private RegionClient client;

	@Test
	public void testThroughJSON_region() throws IOException {

		final Region[] regions = client.getRegions();

		assertEquals(3, regions.length);

		final Region region = regions[1];

		assertEquals("ile_de_france", region.getId());
		assertEquals("Île de France", region.getLabel());

		assertEquals(4715, region.getPoiCount());

		final String poisUrl = region.getPoisUrl();
		assertTrue(poisUrl, poisUrl.endsWith("ile_de_france/pois"));
	}

	@Test
	public void testThroughJSON_university_rennes2() throws IOException {

		final University[] universities = client
				.getUniversitiesByRegion("bretagne");

		final University university = universities[1];

		assertEquals("rennes2", university.getId());
		assertEquals("Université Rennes 2", university.getTitle());
	}

	@Test
	public void testThroughJSON_university_ucp() throws IOException {

		final University[] universities = client
				.getUniversitiesByRegion("ile_de_france");

		final University university = universities[3];

		assertEquals("ensiie", university.getId());
		assertEquals("ENSIIE", university.getTitle());
		assertEquals(27, university.getPoiCount());
		final String poisUrl = university.getPoisUrl();
		assertTrue(poisUrl, poisUrl.endsWith("ile_de_france/ensiie/pois"));

		final String configUrl = university.getConfigUrl();
		assertTrue(configUrl, configUrl.endsWith("ile_de_france/ensiie"));

		assertEquals("https://shibbolethtest.ensiie.fr/idp/shibboleth",
				university.getShibbolethIdentityProvider());
	}

	@Test
	public void test_regionId_regionLabel() throws Exception {

		final String json = regionJSONClient
				.getUniversitiesJSONByRegion("ile_de_france");

		// assertTrue(json.contains("\"id\":\"ile_de_france\""));
		// assertTrue(json.contains("\"label\":\"Île de France\""));
		assertTrue(json.startsWith("{\"id\":\"ile_de_france\","
				+ "\"label\":\"Île de France\""));
	}

	@Test
	public void test_unmarshall_ShibbolethIdentityProvider() throws Exception {

		final String jsonRegions = FileUtils.readFileToString(new File(
				"src/test/json/regions-001.json"), UTF_8);

		final String jsonUniversities = FileUtils.readFileToString(new File(
				"src/test/json/ile_de_france-001.json"), UTF_8);

		final RegionJSONClient regionJSONClient = mock(RegionJSONClient.class);

		when(regionJSONClient.getRegionsJSON()).thenReturn(jsonRegions);
		when(regionJSONClient.getUniversitiesJSONByRegion("ile_de_france"))
				.thenReturn(jsonUniversities);

		final RegionClient client = new RegionClientFromJSON(regionJSONClient);

		final University paris1=RegionsUtils.getUniversityById(client, "paris1");
		
		assertEquals("Paris 1 Panthéon-Sorbonne",paris1.getTitle());
		assertNull(paris1.getShibbolethIdentityProvider());

		final University ensiie=RegionsUtils.getUniversityById(client, "ensiie");
		
		assertEquals("ENSIIE",ensiie.getTitle());
		assertEquals("https://shibbolethtest.ensiie.fr/idp/shibboleth",ensiie.getShibbolethIdentityProvider());
	}
}
