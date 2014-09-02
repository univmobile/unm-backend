package fr.univmobile.backend.core.loadtesting;

import static org.junit.Assert.assertEquals;
import static fr.univmobile.backend.core.loadtesting.AbstractLoadTesting.formatMs;

import org.junit.Test;

public class FormatMsTest {

	@Test
	public void testFormatMs_60000_ms_60_sec() {
		
		assertEquals("60 sec", formatMs(60000));
	}

	@Test
	public void testFormatMs_1200000_ms_2_min_0_sec() {
		
		assertEquals("2 min 0 sec", formatMs(120000));
	}

	@Test
	public void testFormatMs_1240000_ms_2_min_4_sec() {
		
		assertEquals("2 min 4 sec", formatMs(124000));
	}
}
