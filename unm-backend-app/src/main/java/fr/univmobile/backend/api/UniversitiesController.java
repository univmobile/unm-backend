package fr.univmobile.backend.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.UniversityRepository;
import fr.univmobile.backend.domain.User;

@Controller
@RequestMapping(UniversitiesController.BASE_REQUEST_MAPPING)
public class UniversitiesController {
	public static final String BASE_REQUEST_MAPPING = "/universities/manage/";
	
	private static final Log log = LogFactory
			.getLog(UniversitiesController.class);

	@Value("${baseURL}")
	private String baseUrl;
	@Value("${universitiesLogoBaseUrl}")
	private String universitiesLogoBaseUrl;
	@Value("${universitiesLogoBaseDir}")
	private String universitiesLogoBaseDir;

	@Autowired
	UniversityRepository universityRepository;

	@RequestMapping(method = RequestMethod.GET)
	public String get(@RequestParam(value = "regionId", required = false) Long regionId, Model model) throws IOException {

		List<University> universities;

		if (getPrincipal().isSuperAdmin()) {
			if (regionId == null){
				universities = universityRepository.findAll();
			} else {
				universities = universityRepository.findAllByRegion_Id(regionId);
			}
		} else {
			University userUniversity = universityRepository.findOne(getPrincipal().getUniversity().getId());
			universities = new ArrayList<University>(1);
			if (regionId == null || regionId.equals(userUniversity.getRegion().getId())){
				universities.add(userUniversity);
			}
		}

		model.addAttribute("regionId", regionId);
		model.addAttribute("universities", universities);

		return "universities";
	}

	@RequestMapping(value = "{universityId}", method = RequestMethod.GET)
    public String edit(@PathVariable long universityId, Model model){
        University university = universityRepository.findOne(universityId);
        model.addAttribute("universityForm", university);

        return "universities_edit";
    }

	@RequestMapping(value = "{universityId}", method = RequestMethod.POST)
    public String update(@ModelAttribute UniversityForm universityForm, @PathVariable long universityId, 
    		HttpServletRequest request, HttpServletResponse response, Model model, BindingResult result){
		University existingUniversity = universityRepository.findOne(universityId);
		String redirectPath = String.format("redirect:%s%s", request.getServletPath(), BASE_REQUEST_MAPPING);
		String currentLogoUrl;
		
		if (existingUniversity == null) {
			return redirectPath;
		} else {
			currentLogoUrl = existingUniversity.getLogoUrl();
			if (universityForm.getTitle() == null || universityForm.getTitle().isEmpty()) {
				result.rejectValue("title", "univ.title.empty", "Titre ne peut être vide");
			}
		}

		if (universityForm.getFile().isEmpty()) {
			existingUniversity.setLogoUrl(universityForm.getLogoUrl());
		} else {
	    	String fileName = handleFileUpload(universityForm.file, String.format("ul_%s_", existingUniversity.getId()));
	    	if (fileName == null) {
	        	log.error(String.format("Upload failed for university [%s] logo", existingUniversity.getTitle()));
		        result.addError(new ObjectError("logo", "Il y avait une erreur en essayant de sauver les changements . Se il vous plaît vérifier votre entrée"));
	    	} else {
	    		existingUniversity.setLogoUrl(fileName);
	    	}
		}
    	
		existingUniversity.setTitle(universityForm.getTitle());
		existingUniversity.setModerateComments(universityForm.getModerateComments());
		existingUniversity.setMobileShibbolethUrl(universityForm.getMobileShibbolethUrl());
		existingUniversity.setCrous(universityForm.getCrous());
		existingUniversity.setActive(universityForm.getActive());

        model.addAttribute("universityForm", universityForm);

		if (result.hasErrors()) {
	        universityForm.setLogoUrl(currentLogoUrl);
	        return "universities_edit";
		} else {
			try {
				universityRepository.save(existingUniversity);
			} catch (Exception e) {
				log.error("Error persisting university update");
				log.error(e);
				result.addError(new ObjectError("entity", "Il y avait une erreur en essayant de sauver les changements . Se il vous plaît vérifier votre entrée"));
				universityForm.setLogoUrl(currentLogoUrl);
				return "universities_edit";	
			}
	        return redirectPath;
		}
        

    }
	
	private String handleFileUpload(MultipartFile file, String prefix) {
		if (!file.isEmpty()) {
            try {
            	String fileName = String.format("%s_%s", prefix, file.getOriginalFilename());
            	String filePath = Paths.get(universitiesLogoBaseDir, fileName).toString();
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
            log.warn(String.format("University logo uploading failed because file is empty"));
        	return null;
        }
	}
		
	private User getPrincipal() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth == null ? null : (User) auth.getPrincipal();
	}
	
	@ModelAttribute("universityForm")
	public UniversityForm setupFormObject() {
	    return new UniversityForm();
	}	

	public class UniversityForm {
		private String title;
		private boolean moderateComments;
		private String logoUrl;
		private String mobileShibbolethUrl;
		private MultipartFile file;
		private boolean crous;
		private boolean active;

		public UniversityForm() {
		}
		
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public boolean getModerateComments() {
			return moderateComments;
		}

		public void setModerateComments(boolean moderateComments) {
			this.moderateComments = moderateComments;
		}

		public boolean getCrous() {
			return crous;
		}

		public void setCrous(boolean crous) {
			this.crous = crous;
		}

		public boolean getActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public String getLogoUrl() {
			return logoUrl;
		}

		public void setLogoUrl(String logoUrl) {
			this.logoUrl = logoUrl;
		}

		public String getMobileShibbolethUrl() {
			return mobileShibbolethUrl;
		}

		public void setMobileShibbolethUrl(String mobileShibbolethUrl) {
			this.mobileShibbolethUrl = mobileShibbolethUrl;
		}

		public MultipartFile getFile() {
			return file;
		}

		public void setFile(MultipartFile file) {
			this.file = file;
		}
		

	}
	
}
