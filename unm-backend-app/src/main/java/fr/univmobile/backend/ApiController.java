package fr.univmobile.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiController {

	//@Autowired 
	String testString = "no autowire";
	
	@RequestMapping("/hello")
	@ResponseBody
	public String hello(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
		//model.addAttribute("name", name);
		return String.format("Hola: %s - Autow: %s", name, testString);
	}

}
