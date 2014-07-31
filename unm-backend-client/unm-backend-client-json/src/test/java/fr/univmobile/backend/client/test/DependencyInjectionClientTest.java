package fr.univmobile.backend.client.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import net.avcompris.binding.annotation.XPath;
import net.avcompris.binding.dom.helper.DomBinderUtils;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.Region;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.commons.DependencyInjection;

public class DependencyInjectionClientTest {

	@Before
	public void setUp() throws Exception {

		final Map<String, String> initParams = DomBinderUtils.xmlContentToJava(
				new File("src/test/inject/001-inject.xml"), InitParams.class)
				.getInitParams();

		client = new DependencyInjection(initParams).getInject(
				RegionClient.class).into(DependencyInjectionClientTest.class);
	}

	private RegionClient client;

	@Test
	public void testThroughJSON() throws IOException {

		final Region[] regions = client.getRegions();

		assertEquals(3, regions.length);

		final Region region = regions[1];

		assertEquals("ile_de_france", region.getId());
		assertEquals("Île de France", region.getLabel());
	}

	@XPath("/init-params")
	public interface InitParams {

		@XPath(value = "init-param", //
		mapKeysType = String.class, mapValuesType = String.class, //
		mapKeysXPath = "param-name", mapValuesXPath = "param-value")
		Map<String, String> getInitParams();
	}
}
