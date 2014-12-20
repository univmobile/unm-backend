package fr.univmobile.backend.admin;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//murraco@bitbucket.org/blitzitsa/univmobile.git
import fr.univmobile.backend.core.ImageMapBuilder;
import fr.univmobile.backend.core.ImageMapDataSource;
import fr.univmobile.backend.core.PoiBuilder;
import fr.univmobile.backend.core.PoiCategory;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.json.AbstractJSONController;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.Regexp;

@Paths({ "json/admin/geocampus/poi/manage", "json/admin/geocampus/poi/manage/", "json/admin/poi/manage.json" })
public class GeocampusPoiManageJSONController extends AbstractJSONController {

	public GeocampusPoiManageJSONController(PoiRepository poiRepository, final ImageMapDataSource imageMapDs) {
		this.poiRepository = checkNotNull(poiRepository, "poiRepository");
	}

	private PoiRepository poiRepository;
	
	private static final Log log = LogFactory.getLog(GeocampusPoiManageJSONController.class);
	
	@Override
	public String actionJSON(String baseURL) throws Exception {

		// 1 HTTP

		final PoiInfo data = getHttpInputs(PoiInfo.class);

		if (!data.isHttpValid()) {
			return "{ \"result\": \"invalid\" }";
		}

		// 2. APPLICATION VALIDATION

		return String.format("{ \"result\": \"%s\" }", poiSave(data) ? "ok" : "error");
	}
	
	private String coalesce(String value) {
		return value == null ? "" : value;
	}
	
	private boolean poiSave(PoiInfo data) throws IOException {

		boolean hasErrors = false;

		boolean createPoi = data.isNew() != null && data.isNew().equals("true");
		
		Poi poi = poiRepository.findOne(data.id());
		
		if (createPoi) {
			if (poi != null) {
				hasErrors = true;
				setAttribute("err_duplicateId", true);
				return false;
			} else {
				poi = new Poi();
			}
		} else {
			if (poi == null) {
				hasErrors = true;
				setAttribute("err_notexistsId", true);
				return false;
			}
		}

		poi.setName(data.name());
		
		/*
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
		*/
		
		// FIXME: poi.setUniversityIds(data.university());
		poi.setFloor(coalesce(data.floor()));
		poi.setOpeningHours(coalesce(data.openingHours()));
		poi.setPhones(coalesce(data.phone()));
		poi.setAddress(coalesce(data.address()));
		poi.setEmail(coalesce(data.email()));
		poi.setItinerary(coalesce(data.itinerary()));
		poi.setUrl(coalesce(data.url()));
		
		if (data.lat() != null && data.lat().length() > 0 && data.lat() != null && data.lat().length() > 0 ) {
			poi.setLat(Double.valueOf(data.lat()));
			poi.setLng(Double.valueOf(data.lng()));
		}

		poi.setActive(coalesce(data.active()).equals("true"));

		if (hasErrors) {
			return false;
		}

		// 3. SAVE DATA

		poiRepository.save(poi);

		/*
		// 4. IMAGE MAP
		if (data.imageMapId() != null) {
			final ImageMapBuilder imageMap = imageMapDs.update(imageMapDs.getByUid(data.imageMapId()));
			//imageMap
		}
		*/

		return true;
	}


	@HttpMethods("POST")
	interface PoiInfo extends HttpInputs {

		@HttpRequired
		@HttpParameter(trim = true)
		@Regexp("[0-9]+")
		Long id();

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
		String parent();

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
		String category();

		@HttpParameter
		String isNew();
		
		@HttpParameter
		Integer imageMapId();
	}
	
}
