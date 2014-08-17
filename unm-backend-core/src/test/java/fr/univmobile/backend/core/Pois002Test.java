package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import fr.univmobile.backend.core.Poi.AttachmentType;

public class Pois002Test extends AbstractPoisIleDeFranceTest {

	public Pois002Test() {

		super(new File("src/test/data/pois/002"));
	}

	@Test
	public void testCount() throws Exception {

		assertEquals(7440, pois.getAllByInt("uid").size());
	}

	@Test
	public final void test_281_CentrePierreMendèsFrance_attachments()
			throws Exception {

		final Poi pmf = pois.getByUid(281);

		assertEquals("Centre Pierre Mendès France", pmf.getTitle());

		assertEquals(1, pmf.sizeOfAttachments());

		final Poi.Attachment attachment = pmf.getAttachments()[0];

		assertEquals(32, attachment.getId());
		assertEquals("pmf_01.jpg", attachment.getTitle());
		// assertTrue(attachment.isNullDescription());
		assertEquals(AttachmentType.IMAGE, attachment.getType());
		assertEquals(
				"/uploads/poi/6fe6180c4cf7c056c0b34c411c8a6efebb04a454.jpg",
				attachment.getUrl());
	}
}
