package fr.univmobile.backend.core.impl;

import static fr.univmobile.backend.core.impl.DbEnabled.setIntArray;
import static org.junit.Assert.assertEquals;
import static fr.univmobile.backend.core.impl.ConnectionType.*;

import org.junit.Test;

public class DbEnabledTest {

	@Test
	public void testSetIntArray_1_3_4() {

		assertEquals("SELECT IN (1,3,4)",
				setIntArray(H2, "SELECT IN (?)", 1, 1, 3, 4));

		assertEquals("SELECT IN (1,3,4)",
				setIntArray(MYSQL, "SELECT IN (?)", 1, 1, 3, 4));
	}

	@Test
	public void testSetIntArray_empty_h2() {

		assertEquals("SELECT IN (SELECT TOP 0 0)",
				setIntArray(H2,"SELECT IN (?)", 1));
	}

	@Test
	public void testSetIntArray_empty_MySQL() {

		assertEquals("SELECT IN (SELECT 1 FROM DUAL WHERE FALSE)",
				setIntArray(MYSQL,"SELECT IN (?)", 1));
	}
}
