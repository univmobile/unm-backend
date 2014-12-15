// Author: Mauricio

package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.univmobile.backend.core.ImageMap.PoiInfo;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionManager;

public class CreateImageMapsTest extends AbstractImageMapsTest {

	private static final String NSURI = "http://www.w3.org/2005/Atom";

	public CreateImageMapsTest() {
		super(new File("src/test/data/imagemaps/005"));
	}

	@Before
	public void setUp() throws Exception {

		imagemaps = BackendDataSourceFileSystem.newDataSource(
				ImageMapDataSource.class, originalDataDir);
	}

	private ImageMapDataSource imagemaps;

	public void addPoi(Document d, Element eParent, Integer uid,
			boolean active, String coord) {

		final Element ePoi = d.createElement("poi");

		ePoi.setAttribute("uid", String.valueOf(uid));
		if (active)
			ePoi.setAttribute("active", "true");
		else
			ePoi.setAttribute("active", "false");
		if (coord != null)
			if (!coord.trim().isEmpty())
				ePoi.setAttribute("coordinates", coord);

		eParent.appendChild(ePoi);
	}

	@Test
	public void test_create() throws Exception {

		Document document = imagemaps.getDocument();

		final Element rootElement = document.getDocumentElement();

		final Element element = document.createElementNS(NSURI, "atom:content");
		rootElement.appendChild(element);

		addPoi(document, element, 333, true, "10, 10");
		addPoi(document, element, 222, true, "10, 10");

		final ImageMapBuilder imagemap = imagemaps.createBuilder(document);

		imagemap.setAuthorName("mauricio");
		assertEquals("mauricio", imagemap.getAuthorName());

		imagemap.setUid(5);
		imagemap.setName("test_image_map");
		assertEquals(5, imagemap.getUid());
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
	public void test_read() {

		ImageMap imagemap = imagemaps.getByUid(5);

		for (PoiInfo pi : imagemap.getPoiInfos()) {
			System.out.println(pi.getId());
		}
	}
}
