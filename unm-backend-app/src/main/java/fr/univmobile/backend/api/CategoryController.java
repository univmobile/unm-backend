package fr.univmobile.backend.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.backend.domain.User;

@Controller
@RequestMapping(CategoryController.BASE_REQUEST_MAPPING)
public class CategoryController {
	public static final String BASE_REQUEST_MAPPING = "/categories/manage/";
	
	private static final Log log = LogFactory
			.getLog(CategoryController.class);

	@Value("${baseURL}")
	private String baseUrl;
	@Value("${categoriesIconsBaseUrl}")
	private String categoriesIconsBaseUrl;
	@Value("${categoriesIconsBaseDir}")
	private String categoriesIconsBaseDir;

	@Autowired
	CategoryRepository categoryRepository;

	@RequestMapping(value = "{categoryId}/icons", method = RequestMethod.GET)
    public String edit(@PathVariable long categoryId, Model model){
		Category category = categoryRepository.findOne(categoryId);
        model.addAttribute("categoryForm", category);
        model.addAttribute("parentId", category.getParent() == null ? "" : category.getParent().getId());
        return "category_icons";
    }

	@RequestMapping(value = "{categoryId}/icons", method = RequestMethod.POST)
    public String update(@ModelAttribute CategoryForm categoryForm, @PathVariable long categoryId, 
    		HttpServletRequest request, HttpServletResponse response, Model model, BindingResult result){
		Category existingCategory = categoryRepository.findOne(categoryId);
		String parentId = existingCategory.getParent() == null ? "" : existingCategory.getParent().getId().toString();
		String redirectPath = String.format("redirect:%spoicategories/%s", baseUrl, parentId);
        model.addAttribute("parentId", parentId);

		String currentActiveIconUrl;
		String currentInactiveIconUrl;
		String currentMarkerIconUrl;
		
		if (existingCategory == null) {
			return redirectPath;
		} else {
			currentActiveIconUrl = existingCategory.getActiveIconUrl();
			currentInactiveIconUrl = existingCategory.getInactiveIconUrl();
			currentMarkerIconUrl = existingCategory.getMarkerIconUrl();
		}

		// Active Icon
		if (categoryForm.getFile().isEmpty()) {
			existingCategory.setActiveIconUrl(categoryForm.getActiveIconUrl());
		} else {
	    	String fileName = handleFileUpload(categoryForm.getFile(), String.format("cat_active_%s_", existingCategory.getId()));
	    	if (fileName == null) {
	        	log.error(String.format("Upload failed for category [%s] active icon ", existingCategory.getName()));
		        result.addError(new ObjectError("activeIcon", "Il y avait une erreur en essayant de sauver le icône active. Se il vous plaît vérifier votre entrée")); // FIXME: Review message
	    	} else {
	    		existingCategory.setActiveIconUrl(fileName);
	    	}
		}
    	
		// Inactive Icon
		if (categoryForm.getFile2().isEmpty()) {
			existingCategory.setInactiveIconUrl(categoryForm.getInactiveIconUrl());
		} else {
			String fileName = handleFileUpload(categoryForm.getFile2(), String.format("cat_inactive_%s_", existingCategory.getId()));
			if (fileName == null) {
				log.error(String.format("Upload failed for category [%s] inactive icon ", existingCategory.getName()));
				result.addError(new ObjectError("inactiveIcon", "Il y avait une erreur en essayant de sauver le icône inactive. Se il vous plaît vérifier votre entrée")); // FIXME: Review message
			} else {
				existingCategory.setInactiveIconUrl(fileName);
			}
		}
		
		// Marker Icon
		if (categoryForm.getFile3().isEmpty()) {
			existingCategory.setMarkerIconUrl(categoryForm.getMarkerIconUrl());
		} else {
			String fileName = handleFileUpload(categoryForm.getFile3(), String.format("cat_marker_%s_", existingCategory.getId()));
			if (fileName == null) {
				log.error(String.format("Upload failed for category [%s] marker icon ", existingCategory.getName()));
				result.addError(new ObjectError("markerIcon", "Il y avait une erreur en essayant de sauver le icône marqueur. Se il vous plaît vérifier votre entrée")); // FIXME: Review message
			} else {
				existingCategory.setMarkerIconUrl(fileName);
			}
		}
		
        model.addAttribute("categoryForm", categoryForm);

		if (result.hasErrors()) {
			categoryForm.setActiveIconUrl(currentActiveIconUrl);
			categoryForm.setInactiveIconUrl(currentInactiveIconUrl);
			categoryForm.setMarkerIconUrl(currentMarkerIconUrl);
	        return "category_icons";
		} else {
			try {
				categoryRepository.save(existingCategory);
			} catch (Exception e) {
				log.error("Error persisting category icons");
				log.error(e);
				result.addError(new ObjectError("entity", "Il y avait une erreur en essayant de sauver les changements . Se il vous plaît vérifier votre entrée")); // FIXME: Review message
				categoryForm.setActiveIconUrl(currentActiveIconUrl);
				categoryForm.setInactiveIconUrl(currentInactiveIconUrl);
				categoryForm.setMarkerIconUrl(currentMarkerIconUrl);
				return "category_icons";	
			}
	        return redirectPath;
		}
        

    }
	
	private String handleFileUpload(MultipartFile file, String prefix) {
		if (!file.isEmpty()) {
            try {
            	String fileName = String.format("%s_%s", prefix, file.getOriginalFilename());
            	String filePath = Paths.get(categoriesIconsBaseDir, fileName).toString();
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                stream.write(bytes);
                stream.close();
                return fileName;
            } catch (Exception e) {
            	log.error(String.format("There was an error while uploading file"));
            	log.error(e);
            	return null;
            }
        } else {
            log.warn(String.format("Category icon uploading failed because file is empty"));
        	return null;
        }
	}
		
	private User getPrincipal() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth == null ? null : (User) auth.getPrincipal();
	}
	
	@ModelAttribute("categoryForm")
	public CategoryForm setupFormObject() {
	    return new CategoryForm();
	}	

	public class CategoryForm {
		private String name;
		private String activeIconUrl;
		private String inactiveIconUrl;
		private String markerIconUrl;
		private MultipartFile file;
		private MultipartFile file2;
		private MultipartFile file3;

		public CategoryForm() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getActiveIconUrl() {
			return activeIconUrl;
		}

		public void setActiveIconUrl(String activeIconUrl) {
			this.activeIconUrl = activeIconUrl;
		}

		public String getInactiveIconUrl() {
			return inactiveIconUrl;
		}

		public void setInactiveIconUrl(String inactiveIconUrl) {
			this.inactiveIconUrl = inactiveIconUrl;
		}

		public String getMarkerIconUrl() {
			return markerIconUrl;
		}

		public void setMarkerIconUrl(String markerIconUrl) {
			this.markerIconUrl = markerIconUrl;
		}

		public MultipartFile getFile() {
			return file;
		}

		public void setFile(MultipartFile file) {
			this.file = file;
		}

		public MultipartFile getFile2() {
			return file2;
		}

		public void setFile2(MultipartFile file2) {
			this.file2 = file2;
		}

		public MultipartFile getFile3() {
			return file3;
		}

		public void setFile3(MultipartFile file3) {
			this.file3 = file3;
		}
		
		

	}
	
}
