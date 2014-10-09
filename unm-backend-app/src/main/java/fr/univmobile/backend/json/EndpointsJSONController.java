package fr.univmobile.backend.json;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.Paths;

@Paths({ "json", "json/", "json.json" })
public class EndpointsJSONController extends AbstractJSONController {

	private static final Log log = LogFactory
			.getLog(EndpointsJSONController.class);

	@Override
	public String actionJSON(final String baseURL) throws IOException,
			TransactionException {

		log.debug("actionJSON()...");

		final JSONMap json = new JSONMap();

		json.put("url", composeJSONendPoint(baseURL, ""));

		json.put("regions", new JSONMap().put( //
				"url", composeJSONendPoint(baseURL, "/regions" // +".json"
				)));

		json.put("pois", new JSONMap().put( //
				"url", composeJSONendPoint(baseURL, "/pois" // + ".json"
				)));

		json.put("session", new JSONMap().put( //
				"url", composeJSONendPoint(baseURL, "/session" // + ".json"
				)));

		return json.toJSONString();
	}
}
