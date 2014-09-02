package fr.univmobile.backend.client;

import static fr.univmobile.backend.client.AbstractClientFromLocal.composeURL;
import static fr.univmobile.backend.client.AbstractClientFromLocal.formatDateFull;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Test;

public class AbstractClientFromLocalTest {

	@Test
	public void test_noSlash_noSlash() {

		assertEquals("aa/bb", composeURL("aa", "bb"));
	}

	@Test
	public void test_slash_noSlash() {

		assertEquals("aa/bb", composeURL("aa/", "bb"));
	}

	@Test
	public void test_noSlash_slash() {

		assertEquals("aa/bb", composeURL("aa", "/bb"));
	}

	@Test
	public void test_slash_slash() {

		assertEquals("aa/bb", composeURL("aa/", "/bb"));
	}

	@Test
	public void test_dateTimeFormatter_FRENCH_1er_du_mois() throws IOException {

		assertEquals("Lundi 1er septembre 2014, 8 h 03",
				formatDateFull(new DateTime(2014, 9, 1, 8, 3, 0)));
	}

	@Test
	public void test_dateTimeFormatter_FRENCH_2014_09_02() throws IOException {

		assertEquals("Mardi 2 septembre 2014, 14 h 00",
				formatDateFull(new DateTime(2014, 9, 2, 14, 0, 0)));
	}
}
