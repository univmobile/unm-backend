// Author: Mauricio

package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionManager;

public class CreateImageMapsTest extends AbstractImageMapsTest {

	public CreateImageMapsTest() {
		super(new File("src/test/data/imagemaps/005"));
	}

	@Before
	public void setUp() throws Exception {

		imagemaps = BackendDataSourceFileSystem.newDataSource(
				ImageMapDataSource.class, originalDataDir);

		pois = BackendDataSourceFileSystem.newDataSource(PoiDataSource.class,
				new File("/home/mauricio/var/univmobile/pois/"));
	}

	private PoiDataSource pois;
	private ImageMapDataSource imagemaps;

	@Test
	public void test_create() throws Exception {

		final ImageMapBuilder imagemap = imagemaps.create();

		imagemap.setAuthorName("mauricio");
		assertEquals("mauricio", imagemap.getAuthorName());

		imagemap.setUid(1);
		imagemap.setName("test_image_map");
		assertEquals(1, imagemap.getUid());
		assertEquals("test_image_map", imagemap.getName());

		imagemap.setDescription("Description..");
		imagemap.setImageUrl("http://test_url.com");
		assertEquals("Description..", imagemap.getDescription());
		assertEquals("http://test_url.com", imagemap.getImageUrl());

		imagemap.setActive("true");
		assertEquals("true", imagemap.getActive());

		TransactionManager tx = TransactionManager.getInstance();

		Integer uid = imagemap.getUid();

		final Lock lock = tx.acquireLock(5000, originalDataDir.toString(), uid);
		try {

			lock.save(imagemap);

			lock.commit();

		} finally {
			lock.release();
		}
	}

	@Test
	public void test_add_poi() throws Exception {

		// TODO

	}

}
