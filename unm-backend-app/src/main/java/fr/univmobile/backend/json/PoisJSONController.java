package fr.univmobile.backend.json;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.json.PoiJSONClient;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.Paths;

@Paths({ "json/pois", "json/pois/", "json/pois.json" })
public class PoisJSONController extends AbstractJSONController {

	public PoisJSONController(final PoiJSONClient poiJSONClient) {

		this.poiJSONClient = checkNotNull(poiJSONClient, "poiJSONClient");
	}

	private final PoiJSONClient poiJSONClient;

	private static final Log log = LogFactory.getLog(PoisJSONController.class);

	@Override
	public String actionJSON(final String baseURL) throws IOException,
			TransactionException {

		log.debug("actionJSON()...");

		final String poisJSON = poiJSONClient.getPoisJSON();

		if (log.isDebugEnabled()) {
			log.debug("serveJSON(poisJSON.length: " + poisJSON.length() + ")");
		}

		final String json = "{\"url\":\""
				+ composeJSONendPoint(baseURL, "/pois") + "\","
				+ substringAfter(poisJSON, "{");

		return json;
	}
}
