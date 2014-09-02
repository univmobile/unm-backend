package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.inject.Inject;

import net.avcompris.binding.annotation.XPath;
import net.avcompris.binding.json.JsonBinder;
import net.avcompris.binding.json.impl.DomJsonBinder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONValue;

import fr.univmobile.backend.client.json.HomeJSONClient;

public class HomeClientFromJSON implements HomeClient {

	@Inject
	public HomeClientFromJSON(final HomeJSONClient jsonClient) {

		this.jsonClient = checkNotNull(jsonClient, "jsonClient");
	}

	private final HomeJSONClient jsonClient;

	private static Log log = LogFactory.getLog(HomeClientFromJSON.class);

	@Override
	public Home getHome() throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getHome()...");
		}

		final String json = jsonClient.getHomeJSON();

		if (log.isDebugEnabled()) {
			log.debug("json.length(): " + json.length());
			log.debug("json: "
					+ (json.length() <= 80 ? json
							: (json.substring(0, 80) + "...")));
		}

		final Object jsonObject = JSONValue.parse(json);

		final JsonBinder binder = new DomJsonBinder();

		final HomeJSON homeJSON = binder.bind(jsonObject, HomeJSON.class);

		return homeJSON;
	}

	@XPath("/*")
	public interface HomeJSON extends Home {

		@XPath("@url")
		@Override
		String getUrl();
	}
}
