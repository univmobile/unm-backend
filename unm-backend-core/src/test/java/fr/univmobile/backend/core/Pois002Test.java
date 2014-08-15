package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Pois002Test extends AbstractPoisTest {

	public Pois002Test() {

		super(new File("src/test/data/pois/002"));
	}
	
	@Test
	public void testCount() throws Exception {
		
		assertEquals(7440, pois.getAllByInt("uid").size());
	}
}
