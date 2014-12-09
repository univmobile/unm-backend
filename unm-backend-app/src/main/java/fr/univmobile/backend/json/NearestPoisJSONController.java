package fr.univmobile.backend.json;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.json.PoiJSONClient;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;

@Paths({ "json/pois/nearest", "json/pois/nearest/", "json/pois/nearest.json" })
public class NearestPoisJSONController extends AbstractJSONController {

	public NearestPoisJSONController(final PoiJSONClient poiJSONClient, double nearestMaxDistanceInMeters) {

		this.poiJSONClient = checkNotNull(poiJSONClient, "poiJSONClient");
		this.nearestMaxDistanceInMeters = nearestMaxDistanceInMeters;
	}

	private final PoiJSONClient poiJSONClient;
	private final double nearestMaxDistanceInMeters;
	
	private static final Log log = LogFactory.getLog(NearestPoisJSONController.class);

	@Override
	public String actionJSON(final String baseURL) throws IOException,
			TransactionException {

		log.debug("actionJSON()...");

		final String poisJSON;

		final Coordinates coords = getHttpInputs(Coordinates.class);

		if (coords.isHttpValid()) {

			final double lat = coords.lat();
			final double lng = coords.lng();

			if (log.isInfoEnabled()) {
				log.info("lat=" + lat + ", lng=" + lng);
			}

			poisJSON = poiJSONClient.getNearestPoisJSON(lat, lng, nearestMaxDistanceInMeters);

		} else {
			return "{ \"result\": \"invalid\" }";
		}

		if (log.isDebugEnabled()) {
			log.debug("serveJSON(poisJSON.length: " + poisJSON.length() + ")");
		}

		final String json = "{\"url\":\""
				+ composeJSONendPoint(baseURL, "/pois/nearest") + "\","
				+ substringAfter(poisJSON, "{");

		return json;
	}

	@HttpMethods("GET")
	private interface Coordinates extends HttpInputs {

		@HttpRequired
		@HttpParameter
		double lat();

		@HttpRequired
		@HttpParameter
		double lng();
	}
}
