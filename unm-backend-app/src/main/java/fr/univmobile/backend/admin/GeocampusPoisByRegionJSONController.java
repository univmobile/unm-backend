package fr.univmobile.backend.admin;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.json.ImageMapJSONClient;
import fr.univmobile.backend.client.json.PoiCategoryJSONClient;
import fr.univmobile.backend.client.json.PoiJSONClient;
import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.backend.core.ImageMapDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.json.AbstractJSONController;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;

@Paths({ "json/admin/pois/region/{$regionId}", "json/admin/pois/region/{$regionId}/", "json/admin/pois/region/{$regionId}.json" })
public class GeocampusPoisByRegionJSONController extends AbstractJSONController {

	public GeocampusPoisByRegionJSONController(final PoiJSONClient poisJSONClient) {
		this.poisJSONClient = checkNotNull(poisJSONClient, "poisJSONClient");
	}

	private final PoiJSONClient poisJSONClient;

	private static final Log log = LogFactory.getLog(GeocampusPoisByRegionJSONController.class);

	@Override
	public String actionJSON(final String baseURL) throws IOException {

		final String regionId = getPathStringVariable("${regionId}");
		final String poisJson = poisJSONClient.getPoisByRegionJSON(regionId);

		final String json = "{\"url\":\""
				+ composeJSONendPoint(baseURL, "/pois/regions/" + regionId) + "\","
				+ substringAfter(poisJson, "{");

		return json;
	}
}
