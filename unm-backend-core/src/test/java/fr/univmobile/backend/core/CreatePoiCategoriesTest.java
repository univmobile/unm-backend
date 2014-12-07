// Author: Mauricio

package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionManager;

public class CreatePoiCategoriesTest extends AbstractPoisCategoriesTest {

	public CreatePoiCategoriesTest() {
		super(new File("src/test/data/poiscategories/005"));
	}

	@Before
	public void setUp() throws Exception {

		poicategories = BackendDataSourceFileSystem.newDataSource(
				PoiCategoryDataSource.class, originalDataDir);
	}

	private PoiCategoryDataSource poicategories;

	@Test
	public void test_create() throws Exception {

		final PoiCategoryBuilder poicategory = poicategories.create();

		poicategory.setUid(190);

		assertEquals(190, poicategory.getUid());

		poicategory.setAuthorName("mauricio").setTitle("mauricio");

		assertEquals("mauricio", poicategory.getAuthorName());
		assertEquals("mauricio", poicategory.getTitle());

		poicategory.setParentUid(15).setExternalUid(25);

		assertEquals(15, poicategory.getParentUid());
		assertEquals(25, poicategory.getExternalUid());

		poicategory.setName("Test catégorie").setDescription(
				"Une catégorie importante");

		assertEquals("Test catégorie", poicategory.getName());
		assertEquals("Une catégorie importante", poicategory.getDescription());

		TransactionManager tx = TransactionManager.getInstance();

		Integer uid = poicategory.getUid();

		final Lock lock = tx.acquireLock(5000, originalDataDir.toString(), uid);
		try {

			lock.save(poicategory);

			lock.commit();

		} finally {
			lock.release();
		}
	}

	@Test
	public void test_exist1() throws Exception {

		assertEquals(1, poicategories.getAllBy(Integer.class, "uid").size());

	}

	@Test
	public void test_modify_name() throws Exception {

		final PoiCategoryBuilder poicategory = poicategories.create();

		PoiCategory pc = poicategories.getByUid(190);
		assertEquals("Test catégorie", pc.getName());

		poicategory.setAuthorName(pc.getAuthorName());
		poicategory.setTitle(pc.getTitle());

		poicategory.setUid(pc.getUid());
		poicategory.setParentUid(pc.getParentUid());
		poicategory.setExternalUid(pc.getExternalUid());
		poicategory.setDescription(pc.getDescription());
		poicategory.setActive(pc.getActive());

		poicategory.setName("Test catégorie modifié");

		TransactionManager tx = TransactionManager.getInstance();

		Integer uid = poicategory.getUid();

		final Lock lock = tx.acquireLock(5000, originalDataDir.toString(), uid);
		try {

			lock.save(poicategory);

			lock.commit();

		} finally {
			lock.release();
		}

		PoiCategory pcModified = poicategories.getByUid(190);

		assertEquals("Test catégorie modifié", pcModified.getName());
	}

}
