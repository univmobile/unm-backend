package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class PoisCategories004Test extends AbstractPoisCategoriesTest {

	public PoisCategories004Test() {
		super(new File("src/test/data/poiscategories/004"));
	}

	@Test
	public void testCount() throws Exception {

		assertEquals(20, poisCategories.getAllByInt("uid").size());
	}
	
	@Test
	public final void test_3_Amphiteatre() throws Exception {

		final PoiCategory amphi = poisCategories.getByUid(3);
		
		assertEquals(3, amphi.getUid());
		assertEquals("Amphithéâtre", amphi.getName());
		assertEquals("", amphi.getDescription());
		assertEquals("http://admin.univmobile.fr/uploads/images/poitype_icon_amphitheatre_1322666797.png", amphi.getCursorUrl());
		assertEquals(99, amphi.getParentUid());
		int[] expectedChildren = {};
		assertArrayEquals(expectedChildren, amphi.getChildren());
		assertTrue(amphi.isActive());
		assertEquals(0, amphi.getExternalUid());
		
		
	}

	@Test
	public final void test_99_RootUniversite() throws Exception {

		final PoiCategory amphi = poisCategories.getByUid(99);
		
		assertEquals(99, amphi.getUid());
		assertEquals("Catégories universitaires", amphi.getName());
		assertEquals("Catégorie racine de toutes les catégories de POIs des universités. Catégorie jamais visible.", amphi.getDescription());
		assertEquals("http://admin.univmobile.fr/uploads/images/poitype_icon_batiment_1304005503.png", amphi.getCursorUrl());
		assertEquals(0, amphi.getParentUid());
		int[] expectedChildren = { 1, 3, 4, 7, 8, 9, 11, 12, 13, 14, 15, 16, 17, 21, 22, 23, 24};
		assertArrayEquals(expectedChildren, amphi.getChildren());
		assertTrue(amphi.isActive());
		assertEquals(0, amphi.getExternalUid());
		
	}

	@Test
	public final void test_20_Polygone() throws Exception {

		final PoiCategory amphi = poisCategories.getByUid(20);
		
		assertEquals(20, amphi.getUid());
		assertEquals("Polygone", amphi.getName());
		assertNull(amphi.getDescription());
		assertNull(amphi.getCursorUrl());
		assertEquals(99, amphi.getParentUid());
		int[] expectedChildren = {};
		assertArrayEquals(expectedChildren, amphi.getChildren());
		assertFalse(amphi.isActive());
		assertEquals(0, amphi.getExternalUid());
		
	}

	/**
	 * Test une methode de recherche d'elements renvoyant une liste, via la recuperation de tous
	 * les enfants directs de l'universite de la Rochelle 
	 * @throws Exception
	 */
	@Test
	public void test_RootUniversiteChildren() throws Exception {
		final List<PoiCategory> ul = poisCategories.getByParentUid(99);
		assertNotNull(ul);
		Map<Integer, String> ids = new HashMap<Integer, String>();
		ids.put(1, "Plan");
		ids.put(3, "Amphithéâtre");
		ids.put(4, "Restauration Universitaire");
		ids.put(7, "Relais handicap");
		ids.put(8, "Scolarité et Inscription");
		ids.put(9, "Bibliothèque Universitaire");
		ids.put(10, "Accès et Adresses");
		ids.put(11, "Orientation et Insertion professionnelle");
		ids.put(12, "Santé et Social");
		ids.put(13, "Equipements sportifs et service des sports");
		ids.put(14, "Recherche");
		ids.put(15, "Association");
		ids.put(16, "Culture");
		ids.put(17, "Formation");
		ids.put(20, "Polygone");
		ids.put(21, "Unité de Formation et de Recherche - Faculté");
		ids.put(22, "Institut et École");
		ids.put(23, "Résidence Universitaire");
		ids.put(24, "Salle");
		
		assertEquals(19, ul.size());
		for (PoiCategory poiCategory : ul) {
			assertTrue(ids.containsKey(poiCategory.getUid()));
			assertEquals(ids.get(poiCategory.getUid()), poiCategory.getName());
			ids.remove(poiCategory.getUid());
		}
	}

}
