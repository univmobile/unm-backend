package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class UniversitiesTest {

	@Before
	public void setUp() throws Exception {

		final File originalDataDir = new File("src/test/data/regions/001");

		final File tmpDataDir = new File("target/UniversitiesTest");

		if (tmpDataDir.isDirectory()) {
			FileUtils.forceDelete(tmpDataDir);
		}

		FileUtils.copyDirectory(originalDataDir, tmpDataDir);

		regions = BackendDataSourceFileSystem.newDataSource(
				RegionDataSource.class, tmpDataDir);
	}

	private RegionDataSource regions;

	@Test
	public void test_count() throws Exception {

		final Region bretagne = regions.getByUid("bretagne");

		assertEquals(4, bretagne.sizeOfUniversities());
	}

	@Test
	public void test_getAttributes() throws Exception {

		final Region bretagne = regions.getByUid("bretagne");

		final University university = bretagne.getUniversities()[1];

		assertEquals("rennes1", university.getId());
		assertEquals("Université de Rennes 1", university.getTitle());
	}
}
