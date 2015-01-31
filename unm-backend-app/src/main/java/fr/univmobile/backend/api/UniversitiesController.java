package fr.univmobile.backend.api;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.UniversityRepository;
import fr.univmobile.backend.domain.UsageStatDto;
import fr.univmobile.backend.domain.User;

@Controller
@RequestMapping("/universities/manage")
public class UniversitiesController {

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
	public String get(HttpServletRequest request, HttpServletResponse response,
			Model model) throws IOException {

		List<University> universities;

		if (getPrincipal().isSuperAdmin()) {
			universities = universityRepository.findAll();
		} else {
			University userUniversity = universityRepository.findOne(getPrincipal().getUniversity().getId());
			universities = new ArrayList<University>(1);
			universities.add(userUniversity);
		}

		model.addAttribute("universities", universities);

		return "universities";
	}

	@RequestMapping(value = "{universityId}", method = RequestMethod.GET)
    public String edit(@PathVariable long universityId, Model model){
        University university = universityRepository.findOne(universityId);
        model.addAttribute("university", university);

        return "universities_edit";
    }

	@RequestMapping(value = "{universityId}", method = RequestMethod.POST)
    public String update(@ModelAttribute University university, @PathVariable long universityId, Model model){
		University existingUniversity = universityRepository.findOne(universityId);
		
		existingUniversity.setTitle(university.getTitle());
		existingUniversity.setModerateComments(university.getModerateComments());
        
		universityRepository.save(existingUniversity);
        
        model.addAttribute("university", existingUniversity);

        return "universities_edit"; // FIXME: redirect to list if success 
    }

	private User getPrincipal() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth == null ? null : (User) auth.getPrincipal();
	}

}
