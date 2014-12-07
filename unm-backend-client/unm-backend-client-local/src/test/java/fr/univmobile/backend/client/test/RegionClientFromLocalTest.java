package fr.univmobile.backend.client.test;

import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.RegionClientFromLocal;
import fr.univmobile.backend.client.University;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class RegionClientFromLocalTest {

	@Before
	public void setUp() throws Exception {

		final File tmpDataDir = copyDirectory(new File(
				"src/test/data/regions/001"), new File(
				"target/RegionClientFromLocalTest"));

		final RegionDataSource regions = BackendDataSourceFileSystem
				.newDataSource(RegionDataSource.class, tmpDataDir);

		final PoiDataSource poiDataSource = BackendDataSourceFileSystem
				.newDataSource(
						PoiDataSource.class,
						copyDirectory(new File("src/test/data/pois/001"),
								new File("target/PoiThroughJSONTest_pois")));

		client = new RegionClientFromLocal("http://toto", regions, poiDataSource);
	}

	private RegionClient client;

	@Test
	public void test_region() throws IOException {

		final Region[] regions = client.getRegions();

		assertEquals(3, regions.length);

		final Region region = regions[1];

		assertEquals("ile_de_france", region.getId());
		assertEquals("Île de France", region.getLabel());

		assertFalse(region.getUrl().contains("${baseURL}"));

		assertEquals(4715, region.getPoiCount());
		final String poisUrl = region.getPoisUrl();
		assertTrue(poisUrl, poisUrl.endsWith("ile_de_france/pois"));
	}

	@Test
	public void test_university_rennes2() throws IOException {

		final University[] universities = client
				.getUniversitiesByRegion("bretagne");

		final University university = universities[1];

		assertEquals("rennes2", university.getId());
		assertEquals("Université Rennes 2", university.getTitle());
	}

	@Test
	public void test_university_ucp() throws IOException {

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

		assertFalse(poisUrl.contains("${baseURL}"));
		assertFalse(configUrl.contains("${baseURL}"));

		assertEquals("https://shibbolethtest.ensiie.fr/idp/shibboleth",
				university.getShibbolethIdentityProvider());
	}
}
