package fr.univmobile.backend.client.test;

import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.ClientException;
import fr.univmobile.backend.client.ImageMap;
import fr.univmobile.backend.client.ImageMapClient;
import fr.univmobile.backend.client.ImageMapClientFromLocal;
import fr.univmobile.backend.client.ImageMapPoi;
import fr.univmobile.backend.client.PoiCategory;
import fr.univmobile.backend.core.ImageMapDataSource;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.ImageMap.PoiInfo;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class ImageMapClientFromLocalTest {

	@Before
	public void setUp() throws Exception {

		final ImageMapDataSource imageMapDataSource = BackendDataSourceFileSystem
				.newDataSource(
						ImageMapDataSource.class,
						copyDirectory(new File("src/test/data/imagemaps/004"),
								new File("target/ImageMapClientFromLocalTest_images")));

		final PoiDataSource poiDataSource = BackendDataSourceFileSystem
				.newDataSource(
						PoiDataSource.class,
						copyDirectory(
								new File("src/test/data/pois/003"),
								new File(
										"target/ImageMapClientFromLocalTest_pois")));

		client = new ImageMapClientFromLocal("http://toto/", imageMapDataSource, poiDataSource);
	}

	private ImageMapClient client;
	
	/**
	 * Test that we get correctly the Poi root category
	 * @throws IOException
	 */
	@Test
	public void test_poiCategoryRoot() throws IOException, ClientException {

		final ImageMap imageMap = client.getImageMap(1, 20105);

		assertEquals(1, imageMap.getId());
		assertEquals("Plan Campus XXX", imageMap.getName());
		assertEquals("Description Plan Campus XXX", imageMap.getDescription());
		assertEquals("http://univmobile-dev.univ-paris1.fr/image/plan/imagemap1.png", imageMap.getImageUrl());
		assertEquals(2, imageMap.getPois().length);
		assertNotNull(imageMap.getSelectedPoi());
		assertEquals(20105, imageMap.getSelectedPoi().getId());
		
		// We keep only the active categories
		Map<Integer, String> ids = new HashMap<Integer, String>();
		ids.put(20104, "12, 25");
		ids.put(20105, "16,26");

		for (ImageMapPoi poi : imageMap.getPois()) {
			assertTrue(ids.containsKey(poi.getId()));
			if (poi.getId() == 20104) {
				assertEquals("Bâtiment d’Orbigny", poi.getName());
				assertEquals("46.145811685373,-1.158322", poi.getCoordinates());
				assertEquals("46.145811685373", poi.getLatitude());
				assertEquals("-1.158322", poi.getLongitude());
				assertEquals("point", poi.getMarkerType());
				assertEquals(12, poi.getImageMapX());
				assertEquals(25, poi.getImageMapY());
			}
			if (poi.getId() == 20105) {
				assertEquals("Bâtiment Fourrier", poi.getName());
				assertEquals("46.146629332509,-1.1565953493", poi.getCoordinates());
				assertEquals("46.146629332509", poi.getLatitude());
				assertEquals("-1.1565953493", poi.getLongitude());
				assertEquals("point", poi.getMarkerType());
				assertEquals(16, poi.getImageMapX());
				assertEquals(26, poi.getImageMapY());
			}
			ids.remove(poi.getId());
		}
	}
}
