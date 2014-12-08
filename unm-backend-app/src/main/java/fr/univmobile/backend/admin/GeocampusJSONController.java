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
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.json.AbstractJSONController;
import fr.univmobile.backend.json.CommentsJSONController;
import fr.univmobile.web.commons.Paths;

@Paths({ "json/admin/geocampus", "json/admin/geocampus/", "json/admin/geocampus.json" })
public class GeocampusJSONController extends AbstractJSONController {

	public GeocampusJSONController(
			final RegionJSONClient regionJSONClient, 
			final PoiCategoryJSONClient poiCategoryJSONClient, 
			final ImageMapJSONClient imageMapJSONClient, 
			final RegionDataSource regionDs,
			final ImageMapDataSource imageMapDs) {
		this.regionJSONClient = checkNotNull(regionJSONClient, "regionJSONClient");
		this.poiCategoryJSONClient = checkNotNull(poiCategoryJSONClient, "poiCategoryJSONClient");
		this.imageMapJSONClient = checkNotNull(imageMapJSONClient, "imageMapJSONClient");
		this.regionDs = checkNotNull(regionDs, "regionDs");
		this.imageMapDs = checkNotNull(imageMapDs, "imageMapDs");
	}

	private final RegionJSONClient regionJSONClient;
	private final PoiCategoryJSONClient poiCategoryJSONClient;
	private final ImageMapJSONClient imageMapJSONClient;
	private final RegionDataSource regionDs;
	private final ImageMapDataSource imageMapDs;
	
	private static final Log log = LogFactory.getLog(GeocampusJSONController.class);
	
	@Override
	public String actionJSON(String baseURL) throws Exception {
		final String regionsWithUniversitiesJSON = getRegionsWithUniversitiesJSON();
		final String rootUniversitiesCategoryJSON = poiCategoryJSONClient.getPoiCategoryJSON(PoiCategory.ROOT_UNIVERSITIES_CATEGORY_UID);
		final String rootBonPlansCategoryJSON = poiCategoryJSONClient.getPoiCategoryJSON(PoiCategory.ROOT_BON_PLANS_CATEGORY_UID);
		final String imageMapsJSON = getImageMapsJSON();

		
		final String json = "{\"url\":\""
				+ composeJSONendPoint(baseURL, "/admin/geocampus") + "\","
				+ "\"regions\":" + regionsWithUniversitiesJSON + ","
				+ "\"root-universities-category\":" + rootUniversitiesCategoryJSON + ","
				+ "\"root-bonplans-category\":" + rootBonPlansCategoryJSON  + ","
				+ "\"image-maps\":" + imageMapsJSON  + "}";
		
		return json;
	}
	
	private String getImageMapsJSON() throws Exception {
		String json = "[";
		Set<Integer> imageMapsUids = this.imageMapDs.getAllByInt("uid").keySet();
		for (Integer uid : imageMapsUids) {
			json += (json.equals("[") ? "" : ",") + imageMapJSONClient.getImageMapJSON(uid);	
        }
		json += "]";
		return json;
	}

	private String getRegionsWithUniversitiesJSON() throws Exception {
		String json = "[";
		Set<String> regionsUids = this.regionDs.getAllByString("uid").keySet();
		for (String uid : regionsUids) {
			json += (json.equals("[") ? "" : ",") + regionJSONClient.getUniversitiesJSONByRegion(uid);	
        }
		json += "]";
		return json;
	}
}
