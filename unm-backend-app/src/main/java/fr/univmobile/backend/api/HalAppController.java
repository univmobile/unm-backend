package fr.univmobile.backend.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.univmobile.backend.domain.User;

@Controller
@RequestMapping("/app")
public class HalAppController {
	
	private static final Log log = LogFactory
			.getLog(HalAppController.class);

	@Value("${baseURL}")
	private String baseUrl;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, HttpServletResponse response,
			Model model) throws IOException {
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("universityId", getPrincipal().getUniversity().getId());
		model.addAttribute("isSuperAdmin", getPrincipal().isSuperAdmin());
		response.setContentType("text/html; charset=utf-8");
		return "hal";
	}
		
	private User getPrincipal() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth == null ? null : (User) auth.getPrincipal();
	}
	
}
