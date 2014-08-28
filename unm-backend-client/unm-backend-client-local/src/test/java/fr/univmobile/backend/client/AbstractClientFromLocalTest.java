package fr.univmobile.backend.client;

import static fr.univmobile.backend.client.AbstractClientFromLocal.composeURL;
import static org.junit.Assert.assertEquals;

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
}
