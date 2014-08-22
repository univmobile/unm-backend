package fr.univmobile.backend.core.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.core.UploadManager;

public class UploadManagerImplTest {

	@Before
	public void setUp() throws Exception {

		uploadManager = new UploadManagerImpl(new File(
				"src/test/data/uploads/001"));
	}

	private UploadManager uploadManager;

	@Test
	public void testGetUploadAsStream() throws Exception {

		final InputStream is = uploadManager.getUploadAsStream( //
				"/uploads/poi/c99abda0f5d42a24d0cf1ef0d0476b8b6ed4311a.png");

		assertEquals(9213, IOUtils.toByteArray(is).length);

		final File file = new File(
				"src/test/data/uploads/001/poi/c99abda0f5d42a24d0cf1ef0d0476b8b6ed4311a.png");

		assertEquals(9213, file.length());
	}

	@Test
	public void testGetUploadMimeType() throws Exception {

		final String mimeType = uploadManager.getUploadMimeType( //
				"/uploads/poi/c99abda0f5d42a24d0cf1ef0d0476b8b6ed4311a.png");

		assertEquals("image/png", mimeType);
	}
}
