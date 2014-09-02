package fr.univmobile.commons.datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionManager;

public class MyRegionsTest {

	@Before
	public void setUp() throws Exception {

		final File originalDataDir = new File("src/test/data/myregions/001");

		final File tmpDataDir = new File("target/MyRegionsTest");

		if (tmpDataDir.isDirectory()) {
			FileUtils.forceDelete(tmpDataDir);
		}

		FileUtils.copyDirectory(originalDataDir, tmpDataDir);

		regions = BackendDataSourceFileSystem.newDataSource(
				MyRegionDataSource.class, tmpDataDir);
	}

	private MyRegionDataSource regions;

	@Test
	public void test_count() throws Exception {

		assertEquals(3, regions.getAllBy(String.class, "uid").size());
	}

	@Test
	public void test_getIleDeFrance() throws Exception {

		final MyRegion region = regions.getByUid("ile_de_france");

		assertEquals("ile_de_france", region.getUid());
		assertEquals("Île de France", region.getLabel());
		assertTrue(region.getUrl().startsWith("http://univmobile.vswip.com/"));
	}

	@Test
	public void test_setIleDeFranceLabel() throws Exception {

		final MyRegion region = regions.getByUid("ile_de_france");

		assertTrue(region.isNullParent());

		final MyRegionBuilder builder = regions.update(region);

		assertFalse(builder.isNullParent());

		assertEquals(region.getId(), builder.getParentId());

		assertEquals("Île de France", builder.getLabel());

		builder.setLabel("Région parisienne");

		assertEquals("Région parisienne", builder.getLabel());

		final TransactionManager tx = TransactionManager.getInstance();

		final Lock lock = tx.acquireLock(5000, "regions", "ile_de_france");

		lock.save(builder);

		lock.commit();

		regions.reload();

		final MyRegion region2 = regions.getByUid("ile_de_france");

		assertFalse(region2.isNullParent());

		assertEquals("Région parisienne", region2.getLabel());
	}

	@Test
	public void test_setIleDeFranceLabelInlineSave() throws Exception {

		final MyRegion region = regions.getByUid("ile_de_france");

		assertEquals("Île de France", region.getLabel());

		final TransactionManager tx = TransactionManager.getInstance();

		final Lock lock = tx.acquireLock(5000, "regions", "ile_de_france");

		lock.save(regions.update(region).setLabel("Région parisienne"));

		lock.commit();

		assertEquals("Île de France", region.getLabel());

		assertEquals("Région parisienne", regions.reload(region).getLabel());

		// assertEquals("Région parisienne", region.getLabel()); // CAREFUL

		assertEquals("Île de France", region.getLabel());
	}

	@Test
	public void test_setIleDeFranceCommit() throws Exception {

		final MyRegion region0 = regions.getByUid("ile_de_france");

		assertEquals("Île de France", region0.getLabel());

		final TransactionManager tx = TransactionManager.getInstance();

		final Lock lock = tx.acquireLock(5000, "regions", "ile_de_france");

		lock.save(regions.update(region0).setLabel("Région parisienne"));

		lock.commit();

		final MyRegion region1 = regions.getByUid("ile_de_france");

		assertEquals("Région parisienne", region1.getLabel());
	}

	@Test
	public void test_setIleDeFranceRollback() throws Exception {

		final MyRegion region0 = regions.getByUid("ile_de_france");

		assertEquals("Île de France", region0.getLabel());

		final TransactionManager tx = TransactionManager.getInstance();

		final Lock lock = tx.acquireLock(5000, "regions", "ile_de_france");

		lock.save(regions.update(region0).setLabel("Région parisienne"));

		lock.release();

		final MyRegion region1 = regions.getByUid("ile_de_france");

		assertEquals("Île de France", region1.getLabel());
	}
}
