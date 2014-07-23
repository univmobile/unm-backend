package fr.univmobile.backend;

import java.io.IOException;

import fr.univmobile.web.commons.AbstractController;
import fr.univmobile.web.commons.Paths;

@Paths({ "" })
public class HomeController extends AbstractController {

	@Override
	public String action() throws IOException {

		return "home.jsp";
	}
}
