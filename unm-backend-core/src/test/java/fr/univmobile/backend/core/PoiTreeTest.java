package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class PoiTreeTest {

	@Before
	public final void setUp() throws Exception {

		final File tmpDataDir = new File("target/PoiTreeTest");

		if (tmpDataDir.isDirectory()) {
			FileUtils.forceDelete(tmpDataDir);
		}

		final File originalDataDir = new File("src/test/data/poitrees/002");

		FileUtils.copyDirectory(originalDataDir, tmpDataDir);

		final PoiTreeDataSource poiTrees = BackendDataSourceFileSystem
				.newDataSource(PoiTreeDataSource.class, tmpDataDir);

		poiTree = poiTrees.getByUid("poitree");
	}

	private PoiTree poiTree;

	@Test
	public final void test_sizeOfRoots() throws Exception {

		assertEquals(23, poiTree.getRoots().length);
	}

	@Test
	public final void test_countAllPois() throws Exception {

		assertEquals(4715, poiTree.sizeOfAllPois());
	}

	@Test
	public final void test_countAllPois_paris6() throws Exception {

		assertEquals(404, poiTree.sizeOfAllPoisByUniversityId("paris6"));
	}
}
