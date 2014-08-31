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
import fr.univmobile.backend.client.PoiClientFromLocal;
import fr.univmobile.backend.client.PoiGroup;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class PoiClientFromLocalTest {

	@Before
	public void setUp() throws Exception {

		final PoiTreeDataSource poitreeDataSource = BackendDataSourceFileSystem
				.newDataSource(
						PoiTreeDataSource.class,
						copyDirectory(
								new File("src/test/data/poitrees/001"),
								new File(
										"target/PoiClientFromLocalTest_poitrees")));

		final PoiDataSource poiDataSource = BackendDataSourceFileSystem
				.newDataSource(
						PoiDataSource.class,
						copyDirectory(new File("src/test/data/pois/001"),
								new File("target/PoiClientFromLocalTest_pois")));

		final RegionDataSource regionDataSource = BackendDataSourceFileSystem
				.newDataSource(
						RegionDataSource.class,
						copyDirectory(
								new File("src/test/data/regions/001"),
								new File(
										"target/PoiClientFromLocalTest_regions")));

		client = new PoiClientFromLocal("http://toto/", poiDataSource,
				poitreeDataSource, regionDataSource);
	}

	private PoiClient client;

	@Test
	public void test_poiGroups() throws IOException {

		final PoiGroup[] groups = client.getPois();

		assertEquals(3, groups.length);

		for (final PoiGroup group : groups) {

			assertNotNull("All groups should be not null.", group);
		}

		final PoiGroup group1 = groups[1];

		assertEquals("Région : Île de France", group1.getGroupLabel());
	}

	@Test
	public void test_poi2() throws IOException {

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
	public void test_poi20036() throws IOException {

		final PoiGroup[] groups = client.getPois();

		final Poi[] pois = groups[2].getPois();

		final Poi poi = pois[2];

		assertEquals(20036, poi.getId());
		assertEquals("Université de Limoges", poi.getName());

		assertEquals(
				"http://toto/uploads/poi/c99abda0f5d42a24d0cf1ef0d0476b8b6ed4311a.png",
				poi.getImageUrl());
	}

	@Test
	public void test_poi4() throws IOException {

		final PoiGroup[] groups = client.getPois();

		final Poi[] pois = groups[1].getPois();

		final Poi poi = pois[4];

		assertEquals(4, poi.getId());
		assertNull(poi.getAddress());
	}

	@Test
	public void test_noBlankCoordinates() throws IOException {

		for (final PoiGroup group : client.getPois()) {

			for (final Poi poi : group.getPois()) {

				assertFalse(isBlank(poi.getCoordinates()));
			}
		}
	}

	@Test
	public void test_noBlankCommentsUrl() throws IOException {

		for (final PoiGroup group : client.getPois()) {

			for (final Poi poi : group.getPois()) {

				assertFalse(isBlank(poi.getCommentsUrl()));
			}
		}
	}

	@Test
	public void test_unfilteredCommentsUrl() throws IOException {

		for (final PoiGroup group : client.getPois()) {

			for (final Poi poi : group.getPois()) {

				final String commentsUrl = poi.getCommentsUrl();

				assertFalse(commentsUrl.contains("${baseURL}"));
			}
		}
	}

	@Test
	public void test_getUniversityIds_fromPoi1164() throws Exception {

		final Poi poi = client.getPoi(1164);

		assertEquals(1164, poi.getId());

		final String[] universityIds = poi.getUniversityIds();

		assertEquals(1, universityIds.length);

		assertEquals("paris8", universityIds[0]);
	}

	@Test
	public void test_getUniversityIds_fromPoiGroups() throws Exception {
		
		final Poi poi = client.getPois()[1].getPois()[10];

		assertEquals(1164, poi.getId());

		final String[] universityIds = poi.getUniversityIds();

		assertEquals(1, universityIds.length);

		assertEquals("paris8", universityIds[0]);
	}
}
