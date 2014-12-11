package fr.univmobile.backend.admin;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.PoiCategory;
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

@Paths({ "json/admin/geocampus/filter", "json/admin/geocampus/filter/", "json/admin/geocampus/filter.json" })
public class GeocampusPoisByRegionAndCategoryJSONController extends AbstractJSONController {

	public GeocampusPoisByRegionAndCategoryJSONController(final PoiJSONClient poisJSONClient) {
		this.poisJSONClient = checkNotNull(poisJSONClient, "poisJSONClient");
	}

	private final PoiJSONClient poisJSONClient;

	private static final Log log = LogFactory.getLog(GeocampusPoisByRegionAndCategoryJSONController.class);

	@Override
	public String actionJSON(final String baseURL) throws IOException {

		final String poiType;
		final String regionId;
		final Integer categoryId;

		final Filters filters = getHttpInputs(Filters.class);
		if (filters.isHttpValid()) {
			poiType = filters.type();
			regionId = filters.reg();
			categoryId = filters.cat();
			if (log.isDebugEnabled()) {
				log.info("reg=" + regionId + ", cat=" + categoryId);
			}
		} else {
			poiType = "pois";
			regionId = "all";
			categoryId = 0;
		}
		
		String regionIdFilter = regionId.equals("all") ? null : regionId;
		Integer categoryIdFilter = categoryId == 0 ? null : categoryId;

		int[] excludedCategories = null;
		// exclude categories based on poi type
		if (categoryIdFilter == null) {
			if (poiType.equals("pois")) {
				excludedCategories = new int[]{ fr.univmobile.backend.core.PoiCategory.ROOT_BON_PLANS_CATEGORY_UID, fr.univmobile.backend.core.PoiCategory.ROOT_IMAGE_MAP_CATEGORY_UID }; 
			} else if (poiType.equals("bonplans")) {
				categoryIdFilter = fr.univmobile.backend.core.PoiCategory.ROOT_BON_PLANS_CATEGORY_UID;
			} else if (poiType == "images") {
				categoryIdFilter = fr.univmobile.backend.core.PoiCategory.ROOT_IMAGE_MAP_CATEGORY_UID;
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("Filters: %s - %s - %d | excluded: %s", poiType, regionIdFilter, categoryIdFilter, excludedCategories));
		}
		
		final String filterByUniversity = hasDelegationUser() && !getDelegationUser().getRole().equals("superadmin") ? getDelegationUser().getPrimaryUniversity() : null;
		final String poisJson = poisJSONClient.getPoisByRegionAndCategoryJSON(regionIdFilter, categoryIdFilter, excludedCategories, filterByUniversity, true);

		final String json = "{\"url\":\""
				+ composeJSONendPoint(baseURL, String.format("admin/geocampus/filter/%s/%s/%s/", poiType, regionId, categoryId)) + "\","
				+ substringAfter(poisJson, "{");

		return json;
	}
	
	@HttpMethods("GET")
	private interface Filters extends HttpInputs {

		@HttpRequired
		@HttpParameter
		String type();

		@HttpRequired
		@HttpParameter
		String reg();

		@HttpRequired
		@HttpParameter
		int cat();
	}

}
