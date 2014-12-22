package fr.univmobile.backend.admin;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.backend.domain.ImageMapRepository;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.UniversityRepository;
import fr.univmobile.backend.json.AbstractJSONController;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.Regexp;

@Paths({ "json/admin/geocampus/poi/manage", "json/admin/geocampus/poi/manage/", "json/admin/poi/manage.json" })
public class GeocampusPoiManageJSONController extends AbstractJSONController {
	private long errorId = -1;

	public GeocampusPoiManageJSONController(PoiRepository poiRepository, ImageMapRepository imageMapRepository, CategoryRepository categoryRepository, UniversityRepository universityRepository) {
		this.poiRepository = checkNotNull(poiRepository, "poiRepository");
		this.imageMapRepository = checkNotNull(imageMapRepository, "imageMapRepository");
		this.categoryRepository = checkNotNull(categoryRepository, "categoryRepository");
		this.universityRepository = checkNotNull(universityRepository, "universityRepository");
	}

	private PoiRepository poiRepository;
	private ImageMapRepository imageMapRepository;
	private CategoryRepository categoryRepository;
	private UniversityRepository universityRepository;
	
	private static final Log log = LogFactory.getLog(GeocampusPoiManageJSONController.class);
	
	@Override
	public String actionJSON(String baseURL) throws Exception {

		// 1 HTTP

		final PoiInfo data = getHttpInputs(PoiInfo.class);

		if (!data.isHttpValid()) {
			return "{ \"result\": \"invalid\" }";
		}

		// 2. APPLICATION VALIDATION
		long resultId = poiSave(data);
		return String.format("{ \"result\": \"%s\", \"data\": %d }", resultId != errorId ? "ok" : "error", resultId);
	}
	
	private String coalesce(String value) {
		return value == null ? "" : value;
	}
	
	private long poiSave(PoiInfo data) throws IOException {
		boolean createPoi = data.isNew() != null && data.isNew().equals("true");
		
		Poi poi = null;
		if (data.id() != null) {
			poi = poiRepository.findOne(data.id());
		}
		
		if (createPoi) {
			if (poi != null) {
				setAttribute("err_duplicateId", true);
				return errorId;
			} else {
				poi = new Poi();
			}
		} else {
			if (poi == null) {
				setAttribute("err_notexistsId", true);
				return errorId;
			}
		}

		poi.setName(data.name());
		
		if (data.imageMapId() != null) {
			poi.setCategory(categoryRepository.findOne(Category.Type.IMAGE_MAPS.type));
		} else {
			poi.setCategory(categoryRepository.findOne(data.category()));
		}

		if (data.parent() != null && data.parent().length() > 0) {
			Poi parentPoi = poiRepository.findOne(Long.parseLong(data.parent()));
			poi.setParent(parentPoi);
			poi.setUniversity(parentPoi.getUniversity());
		} else {
			poi.setUniversity(universityRepository.findOne(Long.parseLong(data.university())));
		}
		
		poi.setFloor(coalesce(data.floor()));
		poi.setOpeningHours(coalesce(data.openingHours()));
		poi.setPhones(coalesce(data.phones()));
		poi.setAddress(coalesce(data.address()));
		poi.setEmail(coalesce(data.email()));
		poi.setItinerary(coalesce(data.itinerary()));
		poi.setUrl(coalesce(data.url()));
		
		if (data.lat() != null && data.lat().length() > 0 && data.lat() != null && data.lat().length() > 0 ) {
			poi.setLat(Double.valueOf(data.lat()));
			poi.setLng(Double.valueOf(data.lng()));
		}

		poi.setActive(coalesce(data.active()).equals("true"));

		// 3. SAVE DATA
		try {
			poiRepository.save(poi);
		} catch (Exception e) {
			log.error("Error persisting Poi");
			log.error(e);
			return errorId;
		}

		/*
		// 4. IMAGE MAP
		if (data.imageMapId() != null) {
			final ImageMapBuilder imageMap = imageMapDs.update(imageMapDs.getByUid(data.imageMapId()));
			//imageMap
		}
		*/
		
		return poi.getId();
	}


	@HttpMethods("POST")
	interface PoiInfo extends HttpInputs {

		@HttpParameter(trim = true)
		@Regexp("[0-9]+")
		Long id();

		@HttpRequired
		@HttpParameter
		@Regexp(".*")
		String name();

		@HttpParameter(trim = true)
		String university();

		@HttpParameter
		String floor();

		@HttpParameter
		String openingHours();

		@HttpParameter
		String phones();

		@HttpParameter
		String address();

		@HttpParameter(trim = true)
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
		Long category();

		@HttpParameter
		String isNew();
		
		@HttpParameter(trim = true)
		String imageMapId();
	}
	
}
