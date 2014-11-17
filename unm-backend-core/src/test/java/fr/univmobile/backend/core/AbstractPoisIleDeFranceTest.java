package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public abstract class AbstractPoisIleDeFranceTest extends AbstractPoisTest {

	protected AbstractPoisIleDeFranceTest(final File originalDataDir) {

		super(originalDataDir);
	}

	@Test
	public final void test_paris1_University() throws Exception {

		final Poi paris1 = pois.getByUid(1);

		assertEquals(1, paris1.getPoiTypeIds()[0]);
		assertEquals("Université", paris1.getPoiTypeLabels()[0]);
		assertEquals(1, paris1.getPoiCategoryIds()[0]);
		assertEquals("Plans", paris1.getPoiCategoryLabels()[0]);
		assertEquals("/images/universities/logos/univ_paris1.jpg",
				paris1.getLogo());
		assertEquals("Http://www.univ-paris1.fr/", paris1.getUrls()[0]);
		assertEquals("0144078000", paris1.getPhones()[0]);
		assertSame(Poi.MarkerType.POINT, paris1.getMarkerType());
		assertEquals("48.84650925911,2.3459243774", paris1.getCoordinates());
		assertTrue(paris1.isActive());
		assertEquals("12 place du Panthéon 75231 PARIS",
				paris1.getAddresses()[0].getFullAddress().trim());
		assertEquals("75231", paris1.getAddresses()[0].getZipCode());
		assertEquals("PARIS", paris1.getAddresses()[0].getCity());
		assertEquals("FR", paris1.getAddresses()[0].getCountryCode());
		assertEquals("48.85", paris1.getAddresses()[0].getLatitude());
		assertEquals("2.35", paris1.getAddresses()[0].getLongitude());
		assertEquals("paris1", paris1.getUniversities()[0]);

		assertTrue(paris1.isNullParentUid());

		assertEquals(26, paris1.sizeOfChildren());
		assertEquals(0, paris1.sizeOfAttachments());
	}

	@Test
	public final void test_uvsq_University() throws Exception {

		final Poi uvsq = pois.getByUid(4);

		assertEquals(1, uvsq.getPoiTypeIds()[0]);
		assertEquals("Université", uvsq.getPoiTypeLabels()[0]);
		assertEquals("Siège : 55 avenue de Parie à Versailles",
				uvsq.getAddresses()[0].getFloor());
		assertEquals("01 39 25 78 01", uvsq.getFaxes()[0]);
		assertEquals("contact@uvsq.fr", uvsq.getEmails()[0]);
		assertEquals(0, uvsq.sizeOfAttachments());
	}

	@Test
	public final void test_paris1_CentreBroca() throws Exception {

		final Poi centreBroca = pois.getByUid(415);

		assertEquals(2, centreBroca.getPoiTypeIds()[0]);
		assertEquals("Site", centreBroca.getPoiTypeLabels()[0]);
		assertEquals(1, centreBroca.getParentUid());
		assertEquals(1, centreBroca.sizeOfChildren());
		assertEquals(1, centreBroca.sizeOfAttachments());
	}
	
}
