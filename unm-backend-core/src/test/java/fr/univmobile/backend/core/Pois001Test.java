package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Pois001Test extends AbstractPoisIleDeFranceTest {

	public Pois001Test() {

		super(new File("src/test/data/pois/001"));
	}
	
	@Test
	public void testCount() throws Exception {
		
		assertEquals(3, pois.getAllByInt("uid").size());
	}
}
