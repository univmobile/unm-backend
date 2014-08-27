package fr.univmobile.commons.datasource;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class MyPoisTest {

	@Before
	public void setUp() throws Exception {

		final File originalDataDir = new File("src/test/data/mypois/001");

		final File tmpDataDir = new File("target/MyPoisTest");

		if (tmpDataDir.isDirectory()) {
			FileUtils.forceDelete(tmpDataDir);
		}

		FileUtils.copyDirectory(originalDataDir, tmpDataDir);

		pois = BackendDataSourceFileSystem.newDataSource(
				MyPoiDataSource.class, tmpDataDir);
	}

	private MyPoiDataSource pois;

	@Test
	public void test_count() throws Exception {

		assertEquals(1, pois.getAllBy(Integer.class, "uid").size());
	}

	@Test
	public void test_getByUid1() throws Exception {

		final MyPoi poi = pois.getByUid(1);

		assertEquals("paris1", poi.getUniversityIds()[0]);
	}
}
