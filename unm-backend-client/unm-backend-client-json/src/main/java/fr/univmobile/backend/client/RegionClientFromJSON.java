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

import fr.univmobile.backend.client.json.RegionJSONClient;

public class RegionClientFromJSON implements RegionClient {

	@Inject
	public RegionClientFromJSON(final RegionJSONClient jsonClient) {

		this.jsonClient = checkNotNull(jsonClient, "jsonClient");
	}

	private final RegionJSONClient jsonClient;

	private static Log log = LogFactory.getLog(RegionClientFromJSON.class);

	@Override
	public Region[] getRegions() throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getRegions()...");
		}

		final String json = jsonClient.getRegionsJSON();

		if (log.isDebugEnabled()) {
			log.debug("json.length(): " + json.length());
			log.debug("json: "
					+ (json.length() <= 80 ? json
							: (json.substring(0, 80) + "...")));
		}

		final Object jsonObject = JSONValue.parse(json);

		final JsonBinder binder = new DomJsonBinder();

		final RegionsJSON regionsJSON = binder.bind(jsonObject,
				RegionsJSON.class);

		return regionsJSON.getRegions();
	}

	@XPath("/*")
	public interface RegionsJSON {

		@XPath("region")
		RegionJSON[] getRegions();

		interface RegionJSON extends Region {

			@XPath("@id")
			String getId();

			@XPath("@label")
			String getLabel();

			@XPath("@url")
			String getUrl();
		}
	}
}
