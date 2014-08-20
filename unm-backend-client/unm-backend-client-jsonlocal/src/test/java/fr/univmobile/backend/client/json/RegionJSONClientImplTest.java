package fr.univmobile.backend.client.json;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;

public class RegionJSONClientImplTest {

	@Test
	public void testSimple() throws Exception {

		final RegionClient regionClient = mock(RegionClient.class);

		final Region region = mock(Region.class);

		when(region.getId()).thenReturn("aa");
		when(region.getLabel()).thenReturn("bb");
		when(region.getUrl()).thenReturn("cc");

		when(regionClient.getRegions()).thenReturn(new Region[] { region });

		final RegionJSONClient regionJSONClient = new RegionJSONClientImpl(
				"(dummy baseURL)", regionClient);

		final String s = regionJSONClient.getRegionsJSON();

		assertEquals("{\"regions\":[{\"id\":\"aa\"," //
				+"\"pois\":{\"count\":0,\"url\":null}," //
				+ "\"label\":\"bb\"," //
				+ "\"url\":\"cc\"}]}", s);
	}

	@Test
	public void testFilterURL() throws Exception {

		final RegionClient regionClient = mock(RegionClient.class);

		final Region region = mock(Region.class);

		when(region.getUrl()).thenReturn("${baseURL}/json/regions");

		when(regionClient.getRegions()).thenReturn(new Region[] { region });

		final RegionJSONClient regionJSONClient = new RegionJSONClientImpl(
				"http://toto/tralala/", regionClient);

		final String s = regionJSONClient.getRegionsJSON();

		assertEquals("{\"regions\":[{\"id\":null," //
				+"\"pois\":{\"count\":0,\"url\":null}," //
				 + "\"label\":null," //
				+ "\"url\":\"http:\\/\\/toto\\/tralala\\/json\\/regions\"}]}", s);
	}

	@Test
	public void testEscapedChars() throws Exception {

		final RegionClient regionClient = mock(RegionClient.class);

		final Region region = mock(Region.class);

		when(region.getId()).thenReturn("a\"a");
		when(region.getLabel()).thenReturn("b\\b");
		when(region.getUrl()).thenReturn("cc");

		when(regionClient.getRegions()).thenReturn(new Region[] { region });

		final RegionJSONClient regionJSONClient = new RegionJSONClientImpl(
				"(dummy baseURL)", regionClient);

		final String s = regionJSONClient.getRegionsJSON();

		assertEquals("{\"regions\":[{\"id\":\"a\\\"a\"," //
				+"\"pois\":{\"count\":0,\"url\":null}," //
				+ "\"label\":\"b\\\\b\"," //
				+ "\"url\":\"cc\"}]}", s);
	}
}
