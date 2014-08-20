package fr.univmobile.backend.client.test;

import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.RegionClientFromJSON;
import fr.univmobile.backend.client.RegionClientFromLocal;
import fr.univmobile.backend.client.University;
import fr.univmobile.backend.client.json.RegionJSONClientImpl;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class RegionThroughJSONTest {

	@Before
	public void setUp() throws Exception {

		final RegionDataSource dataSource = BackendDataSourceFileSystem
				.newDataSource(
						RegionDataSource.class,
						copyDirectory(new File("src/test/data/regions/001"),
								new File("target/RegionThroughJSONTest")));

		final RegionClientFromLocal regionClient = new RegionClientFromLocal(
				dataSource);

		final RegionJSONClientImpl regionJSONClient = new RegionJSONClientImpl(
				"(dummy baseURL)", regionClient);

		client = new RegionClientFromJSON(regionJSONClient);
	}

	private RegionClient client;

	@Test
	public void testThroughJSON_region() throws IOException {

		final Region[] regions = client.getRegions();

		assertEquals(3, regions.length);

		final Region region = regions[1];

		assertEquals("ile_de_france", region.getId());
		assertEquals("Île de France", region.getLabel());
	}

	@Test
	public void testThroughJSON_university() throws IOException {

		final University[] universities = client
				.getUniversitiesByRegion("bretagne");

		final University university = universities[1];

		assertEquals("rennes2", university.getId());
		assertEquals("Université Rennes 2", university.getTitle());
	}
}
