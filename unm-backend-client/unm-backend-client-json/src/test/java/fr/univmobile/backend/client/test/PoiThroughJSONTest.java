package fr.univmobile.backend.client.test;

import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.Poi;
import fr.univmobile.backend.client.PoiClient;
import fr.univmobile.backend.client.PoiClientFromJSON;
import fr.univmobile.backend.client.PoiClientFromLocal;
import fr.univmobile.backend.client.PoiGroup;
import fr.univmobile.backend.client.json.PoiJSONClientImpl;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class PoiThroughJSONTest {

	@Before
	public void setUp() throws Exception {

		final RegionDataSource regionDataSource = BackendDataSourceFileSystem
				.newDataSource(
						RegionDataSource.class,
						copyDirectory(new File("src/test/data/regions/001"),
								new File("target/PoiThroughJSONTest_regions")));

		final PoiDataSource poiDataSource = BackendDataSourceFileSystem
				.newDataSource(
						PoiDataSource.class,
						copyDirectory(new File("src/test/data/pois/001"),
								new File("target/PoiThroughJSONTest_pois")));

		final PoiTreeDataSource poitreeDataSource = BackendDataSourceFileSystem
				.newDataSource(
						PoiTreeDataSource.class,
						copyDirectory(new File("src/test/data/poitrees/001"),
								new File("target/PoiThroughJSONTest_poitrees")));

		final PoiClientFromLocal poiClient = new PoiClientFromLocal("(dummy baseURL)",
				poiDataSource, poitreeDataSource, regionDataSource);

		poiJSONClient = new PoiJSONClientImpl( poiClient);

		client = new PoiClientFromJSON(poiJSONClient);
	}

	private PoiJSONClientImpl poiJSONClient;
	private PoiClient client;

	@Test
	public void testThroughJSON_poiGroups() throws IOException {

		final PoiGroup[] groups = client.getPois();

		assertEquals(3, groups.length);

		for (final PoiGroup group : groups) {

			assertNotNull("All groups should be not null.", group);
		}

		final PoiGroup group1 = groups[1];

		assertEquals("Région : Île de France", group1.getGroupLabel());
	}

	@Test
	public void testThroughJSON_poi2() throws IOException {

		final PoiGroup[] groups = client.getPois();

		final Poi[] pois = groups[1].getPois();

		assertEquals(16, pois.length);

		for (final Poi poi : pois) {

			assertNotNull("All POIs should be not null.", poi);
		}

		final Poi poi = pois[2];

		assertEquals(2, poi.getId());
		assertEquals("Université Paris 13", poi.getName());
		assertEquals("Avenue Jean-Baptiste Clément 93430 Villetaneuse",
				poi.getAddress());
		assertEquals("48.956368610524,2.3387575149536", poi.getCoordinates());
		assertEquals("48.956368610524", poi.getLatitude()); // 48.96
		assertEquals("2.3387575149536", poi.getLongitude()); // 2.34
		assertEquals("Http://www.univ-paris13.fr/", poi.getUrl());
		assertEquals("0149403000", poi.getPhone());
		assertEquals("0149403893", poi.getFax());

		assertNull(poi.getEmail());
		assertNull(poi.getFloor());
		assertNull(poi.getImageUrl());
		assertNull(poi.getItinerary());
		assertNull(poi.getOpeningHours());

		assertEquals("green", poi.getMarkerType());
		assertEquals("C", poi.getMarkerIndex());
	}

	@Test
	public void testThroughJSON_poi20036() throws IOException {

		final PoiGroup[] groups = client.getPois();

		final Poi[] pois = groups[2].getPois();

		final Poi poi = pois[2];

		assertEquals(20036, poi.getId());

		assertEquals(
				"(dummy baseURL)/uploads/poi/c99abda0f5d42a24d0cf1ef0d0476b8b6ed4311a.png",
				poi.getImageUrl());
	}

	@Test
	public void testThroughJSON_noEmptyCoordinates() throws IOException {

		final String json = poiJSONClient.getPoisJSON();

		assertFalse(json.contains("\"coordinates\":\"\""));
	}

	@Test
	public void testThroughJSON_noUnfilteredUrl() throws IOException {

		final String json = poiJSONClient.getPoisJSON();

		assertFalse(json.contains("${baseURL}"));
	}

	@Test
	public void testThroughJSON_noBlankCoordinates() throws IOException {

		for (final PoiGroup group : client.getPois()) {

			for (final Poi poi : group.getPois()) {

				assertFalse(isBlank(poi.getCoordinates()));
			}
		}
	}

	@Test
	public void testThroughJSON_commentsUrls() throws Exception {

		for (final PoiGroup group : client.getPois()) {

			for (final Poi poi : group.getPois()) {

				final String commentsUrl = poi.getCommentsUrl();
				
				assertFalse(isBlank(commentsUrl));
			}
		}
	}
}
