package fr.univmobile.backend.json;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.Paths;

@Paths({ "json/regions", "json/regions/", "json/regions.json" })
public class RegionsJSONController extends AbstractJSONController {

	public RegionsJSONController(final RegionJSONClient regionJSONClient) {

		this.regionJSONClient = checkNotNull(regionJSONClient,"regionJSONClient");
	}
	
	private final RegionJSONClient regionJSONClient;

	private static final Log log = LogFactory
			.getLog(RegionsJSONController.class);

	@Override
	public String actionJSON(final String baseURL) throws IOException,
			TransactionException {

		log.debug("actionJSON()...");

		final String regionsJSON = regionJSONClient.getRegionsJSON();

		if (log.isDebugEnabled()) {
			log.debug("serveJSON(regionJSON.length: "
					+ regionsJSON.length() + ")");
		}

		final String json = "{\"url\":\""
				+ composeJSONendPoint(baseURL, "/regions") + "\","
				+ substringAfter(regionsJSON, "{");
		
		return json;
	}
}
