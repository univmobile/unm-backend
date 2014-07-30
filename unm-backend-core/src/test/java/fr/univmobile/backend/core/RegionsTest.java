package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class RegionsTest {

	@Before
	public void setUp() throws Exception {

		final File originalDataDir = new File("src/test/data/regions/001");

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

		assertEquals(3, regions.getAllBy("uid").size());
	}

	@Test
	public void test_getIleDeFrance() throws Exception {

		final Region region = regions.getByUid("ile_de_france");

		assertEquals("ile_de_france", region.getUid());
		assertEquals("Île de France", region.getLabel());
		assertTrue(region.getUrl().startsWith("http://univmobile.vswip.com/"));
	}

	@Test
	public void test_setIleDeFranceLabel() throws Exception {

		final Region region = regions.getByUid("ile_de_france");

		assertTrue(region.isNullParent());

		final RegionBuilder builder = regions.update(region);

		assertFalse(builder.isNullParent());

		assertEquals(region.getId(), builder.getParentId());

		assertEquals("Île de France", builder.getLabel());

		builder.setLabel("Région parisienne");

		assertEquals("Région parisienne", builder.getLabel());

		builder.save();

		regions.reload();

		final Region region2 = regions.getByUid("ile_de_france");

		assertFalse(region2.isNullParent());

		assertEquals("Région parisienne", region2.getLabel());
	}

	@Test
	public void test_setIleDeFranceLabelInlineSave() throws Exception {

		final Region region = regions.getByUid("ile_de_france");

		regions.update(region).setLabel("Région parisienne").save();

		assertEquals("Région parisienne", region.getLabel());
	}
}
