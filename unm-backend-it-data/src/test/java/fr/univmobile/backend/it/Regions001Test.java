package fr.univmobile.backend.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.core.Region;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class Regions001Test {

	@Before
	public void setUp() throws Exception {

		final File originalDataDir = new File(
				"src/main/resources/data/001/regions");

		final File tmpDataDir = new File("target/RegionsTest");

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

		assertEquals(3, regions.getAllBy(String.class, "uid").size());
	}

	@Test
	public void test_ileDeFrance() throws Exception {

		final Region ile_de_france = regions.getByUid("ile_de_france");

		assertEquals("ile_de_france", ile_de_france.getUid());
		assertEquals("Île de France", ile_de_france.getLabel());
		assertTrue(ile_de_france.getUrl().startsWith(
				"${baseURL}/json/regions/"));
//				"http://univmobile.vswip.com/"));

		assertEquals(18, ile_de_france.sizeOfUniversities());
	}

	@Test
	public void test_bretagne() throws Exception {

		final Region bretagne = regions.getByUid("bretagne");

		assertEquals(4, bretagne.sizeOfUniversities());
	}

	@Test
	public void test_unrpcl() throws Exception {

		final Region unrpcl = regions.getByUid("unrpcl");

		assertEquals(5, unrpcl.sizeOfUniversities());
	}
}
