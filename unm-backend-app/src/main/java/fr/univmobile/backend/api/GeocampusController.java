package fr.univmobile.backend.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.univmobile.backend.core.PoiCategory;
import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.backend.domain.ImageMap;
import fr.univmobile.backend.domain.ImageMapRepository;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.Region;
import fr.univmobile.backend.domain.RegionRepository;
import fr.univmobile.backend.domain.UniversityRepository;
import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;

@Controller
@RequestMapping("/admin/geocampus")
public class GeocampusController {

	@Autowired
	RegionRepository regionRepository;
	@Autowired
	UniversityRepository universityRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ImageMapRepository imageMapRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	PoiRepository poiRepository;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public GeocampusData get(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User currentUser = getCurrentUser(request);
		GeocampusData data = new GeocampusData();

		/* FIXME: remove */currentUser = userRepository.findOne(2L);
		
		if (currentUser == null || currentUser.isStudent()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		if (currentUser.isSuperAdmin()) {
			data.setRegions(regionRepository.findAllByOrderByNameAsc());
		} else {
			List<Region> oneRegion = new ArrayList<Region>(1);
			oneRegion.add(currentUser.getUniversity().getRegion());
			data.setRegions(oneRegion);
		}

		data.setPlansCategories(categoryRepository.findByLegacyStartingWithOrderByLegacyAsc(Category.getPlansLegacy()));
		data.setBonPlansCategories(categoryRepository.findByLegacyStartingWithOrderByLegacyAsc(Category.getBonPlansLegacy()));
		data.setImageMaps(imageMapRepository.findAll());

		return data;
	}

	@RequestMapping(value = "/filter", method = RequestMethod.GET)
	@ResponseBody
	public List<Poi> getFilteredPois(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User currentUser = getCurrentUser(request);

		if (currentUser == null || currentUser.isStudent()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		if (currentUser.isSuperAdmin()) {
			return poiRepository.findAllByOrderByNameAsc();
		} else {
			return poiRepository.findAllByOrderByNameAsc();
		}
	}

	/*
	public GeocampusData get(HttpServletRequest request) {
		User currentUser = getCurrentUser(request);
		
		
		
		GeocampusData data = new GeocampusData();
		data.setRegions(regionRepository.findAll());
		data.setPlansCategories(categoryRepository.findByUniversityAndLegacyStartingWithOrderByLegacyAsc(currentUser.getUniversity(),Category.getPlansLegacy()));
		data.setBonPlansCategories(categoryRepository.findByUniversityAndLegacyStartingWithOrderByLegacyAsc(currentUser.getUniversity(),Category.getBonPlansLegacy()));
		

		 //* 		final String regionsWithUniversitiesJSON = getRegionsWithUniversitiesJSON();
		//final String rootUniversitiesCategoryJSON = poiCategoryJSONClient.getPoiCategoryJSON(PoiCategory.ROOT_UNIVERSITIES_CATEGORY_UID);
		//final String rootBonPlansCategoryJSON = poiCategoryJSONClient.getPoiCategoryJSON(PoiCategory.ROOT_BON_PLANS_CATEGORY_UID);
		//final String imageMapsJSON = getImageMapsJSON();

		
		final String json = "{\"url\":\""
				+ composeJSONendPoint(baseURL, "/admin/geocampus") + "\","
				+ "\"regions\":" + regionsWithUniversitiesJSON + ","
				+ "\"root-universities-category\":" + rootUniversitiesCategoryJSON + ","
				+ "\"root-bonplans-category\":" + rootBonPlansCategoryJSON  + ","
				+ "\"image-maps\":" + imageMapsJSON  + "}";

		return data;
	}
	*/
	public class GeocampusData {
		private	Iterable<Region> regions;
		private	List<Category> plansCategories;
		private	List<Category> bonPlansCategories;
		private	Iterable<ImageMap> imageMaps;

		public Iterable<Region> getRegions() {
			return regions;
		}

		public void setRegions(Iterable<Region> regions) {
			this.regions = regions;
		}

		public List<Category> getPlansCategories() {
			return plansCategories;
		}

		public void setPlansCategories(List<Category> plansCategories) {
			this.plansCategories = plansCategories;
		}

		public List<Category> getBonPlansCategories() {
			return bonPlansCategories;
		}

		public void setBonPlansCategories(List<Category> bonPlansCategories) {
			this.bonPlansCategories = bonPlansCategories;
		}

		public Iterable<ImageMap> getImageMaps() {
			return imageMaps;
		}

		public void setImageMaps(Iterable<ImageMap> imageMaps) {
			this.imageMaps = imageMaps;
		}
	}
	
	private User getCurrentUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute("user");
	}
}
