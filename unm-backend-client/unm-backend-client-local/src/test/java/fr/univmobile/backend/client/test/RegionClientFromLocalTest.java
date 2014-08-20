package fr.univmobile.backend.client.test;

import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.RegionClientFromLocal;
import fr.univmobile.backend.client.University;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class RegionClientFromLocalTest {

	@Before
	public void setUp() throws Exception {

		final File tmpDataDir = copyDirectory(new File(
				"src/test/data/regions/001"), new File(
				"target/RegionClientFromLocalTest"));

		final RegionDataSource dataSource = BackendDataSourceFileSystem
				.newDataSource(RegionDataSource.class, tmpDataDir);

		client = new RegionClientFromLocal(dataSource);
	}

	private RegionClient client;

	@Test
	public void test_region() throws IOException {

		final Region[] regions = client.getRegions();

		assertEquals(3, regions.length);

		final Region region = regions[1];

		assertEquals("ile_de_france", region.getId());
		assertEquals("Île de France", region.getLabel());
	}

	@Test
	public void test_university() throws IOException {

		final University[] universities = client
				.getUniversitiesByRegion("bretagne");

		final University university = universities[1];

		assertEquals("rennes2", university.getId());
		assertEquals("Université Rennes 2", university.getTitle());
	}
}
