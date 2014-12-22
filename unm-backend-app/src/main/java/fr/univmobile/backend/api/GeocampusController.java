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
import org.springframework.web.bind.annotation.RequestParam;
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
	public List<Poi> getFilteredPois(@RequestParam("type") String poiType, @RequestParam("reg") Long regionId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User currentUser = getCurrentUser(request);

		if (currentUser == null || currentUser.isStudent()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		String rootCategoryLegacy;
		if (poiType.equals("bonplans")) {
			rootCategoryLegacy = Category.getBonPlansLegacy();
		} else if (poiType.equals("bonplans")) {
			rootCategoryLegacy = Category.getImageMapsLegacy();
		} else {
			rootCategoryLegacy = Category.getPlansLegacy();
		}
		
		if (currentUser.isSuperAdmin()) {
			return regionId == null 
					? poiRepository.findByCategory_LegacyStartingWithOrderByNameAsc(rootCategoryLegacy) 
					: poiRepository.findByCategory_LegacyStartingWithAndUniversity_RegionOrderByNameAsc(rootCategoryLegacy, regionRepository.findOne(regionId));
		} else {
			return regionId == null 
					? poiRepository.findByCategory_LegacyStartingWithOrderByNameAsc(rootCategoryLegacy) 
					: poiRepository.findByCategory_LegacyStartingWithAndUniversity_RegionOrderByNameAsc(rootCategoryLegacy, regionRepository.findOne(regionId));
		}
	}

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
