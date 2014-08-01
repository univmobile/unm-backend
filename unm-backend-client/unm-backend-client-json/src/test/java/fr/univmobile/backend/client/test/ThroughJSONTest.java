package fr.univmobile.backend.client.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
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

public class ThroughJSONTest {

	@Before
	public void setUp() throws Exception {

		final File originalDataDir = new File("src/test/data/regions/001");

		final File tmpDataDir = new File("target/RegionsTest");

		if (tmpDataDir.isDirectory()) {
			FileUtils.forceDelete(tmpDataDir);
		}

		FileUtils.copyDirectory(originalDataDir, tmpDataDir);

		final RegionDataSource dataSource = BackendDataSourceFileSystem
				.newDataSource(RegionDataSource.class, tmpDataDir);

		final RegionClientFromLocal regionClient = new RegionClientFromLocal(
				dataSource);

		final RegionJSONClientImpl regionJSONClient = new RegionJSONClientImpl(
				regionClient);

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
