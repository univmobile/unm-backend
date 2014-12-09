package fr.univmobile.backend.admin;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

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

@Paths({ "json/admin/geocampus/filter/${poiType}", "json/admin/geocampus/filter/{$poiType}/", "json/admin/geocampus/filter/{$poiType}.json" })
public class GeocampusPoisByRegionAndCategoryJSONController extends AbstractJSONController {

	public GeocampusPoisByRegionAndCategoryJSONController(final PoiJSONClient poisJSONClient) {
		this.poisJSONClient = checkNotNull(poisJSONClient, "poisJSONClient");
	}

	private final PoiJSONClient poisJSONClient;

	private static final Log log = LogFactory.getLog(GeocampusPoisByRegionAndCategoryJSONController.class);

	@Override
	public String actionJSON(final String baseURL) throws IOException {

		final String poiType = getPathStringVariable("${poiType}");

		final String regionId;
		final Integer categoryId;

		final Filters filters = getHttpInputs(Filters.class);
		if (filters.isHttpValid()) {
			regionId = filters.reg();
			categoryId = filters.cat();
			if (log.isDebugEnabled()) {
				log.info("reg=" + regionId + ", cat=" + categoryId);
			}
		} else {
			regionId = "all";
			categoryId = 0;
		}
		
		String regionIdFilter = regionId.equals("all") ? null : regionId;
		Integer categoryIdFilter = categoryId == 0 ? null : categoryId;

		final String poisJson = poisJSONClient.getPoisByRegionAndCategoryJSON(regionIdFilter, categoryIdFilter);

		final String json = "{\"url\":\""
				+ composeJSONendPoint(baseURL, String.format("json/admin/geocampus/filter/%s/%s/%s/", poiType, regionId, categoryId)) + "\","
				+ substringAfter(poisJson, "{");

		return json;
	}
	
	@HttpMethods("GET")
	private interface Filters extends HttpInputs {

		@HttpRequired
		@HttpParameter
		String reg();

		@HttpRequired
		@HttpParameter
		int cat();
	}

}
