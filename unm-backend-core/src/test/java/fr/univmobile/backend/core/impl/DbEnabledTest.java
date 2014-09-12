package fr.univmobile.backend.core.impl;

import static org.junit.Assert.assertEquals;
import static fr.univmobile.backend.core.impl.DbEnabled.*;
import org.junit.Test;

public class DbEnabledTest {

	@Test
	public void testSetIntArray() {

		assertEquals("SELECT IN (1,3,4)",
				setIntArray("SELECT IN (?)", 1, 1, 3, 4));
	}
}
