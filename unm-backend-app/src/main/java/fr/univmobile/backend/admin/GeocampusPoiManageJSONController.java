package fr.univmobile.backend.admin;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.internal.matchers.And;

import fr.univmobile.backend.client.json.ImageMapJSONClient;
import fr.univmobile.backend.client.json.ImageMapJSONClientImpl;
import fr.univmobile.backend.client.json.PoiCategoryJSONClient;
import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.backend.core.ImageMap;
import fr.univmobile.backend.core.ImageMapBuilder;
import fr.univmobile.backend.core.ImageMapDataSource;
import fr.univmobile.backend.core.PoiBuilder;
import fr.univmobile.backend.core.PoiCategory;
import fr.univmobile.backend.core.PoiCategoryDataSource;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.Region;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.json.AbstractJSONBackendController;
import fr.univmobile.backend.json.AbstractJSONController;
import fr.univmobile.backend.json.CommentsJSONController;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.Regexp;
import fr.univmobile.web.commons.View;

@Paths({ "json/admin/geocampus/poi/manage", "json/admin/geocampus/poi/manage/", "json/admin/poi/manage.json" })
public class GeocampusPoiManageJSONController extends AbstractJSONController {

	/*
	public GeocampusPoiManageJSONController(
			final TransactionManager tx,
			final RegionJSONClient regionJSONClient, 
			final PoiCategoryJSONClient poiCategoryJSONClient, 
			final ImageMapJSONClient imageMapJSONClient, 
			final RegionDataSource regionDs,
			final ImageMapDataSource imageMapDs,
			final PoiCategoryDataSource poiCategoryDs,
			final PoiDataSource poiDs) {
		this.tx = checkNotNull(tx, "tx");
		this.regionJSONClient = checkNotNull(regionJSONClient, "regionJSONClient");
		this.poiCategoryJSONClient = checkNotNull(poiCategoryJSONClient, "poiCategoryJSONClient");
		this.imageMapJSONClient = checkNotNull(imageMapJSONClient, "imageMapJSONClient");
		this.regionDs = checkNotNull(regionDs, "regionDs");
		this.imageMapDs = checkNotNull(imageMapDs, "imageMapDs");
		this.poiCategoryDs = checkNotNull(poiCategoryDs, "poiCategoryDs");
		this.poiDs = checkNotNull(poiDs, "poiDs");
	}

	private final TransactionManager tx;
	
	private final RegionJSONClient regionJSONClient;
	private final PoiCategoryJSONClient poiCategoryJSONClient;
	private final ImageMapJSONClient imageMapJSONClient;
	private final RegionDataSource regionDs;
	private final ImageMapDataSource imageMapDs;
	private final PoiCategoryDataSource poiCategoryDs;
	private final PoiDataSource poiDs;
	*/

	public GeocampusPoiManageJSONController(final TransactionManager tx, final PoiDataSource poiDs, final ImageMapDataSource imageMapDs) {
		this.tx = checkNotNull(tx, "tx");
		this.poiDs = checkNotNull(poiDs, "poiDs");
		this.imageMapDs = checkNotNull(imageMapDs, "imageMapDs");
	}

	private final TransactionManager tx;
	
	private final PoiDataSource poiDs;
	private final ImageMapDataSource imageMapDs;
	
	private static final Log log = LogFactory.getLog(GeocampusPoiManageJSONController.class);
	
	@Override
	public String actionJSON(String baseURL) throws Exception {

		// 1 HTTP

		final PoiInfo data = getHttpInputs(PoiInfo.class);

		if (!data.isHttpValid()) {
			return "{ \"result\": \"invalid\" }";
		}

		// 2. APPLICATION VALIDATION

		final String name = data.name();

		final Lock lock = tx.acquireLock(5000, "pois", name);

		try {
			return String.format("{ \"result\": \"%s\" }", poiSave(lock, data) ? "ok" : "error");
		} finally {
			lock.release();
		}
	}
	
	private String coalesce(String value) {
		return value == null ? "" : value;
	}
	
	private boolean poiSave(Lock lock, PoiInfo data) throws IOException,
		TransactionException {

		boolean hasErrors = false;

		boolean createPoi = data.isNew() != null && data.isNew().equals("true");
		
		if (createPoi && !poiDs.isNullByUid(data.id())) {
			hasErrors = true;
			setAttribute("err_duplicateUid", true);
			return false;
		}

		final PoiBuilder poi = poiDs.create();

		poi.setAuthorName(getDelegationUser().getAuthorName());
		poi.setUid(data.id());
		poi.setName(data.name());
		
		if (data.imageMapId() != null) {
			poi.setCategoryId(PoiCategory.ROOT_IMAGE_MAP_CATEGORY_UID);
		} else {
			if (data.categoryId() != null && data.categoryId().length() > 0) {
				poi.setCategoryId(Integer.parseInt(data.categoryId()));
			}
			if (data.parentUid() != null && data.parentUid().length() > 0) {
				poi.setParentUid(Integer.parseInt(data.parentUid()));
			}
		}
		
		poi.setUniversityIds(data.university());
		poi.setFloors(coalesce(data.floor()));
		poi.setOpeningHours(coalesce(data.openingHours()));
		poi.setPhones(coalesce(data.phone()));
		poi.setFullAddresses(coalesce(data.address()));
		poi.setEmails(coalesce(data.email()));
		poi.setItineraries(coalesce(data.itinerary()));
		poi.setUrls(coalesce(data.url()));
		
		if (data.lat() != null && data.lat().length() > 0 && data.lat() != null && data.lat().length() > 0 ) {
			String coordinates = String.format("%s,%s", data.lat(), data.lng());
			poi.setCoordinates(coordinates);
			poi.setLatitudes(String.valueOf(data.lat()));
			poi.setLongitudes(String.valueOf(data.lng()));
		}

		poi.setActive(coalesce(data.active()).equals("true") ? "true" : "false");

		if (hasErrors) {
			return false;
		}

		// 3. SAVE DATA

		lock.save(poi);

		// 4. IMAGE MAP
		if (data.imageMapId() != null) {
			final ImageMapBuilder imageMap = imageMapDs.update(imageMapDs.getByUid(data.imageMapId()));
			//imageMap
		}
		
		lock.commit();

		return true;
	}


	@HttpMethods("POST")
	interface PoiInfo extends HttpInputs {

		@HttpRequired
		@HttpParameter(trim = true)
		@Regexp("[0-9]+")
		int id();

		@HttpRequired
		@HttpParameter
		@Regexp(".*")
		String name();

		@HttpParameter
		String university();

		@HttpParameter
		String floor();

		@HttpParameter
		String openingHours();

		@HttpParameter
		String phone();

		@HttpParameter
		String address();

		@HttpParameter
		String parentUid();

		@HttpParameter
		String email();

		@HttpParameter
		String itinerary();

		@HttpParameter
		String url();

		@HttpParameter
		String lat();

		@HttpParameter
		String lng();

		@HttpParameter
		String active();
		
		@HttpParameter
		String categoryId();

		@HttpParameter
		String isNew();
		
		@HttpParameter
		Integer imageMapId();
	}
	
}
