package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import fr.univmobile.backend.core.Poi.AttachmentType;

public class Pois003Test extends AbstractPoisTest {

	public Pois003Test() {

		super(new File("src/test/data/pois/003"));
	}

	@Test
	public void testCount() throws Exception {

		assertEquals(277, pois.getAllByInt("uid").size());
	}

	@Test
	public final void test_181_ISAE_ENSMA_fromJson() throws Exception {

		final Poi ensma = pois.getByUid(20181);

		assertEquals(20181, ensma.getUid());
		assertEquals("ensma", ensma.getUniversities()[0]);
		assertEquals(1, ensma.getPoiTypeIds()[0]);
		assertEquals("Université", ensma.getPoiTypeLabels()[0]);
		assertEquals(1, ensma.getPoiCategoryIds()[0]);
		assertEquals("Plans", ensma.getPoiCategoryLabels()[0]);
		assertEquals("46.660953672573,0.36190509", ensma.getCoordinates());
		assertEquals("ISAE-ENSMA", ensma.getTitle());
		assertTrue(ensma.getDescription().contains("Ecole Nationale Sup"));
		final Poi.Address address = ensma.getAddresses()[0];
		assertTrue(address.getFullAddress().contains("Teleport 2"));
		assertEquals("05 49 49 80 80", ensma.getPhones()[0]);
		assertEquals("05 49 49 80 00", ensma.getFaxes()[0]);
		assertEquals("contact@ensma.fr", ensma.getEmails()[0]);
		assertEquals("http://www.ensma.fr", ensma.getUrls()[0]);
		assertTrue(ensma.isNullParentUid());
		assertTrue(ensma.isActive());
		// opening hours
		// itinerary
		assertEquals(0, ensma.sizeOfAttachments());
		assertEquals(1, ensma.getChildren().length);
		assertEquals(20268, ensma.getChildren()[0]);
	}

	@Test
	public final void test_268_ISAE_ENSMA_fromJson() throws Exception {

		final Poi ensma = pois.getByUid(20268);

		assertEquals(20268, ensma.getUid());
		assertEquals(2, ensma.getPoiTypeIds()[0]);
		assertEquals("Site", ensma.getPoiTypeLabels()[0]);
		assertEquals(1, ensma.getPoiCategoryIds()[0]);
		assertEquals("Plans", ensma.getPoiCategoryLabels()[0]);
		assertEquals("46.661100939415,0.3613471984863", ensma.getCoordinates());
		assertEquals("ISAE-ENSMA", ensma.getTitle());
		assertNull(ensma.getDescription());
		assertEquals(1, ensma.getAddresses().length);
		assertEquals("test", ensma.getFaxes()[0]);
		assertEquals(0, ensma.sizeOfAttachments());
		assertEquals(20181, ensma.getParentUid());
		assertEquals(3, ensma.getChildren().length);
		assertEquals(20269, ensma.getChildren()[0]);
		assertEquals(20270, ensma.getChildren()[1]);
		assertEquals(20271, ensma.getChildren()[2]);
	}
	
	@Test
	public void test_36_UniversitéDeLimoges_attachments() throws Exception {
		
		final Poi ul = pois.getByUid(20036);
		
		assertEquals(1, ul.getAttachments().length);
		final Poi.Attachment attachment=ul.getAttachments()[0];
		assertEquals(2,attachment.getId());
		assertEquals("Carre-UL-150.png", attachment.getTitle());
		assertEquals(AttachmentType.IMAGE, attachment.getType());
		assertEquals("/uploads/poi/c99abda0f5d42a24d0cf1ef0d0476b8b6ed4311a.png",
				attachment.getUrl());
	}
}

/*
 * "url":"http:\/\/", "opening_hours":"","itinerary":"","type":"point",
 * "coordinates":[{"lat":"46.660953672573","lng":"0.36190509"}],
 * "style":{"strokeColor"
 * :"#123456","strokeWeight":"3","strokeOpacity":"0.25","fillColor"
 * :"#654321","fillOpacity":"0.45","icon":
 * "http:\/\/admin.umobile.univ-lr.fr\/uploads\/images\/poitype_icon_universite_1325777675.png"}
 * ,"overlay_image":"","parent_id":"181","level":"0","attachments":[]},
 */