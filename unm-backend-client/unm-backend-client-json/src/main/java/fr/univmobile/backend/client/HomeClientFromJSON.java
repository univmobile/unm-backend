package fr.univmobile.backend.client;

import java.io.IOException;

import javax.inject.Inject;

import net.avcompris.binding.annotation.XPath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.json.HomeJSONClient;

public class HomeClientFromJSON extends AbstractClientFromJSON<HomeJSONClient>
		implements HomeClient {

	@Inject
	public HomeClientFromJSON(final HomeJSONClient jsonClient) {

		super(jsonClient);
	}

	private static Log log = LogFactory.getLog(HomeClientFromJSON.class);

	@Override
	public Home getHome() throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getHome()...");
		}

		return unmarshall(jsonClient.getHomeJSON(), HomeJSON.class);
	}

	@XPath("/*")
	public interface HomeJSON extends Home {

		@XPath("@url")
		@Override
		String getUrl();
	}
}
