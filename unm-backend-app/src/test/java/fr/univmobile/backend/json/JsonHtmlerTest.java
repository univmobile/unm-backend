package fr.univmobile.backend.json;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.split;
import static org.junit.Assert.assertEquals;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class JsonHtmlerTest {

	@Test
	public void test001_endpoints_json() throws Exception {

		assertJsonHtmler(new File("src/test/JsonHtmler/001-endpoints.html"),
				new File("src/test/JsonHtmler/001-endpoints.json"));
	}

	@Test
	public void test001_endpoints_json2() throws Exception {

		assertJsonHtmler(new File("src/test/JsonHtmler/001-endpoints.html"),
				new File("src/test/JsonHtmler/001-endpoints.json2"));
	}

	@Test
	public void test002_endpoints_urls_json() throws Exception {

		assertJsonHtmler(
				new File("src/test/JsonHtmler/002-endpoints_urls.html"),
				new File("src/test/JsonHtmler/002-endpoints_urls.json"));
	}

	@Test
	public void test002_endpoints_urls_json2() throws Exception {

		assertJsonHtmler(
				new File("src/test/JsonHtmler/002-endpoints_urls.html"),
				new File("src/test/JsonHtmler/002-endpoints_urls.json2"));
	}

	@Test
	public void test003_regions_json() throws Exception {

		assertJsonHtmler(new File("src/test/JsonHtmler/003-regions.html"),
				new File("src/test/JsonHtmler/003-regions.json"));
	}

	@Test
	public void test003_regions_json2() throws Exception {

		assertJsonHtmler(new File("src/test/JsonHtmler/003-regions.html"),
				new File("src/test/JsonHtmler/003-regions.json2"));
	}

	@Test
	public void test004_bretagne_json() throws Exception {

		assertJsonHtmler(new File("src/test/JsonHtmler/004-bretagne.html"),
				new File("src/test/JsonHtmler/004-bretagne.json"));
	}

	@Test
	public void test004_bretagne_json2() throws Exception {

		assertJsonHtmler(new File("src/test/JsonHtmler/004-bretagne.html"),
				new File("src/test/JsonHtmler/004-bretagne.json2"));
	}

	@Test
	public void test005_pois_json() throws Exception {

		assertJsonHtmler(new File("src/test/JsonHtmler/005-pois.html"),
				new File("src/test/JsonHtmler/005-pois.json"));
	}

	@Test
	public void test005_pois_json2() throws Exception {

		assertJsonHtmler(new File("src/test/JsonHtmler/005-pois.html"),
				new File("src/test/JsonHtmler/005-pois.json2"));
	}

	@Test
	public void test006_pois_imageUrls_json() throws Exception {

		assertJsonHtmler(
				new File("src/test/JsonHtmler/006-pois_imageUrls.html"),
				new File("src/test/JsonHtmler/006-pois_imageUrls.json"));
	}

	@Test
	public void test006_pois_imageUrls_json2() throws Exception {

		assertJsonHtmler(
				new File("src/test/JsonHtmler/006-pois_imageUrls.html"),
				new File("src/test/JsonHtmler/006-pois_imageUrls.json2"));
	}

	@Test
	public void test007_comments_json() throws Exception {

		assertJsonHtmler(
				new File("src/test/JsonHtmler/007-comments.html"),
				new File("src/test/JsonHtmler/007-comments.json"));
	}

	@Test
	public void test007_comments_json2() throws Exception {

		assertJsonHtmler(
				new File("src/test/JsonHtmler/007-comments.html"),
				new File("src/test/JsonHtmler/007-comments.json2"));
	}

	@Test(expected = EOFException.class)
	public void testEOF_length0() throws Exception {

		JsonHtmler.jsonToHtml("");
	}

	private static void assertJsonHtmler(final File htmlRefFile,
			final File jsonTestFile) throws IOException {

		final String html = JsonHtmler.jsonToHtml(readFileToString(
				jsonTestFile, UTF_8));

		// System.out.println(html);

		assertFileEquals(htmlRefFile, html);
	}

	private static void assertFileEquals(final File htmlRefFile,
			final String htmlTestString) throws IOException {

		final List<String> refLines = readLines(htmlRefFile, UTF_8);

		final String[] testLines = split(htmlTestString, "\r\n");

		for (int i = 0; i < refLines.size(); ++i) {

			assertEquals("Line " + (i + 1), refLines.get(i), testLines[i]);
		}

		assertEquals(refLines.size(), testLines.length);
	}
}
