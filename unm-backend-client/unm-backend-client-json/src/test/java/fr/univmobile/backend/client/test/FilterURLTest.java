package fr.univmobile.backend.client.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Test;

import fr.univmobile.backend.client.AbstractClientFromLocal;
import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.RegionClientFromJSON;
import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.backend.client.json.RegionJSONClientImpl;

public class FilterURLTest {

	@After
	public void tearDown() throws Exception {
		
		AbstractClientFromLocal.resetThreadLocalBaseURL();
	}
	
	@Test
	public void testFilterURL() throws Exception {

		final RegionClient regionClient = mock(RegionClient.class);

		final Region region = mock(Region.class);

		when(region.getUrl()).thenReturn("http://toto/tralala/json/regions");

		when(regionClient.getRegions()).thenReturn(new Region[] { region });

		final RegionJSONClient regionJSONClient = new RegionJSONClientImpl(
		// "http://toto/tralala/",
				regionClient);

		final String s = regionJSONClient.getRegionsJSON();

		assertEquals("{\"regions\":[{\"id\":null," //
				+ "\"pois\":{\"count\":0,\"url\":null}," //
				+ "\"label\":null," //
				+ "\"url\":\"http:\\/\\/toto\\/tralala\\/json\\/regions\"}]}",
				s);

		final String url = regionClient.getRegions()[0].getUrl();

		assertEquals("http://toto/tralala/json/regions", url);

		final RegionClient regionClient2 = new RegionClientFromJSON(
				regionJSONClient);

		final String url2 = regionClient2.getRegions()[0].getUrl();

		assertEquals("http://toto/tralala/json/regions", url2);
	}

	@Test
	public void testThreadLocalBaseURLFilterURL() throws Exception {

		final RegionClient regionClient = mock(RegionClient.class);

		final Region region = mock(Region.class);

		when(region.getUrl()).thenReturn("http://toto/tralala/json/regions");

		when(regionClient.getRegions()).thenReturn(new Region[] { region });

		final RegionJSONClient regionJSONClient = new RegionJSONClientImpl(
		// "http://toto/tralala/",
				regionClient);

		AbstractClientFromLocal.setThreadLocalBaseURL("(dummy)");

		final String s = regionJSONClient.getRegionsJSON();

		assertEquals("{\"regions\":[{\"id\":null," //
				+ "\"pois\":{\"count\":0,\"url\":null}," //
				+ "\"label\":null," //
				+ "\"url\":\"http:\\/\\/toto\\/tralala\\/json\\/regions\"}]}",
				s);

		final String url = regionClient.getRegions()[0].getUrl();

		assertEquals("http://toto/tralala/json/regions", url);

		final RegionClient regionClient2 = new RegionClientFromJSON(
				regionJSONClient);

		final String url2 = regionClient2.getRegions()[0].getUrl();

		assertEquals("http://toto/tralala/json/regions", url2);
	}
}
