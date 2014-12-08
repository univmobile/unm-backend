package fr.univmobile.backend.admin;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.json.ImageMapJSONClient;
import fr.univmobile.backend.client.json.ImageMapJSONClientImpl;
import fr.univmobile.backend.client.json.PoiCategoryJSONClient;
import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.backend.core.ImageMap;
import fr.univmobile.backend.core.ImageMapDataSource;
import fr.univmobile.backend.core.PoiCategory;
import fr.univmobile.backend.json.AbstractJSONController;
import fr.univmobile.backend.json.CommentsJSONController;

public class GeocampusJSONController extends AbstractJSONController {

	//public GeocampusJSONController(final RegionJSONClient regionJSONClient, final PoiCategoryJSONClient poiCategoryJSONClient, final ImageMapJSONClient imageMapJSONClient) {
	public GeocampusJSONController(final RegionJSONClient regionJSONClient, final PoiCategoryJSONClient poiCategoryJSONClient, final ImageMapJSONClient imageMapJSONClient, final ImageMapDataSource imageMapDs) {
		this.regionJSONClient = checkNotNull(regionJSONClient, "regionJSONClient");
		this.poiCategoryJSONClient = checkNotNull(poiCategoryJSONClient, "poiCategoryJSONClient");
		this.imageMapJSONClient = checkNotNull(imageMapJSONClient, "imageMapJSONClient");
		this.imageMapDs = checkNotNull(imageMapDs, "imageMapDs");
	}

	private final RegionJSONClient regionJSONClient;
	private final PoiCategoryJSONClient poiCategoryJSONClient;
	private final ImageMapJSONClient imageMapJSONClient;
	private final ImageMapDataSource imageMapDs;
	
	private static final Log log = LogFactory.getLog(GeocampusJSONController.class);
	
	@Override
	public String actionJSON(String baseURL) throws Exception {
		final String regionsJSON = regionJSONClient.getRegionsJSON();
		final String rootUniversitiesCategoryJSON = poiCategoryJSONClient.getPoiCategoryJSON(PoiCategory.ROOT_UNIVERSITIES_CATEGORY_UID);
		final String rootBonPlansCategoryJSON = poiCategoryJSONClient.getPoiCategoryJSON(PoiCategory.ROOT_BON_PLANS_CATEGORY_UID);
		final String imageMapsJSON = getImageMapsJSON();
		
		if (log.isDebugEnabled()) {
			log.debug("serveJSON(regionJSON.length: " + regionsJSON.length() + ")");
		}

		final String json = "{\"url\":\""
				+ composeJSONendPoint(baseURL, "/admin/geocampus") + "\","
				+ "\"root-universities-category\":" + rootUniversitiesCategoryJSON + ","
				+ "\"root-bonplans-category\":" + rootBonPlansCategoryJSON  + ","
				+ "\"image-maps\":" + imageMapsJSON  + ","
				+ substringAfter(regionsJSON, "{");
		return json;
	}
	
	private String getImageMapsJSON() throws Exception {
		String imageMapsJSON = "[";
		Set<Integer> imageMapsUids = this.imageMapDs.getAllByInt("uid").keySet();
		for (Integer uid : imageMapsUids) {
			imageMapsJSON += (imageMapsJSON.equals("[") ? "" : ",") + imageMapJSONClient.getImageMapJSON(uid);	
        }
		imageMapsJSON += "]";
		return imageMapsJSON;
	}

}
