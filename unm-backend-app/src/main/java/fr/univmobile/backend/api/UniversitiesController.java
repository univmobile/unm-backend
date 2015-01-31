package fr.univmobile.backend.api;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.UniversityRepository;

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

		List<University> univertities = universityRepository.findAll();
		model.addAttribute("universities", univertities);

		return "universities";
	}

}
