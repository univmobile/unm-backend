package fr.univmobile.backend.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.backend.domain.Comment;
import fr.univmobile.backend.domain.CommentRepository;
import fr.univmobile.backend.domain.ImageMap;
import fr.univmobile.backend.domain.ImageMapRepository;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.UniversityRepository;
import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;
import fr.univmobile.backend.helpers.QrCode;

@Controller
@RequestMapping("/admin/geocampus")
public class GeocampusController {

	private static final Log log = LogFactory.getLog(GeocampusController.class);
	
	@Value("${baseURL}")
	private String baseUrl;
	@Value("${qrCodesBaseUrl}")
	private String qrCodesBaseUrl;
	@Value("${imageMapsBaseUrl}")
	private String imageMapsBaseUrl;
	@Value("${imageMapsBaseDir}")
	private String imageMapsBaseDir;
	@Value("${qrBaseDir}")
	private String qrBaseDir;
	@Value("${qrCodeLinkPattern}")
	private String qrCodeLinkPattern;
	
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
	@Autowired
	CommentRepository commentRepository;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public GeocampusData get(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User currentUser = getCurrentUser(request);
		
		if (currentUser == null || currentUser.isStudent()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		GeocampusData data = new GeocampusData();
		
		if (currentUser.isSuperAdmin()) {
			data.setUniversities(universityRepository.findAllByOrderByRegion_NameAscTitleAsc());
		} else {
			List<University> universities = new ArrayList<University>(1);
			universities.add(currentUser.getUniversity());
			data.setUniversities(universities);
		}

		data.setPlansCategories(categoryRepository.findByLegacyStartingWithOrderByLegacyAsc(Category.getPlansLegacy()));
		data.setBonPlansCategories(categoryRepository.findByLegacyStartingWithOrderByLegacyAsc(Category.getBonPlansLegacy()));
		data.setLibrariesCategories(categoryRepository.findByLegacyStartingWithOrderByLegacyAsc(Category.getLibrariesLegacy()));
		data.setImagesCategories(categoryRepository.findByLegacyStartingWithOrderByLegacyAsc(Category.getImageMapsLegacy()));

		return data;
	}
	
	@RequestMapping(value = "/filterImageMap", method = RequestMethod.GET)
	@ResponseBody
	public List<ImageMap> getFilteredImageMaps(
			@RequestParam(value = "universityId", required = false) Long universityId,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		User currentUser = getCurrentUser(request);
		
		if (currentUser == null || currentUser.isStudent()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		
		if (universityId == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			log.warn("getFilteredImageMaps - A value is mandatory for universityId");
			return null;
		}

		List<ImageMap> selectedImages = null;

		if (currentUser.isSuperAdmin()) {
			if (universityId != null) {
				selectedImages = imageMapRepository.findByUniversity(universityId);
			}
		} else {
			if (currentUser.isLibrarian()) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return null;
			} else {
				selectedImages = imageMapRepository.findByUniversity(currentUser.getUniversity().getId());
			}
		}

		return selectedImages;
	}

	@RequestMapping(value = "/filter", method = RequestMethod.GET)
	@ResponseBody
	public List<Poi> getFilteredPois(
			@RequestParam("type") String poiType, 
			@RequestParam(value = "uni", required = false) Long universityId, 
			@RequestParam(value = "cat", required = false) Long categoryId, 
			@RequestParam(value = "im", required = false) Long imageMapId, 
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		User currentUser = getCurrentUser(request);
		
		if (currentUser == null || currentUser.isStudent()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		if (poiType.equals("images")) {
			if (imageMapId == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return null;
			}
			ImageMap im = imageMapRepository.findOne(imageMapId);
			if (im == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			log.warn("Nombre de POIs pour image ID " + imageMapId + " : " + im.getPois().size());
			return new ArrayList<Poi>(im.getPois());
		}
		
		String rootCategoryLegacy;
		if (poiType.equals("bonplans")) {
			rootCategoryLegacy = Category.getBonPlansLegacy();
		} else if (poiType.equals("images")) {
			rootCategoryLegacy = Category.getImageMapsLegacy();
		} else if (poiType.equals("libraries")) {
			rootCategoryLegacy = Category.getLibrariesLegacy();
			universityId = null;
		} else {
			rootCategoryLegacy = Category.getPlansLegacy();
		}
		
		Category category = null;
		List<Poi> selectedPois = null, filteredPois;
		
		if (categoryId != null) {
			category = categoryRepository.findOne(categoryId);
			
			if (currentUser.isLibrarian() && !category.getLegacy().startsWith(Category.getLibrariesLegacy())) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return null;
			}
		}
		
		if (currentUser.isSuperAdmin()) {
			if (category != null) {
				selectedPois = universityId == null 
						? poiRepository.findByCategoryOrderByNameAsc(category) 
						: poiRepository.findByCategoryAndUniversityOrderByNameAsc(category, universityRepository.findOne(universityId));
			} else {
				selectedPois = universityId == null 
						? poiRepository.findByCategory_LegacyStartingWithOrderByNameAsc(rootCategoryLegacy) 
						: poiRepository.findByCategory_LegacyStartingWithAndUniversityOrderByNameAsc(rootCategoryLegacy, universityRepository.findOne(universityId));
			}
		} else {
			if (currentUser.isLibrarian()) {
				selectedPois = category != null 
						? poiRepository.findByCategoryOrderByNameAsc(category)
						: poiRepository.findByCategory_LegacyStartingWithOrderByNameAsc(rootCategoryLegacy);
			} else {
				selectedPois = category != null
						? poiRepository.findByCategoryAndUniversityOrderByNameAsc(category, currentUser.getUniversity())
						: poiRepository.findByCategory_LegacyStartingWithAndUniversityOrderByNameAsc(rootCategoryLegacy, currentUser.getUniversity());
			}
		}
		
		if (category != null && selectedPois.size() > 0) {
			// Filling tree to get a complete one
			filteredPois = fillTree(selectedPois);
		} else {
			filteredPois = selectedPois;
		}
		
		return filteredPois;
	}

	private List<Poi> fillTree(List<Poi> selectedPois) {
		Set<Long> allPoiIds = new HashSet<Long>(selectedPois.size() * 2); // Making some allocated initial room
		
		for (Poi p : selectedPois) {
			allPoiIds.addAll(p.getLegacyIds());
		}
		
		return poiRepository.findByIdIn(allPoiIds);
	}

	@RequestMapping(value = "/qr/create", method = RequestMethod.POST)
	@ResponseBody
	public Poi createQrCode(@RequestParam("poiId") long poiId, @RequestParam("imId") Long imageMapId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User currentUser = getCurrentUser(request);
		
		if (currentUser == null || currentUser.isStudent()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		Poi poi = poiRepository.findOne(poiId);
		if (poi == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} else if (currentUser.isAdmin() && poi.getUniversity().getId() != currentUser.getUniversity().getId()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		} else if (poi.getImageMap() == null || poi.getImageMap().getId() != imageMapId) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
		
		String qrCode = QrCode.getQrFileName(poiId, imageMapId);
		QrCode.createQrCode(QrCode.composeQrURL(qrCodeLinkPattern, ImageMapController.buildImageMapWithSelectedPoiUrl(request.getServletPath(), imageMapId, poiId)), QrCode.getQrFilePath(poiId, imageMapId, qrBaseDir));
		
		poi.setQrCode(qrCode);
		poiRepository.save(poi);
		
		return poi;
	}

	@RequestMapping(value = "/comment/toggle", method = RequestMethod.POST)
	@ResponseBody
	public Comment toogleComment(@RequestParam("id") long commentId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User currentUser = getCurrentUser(request);
		
		if (currentUser == null || currentUser.isStudent()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		Comment comment = commentRepository.findOne(commentId);
		if (comment == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} else if (currentUser.isAdmin() && comment.getPoi().getUniversity().getId() != currentUser.getUniversity().getId()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		
		comment.setActive(!comment.isActive());
		
		commentRepository.save(comment);
		
		return comment;
	}

	@RequestMapping(value = "/comments", method = RequestMethod.GET)
	@ResponseBody
	private List<Comment> getPoiComments(@RequestParam("poi") Long poiId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User currentUser = getCurrentUser(request);
		
		if (currentUser == null || currentUser.isStudent()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		
		Poi p = poiRepository.findOne(poiId);
		
		return commentRepository.findTop10ByPoiOrderByIdDesc(p);
	}
	
	@RequestMapping(value = "/imagemap", method = RequestMethod.POST)
	@ResponseBody
	private ImageMap manageImageMap(@RequestParam(value = "universityId", required = false) Long universityId,  @RequestParam(value = "id", required = false) Long id, @RequestParam("name") String name, @RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User currentUser = getCurrentUser(request);
		
		if (currentUser == null || currentUser.isStudent()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		
		if (universityId == null && id == null) {
			// The university is required for an image map
    		log.error("The university ID is required to submit a new Image Map.");
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        	return null;
		}
		
        try {
        	ImageMap im;
        	if (id != null) {
        		im = imageMapRepository.findOne(id);
        		if (im == null) {
                	response.sendError(HttpServletResponse.SC_NOT_FOUND);
                	return null;
        		}
        	} else {
        		im = new ImageMap();
        		University university = universityRepository.findOne(universityId);
        		if (university == null) {
        			log.error("Impossible to create the image map, the university of id : " + universityId + " is not found.");
        			response.sendError(HttpServletResponse.SC_NOT_FOUND);
                	return null;
        		}
        		im.setUniversity(university);
        	}

        	// We allow only image files
        	if (! (file.getContentType().equalsIgnoreCase("image/gif") ||
        			file.getContentType().equalsIgnoreCase("image/jpeg") ||
        			file.getContentType().equalsIgnoreCase("image/png"))){
        		log.error(String.format("Upload failed for %s, this file does not contains an image", name));
            	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            	return null;
        	}
        	String imageMapFileName = handleFileUpload(file);
        	if (imageMapFileName == null) {
            	log.error(String.format("Upload failed for %s", name));
            	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            	return null;
        	}
    		im.setActive(true);
    		im.setName(name);
    		im.setUrl(imageMapFileName);
    		imageMapRepository.save(im);
            return im;
        } catch (Exception e) {
        	log.error(String.format("There was an error creating a new imageMap: %s", name));
        	log.error(e);
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        	return null;
        }
	}
	
	@RequestMapping(value = "/imagemapdelete", method = RequestMethod.POST)
	@ResponseBody
	private Boolean removeImageMap(
			@RequestParam(value = "universityId", required = true) Long universityId,
			@RequestParam(value = "id", required = true) Long id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// We check that the university of the image map is the same univesity
		// than the one given in parameter
		try {
			ImageMap im;
			im = imageMapRepository.findOne(id);
			if (im == null) {
				log.error("Impossible to find the image Map of id " + id);
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			University university = universityRepository.findOne(universityId);
			if (university == null) {
				log.error("Impossible to delete the image map, the university of id : "
						+ universityId + " is not found.");
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			if (im.getUniversity().getId().equals(university.getId())) {
				imageMapRepository.delete(im);
			}
			return Boolean.TRUE;
		} catch (Exception e) {
			log.error(String.format(
					"There was an error deleting the imageMap of id : %s", id));
			log.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
	}

	@RequestMapping(value = "/poidelete", method = RequestMethod.POST)
	@ResponseBody
	private Boolean removeImageMap(
			@RequestParam(value = "id", required = true) Long id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// We check that the poi is a leaf POI, the other one can not be deleted
		// The comments and bookmarks should be removed automatically
		try {
			Poi poi = poiRepository.findOne(id);
			if (poi == null) {
				log.error("Impossible to find the poi of id " + id);
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			poiRepository.delete(poi);
			return Boolean.TRUE;
		} catch (Exception e) {
			log.error(String.format(
					"There was an error deleting the poi of id : %s", id));
			log.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
	}

	private String handleFileUpload(MultipartFile file) {
		if (!file.isEmpty()) {
            try {
            	String imageMapFileName = String.format("imagemap_%s", file.getOriginalFilename());
            	String imageMapFilePath = Paths.get(imageMapsBaseDir, imageMapFileName).toString();
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(imageMapFilePath)));
                stream.write(bytes);
                stream.close();
                return imageMapFileName;
            } catch (Exception e) {
            	log.error(String.format("There was an error while uploading file"));
            	log.error(e);
            	return null;
            }
        } else {
            log.warn(String.format("Image map uploading failed for because file is empty"));
        	return null;
        }
	}
	
	public class GeocampusData {
		private	List<University> universities;
		private	List<Category> plansCategories;
		private	List<Category> bonPlansCategories;
		private	List<Category> librariesCategories;
		private	List<Category> imagesCategories;

		public List<University> getUniversities() {
			return universities;
		}

		public void setUniversities(List<University> universities) {
			this.universities = universities;
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

		public List<Category> getLibrariesCategories() {
			return librariesCategories;
		}

		public void setLibrariesCategories(List<Category> librariesCategories) {
			this.librariesCategories = librariesCategories;
		}
		
		public List<Category> getImagesCategories() {
			return imagesCategories;
		}

		public void setImagesCategories(List<Category> imagesCategories) {
			this.imagesCategories = imagesCategories;
		}

	}
	
	private User getCurrentUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute("user");
	}
}
