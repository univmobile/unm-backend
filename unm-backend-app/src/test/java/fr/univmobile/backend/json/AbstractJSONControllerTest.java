package fr.univmobile.backend.json;

import static org.junit.Assert.assertEquals;

import static fr.univmobile.backend.json.AbstractJSONController.*;
import org.junit.Test;

public class AbstractJSONControllerTest {

	@Test
	public void test_composeJSONendPoint_noslash_noslash() {
		
		assertEquals("aa/json/bb", composeJSONendPoint("aa", "bb"));
	}

	@Test
	public void test_composeEndPoint_noslash_noslash() {
		
		assertEquals("aa/bb", composeEndPoint("aa", "bb"));
	}

	@Test
	public void test_composeEndPoint_noslash_questionMark() {
		
		assertEquals("aa?bb", composeEndPoint("aa", "?bb"));
	}

	@Test
	public void test_composeEndPoint_noslash() {
		
		assertEquals("aa", composeEndPoint("aa"));
	}

	@Test
	public void test_composeJSONendPoint_slash_noslash() {
		
		assertEquals("aa/json/bb", composeJSONendPoint("aa/", "bb"));
	}

	@Test
	public void test_composeEndPoint_slash_noslash() {
		
		assertEquals("aa/bb", composeEndPoint("aa/", "bb"));
	}

	@Test
	public void test_composeEndPoint_slash_questionMark() {
		
		assertEquals("aa/?bb", composeEndPoint("aa/", "?bb"));
	}

	@Test
	public void test_composeEndPoint_slash() {
		
		assertEquals("aa/", composeEndPoint("aa/"));
	}

	@Test
	public void test_composeJSONendPoint_noslash_slash() {
		
		assertEquals("aa/json/bb", composeJSONendPoint("aa", "/bb"));
	}

	@Test
	public void test_composeEndPoint_noslash_slash() {
		
		assertEquals("aa/bb", composeEndPoint("aa", "/bb"));
	}

	@Test
	public void test_composeJSONendPoint_slash_slash() {
		
		assertEquals("aa/json/bb", composeJSONendPoint("aa/", "/bb"));
	}

	@Test
	public void test_composeEndPoint_slash_slash() {
		
		assertEquals("aa/bb", composeEndPoint("aa/", "/bb"));
	}
}
