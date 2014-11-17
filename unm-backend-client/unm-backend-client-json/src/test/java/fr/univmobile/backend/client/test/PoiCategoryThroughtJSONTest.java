package fr.univmobile.backend.client.test;

import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.ClientException;
import fr.univmobile.backend.client.PoiCategory;
import fr.univmobile.backend.client.PoiCategoryClient;
import fr.univmobile.backend.client.PoiCategoryClientFromJSON;
import fr.univmobile.backend.client.PoiCategoryClientFromLocal;
import fr.univmobile.backend.client.PoiClientFromJSON;
import fr.univmobile.backend.client.json.PoiCategoryJSONClient;
import fr.univmobile.backend.client.json.PoiCategoryJSONClientImpl;
import fr.univmobile.backend.core.PoiCategoryDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class PoiCategoryThroughtJSONTest {

	@Before
	public void setUp() throws Exception {

		final PoiCategoryDataSource poiCategoryDataSource = BackendDataSourceFileSystem
				.newDataSource(
						PoiCategoryDataSource.class,
						copyDirectory(new File("src/test/data/poiscategories/004"),
								new File("target/PoiCategoryClientFromLocalTest_pois")));

		final RegionDataSource regionDataSource = BackendDataSourceFileSystem
				.newDataSource(
						RegionDataSource.class,
						copyDirectory(
								new File("src/test/data/regions/001"),
								new File(
										"target/PoiClientFromLocalTest_regions")));
		
		client = new PoiCategoryClientFromLocal("(dummy baseURL)", poiCategoryDataSource, regionDataSource);
		
		poiCategoryJSONClient = new PoiCategoryJSONClientImpl(client);
		
		client = new PoiCategoryClientFromJSON(poiCategoryJSONClient);
	}

	private PoiCategoryJSONClient poiCategoryJSONClient;
	private PoiCategoryClient client;
	
	@Test
	public void test_UniversityCategories() throws IOException, ClientException {
		PoiCategory rootUniversityCategory = client.getPoiCategory(99);
		
		rootUniversityCategory.getChildCategories();
		
		assertEquals("Catégories universitaires", rootUniversityCategory.getName());
		assertEquals("Catégorie racine de toutes les catégories de POIs des universités. Catégorie jamais visible.", rootUniversityCategory.getDescription());
		assertEquals("http://admin.univmobile.fr/uploads/images/poitype_icon_batiment_1304005503.png", rootUniversityCategory.getCursorUrl());
		assertEquals(17, rootUniversityCategory.getChildCategories().length);
		
		// We keep only the active categories
		Map<Integer, String> ids = new HashMap<Integer, String>();
		ids.put(1, "Plan");
		ids.put(3, "Amphithéâtre");
		ids.put(4, "Restauration Universitaire");
		ids.put(7, "Relais handicap");
		ids.put(8, "Scolarité et Inscription");
		ids.put(9, "Bibliothèque Universitaire");
		ids.put(11, "Orientation et Insertion professionnelle");
		ids.put(12, "Santé et Social");
		ids.put(13, "Equipements sportifs et service des sports");
		ids.put(14, "Recherche");
		ids.put(15, "Association");
		ids.put(16, "Culture");
		ids.put(17, "Formation");
		ids.put(21, "Unité de Formation et de Recherche - Faculté");
		ids.put(22, "Institut et École");
		ids.put(23, "Résidence Universitaire");
		ids.put(24, "Salle");

		for (PoiCategory poiCategory : rootUniversityCategory.getChildCategories()) {
			assertTrue(ids.containsKey(poiCategory.getId()));
			assertEquals(ids.get(poiCategory.getId()), poiCategory.getName());
			ids.remove(poiCategory.getId());
		}

	}
	
}
