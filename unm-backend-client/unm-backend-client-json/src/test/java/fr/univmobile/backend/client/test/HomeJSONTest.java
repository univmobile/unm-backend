package fr.univmobile.backend.client.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import fr.univmobile.backend.client.HomeClient;
import fr.univmobile.backend.client.HomeClientFromJSON;
import fr.univmobile.backend.client.json.HomeJSONClient;

public class HomeJSONTest {

	@Test
	public void testThroughJSON_home() throws IOException {

		final HomeJSONClient jsonClient = new HomeJSONClient() {

			@Override
			public String getHomeJSON() throws IOException {

				return "{\"url\":\"toto\"}";
			}
		};

		final HomeClient client = new HomeClientFromJSON(jsonClient);

		assertEquals("toto", client.getHome().getUrl());
	}
}
