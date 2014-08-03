package fr.univmobile.backend.it;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import fr.univmobile.backend.it.TestBackend;

public class TestBackendTest {

	@Test
	public void testSetUpData() throws IOException {

		TestBackend.setUpData("001", new File("target", "001"));
	}
}
