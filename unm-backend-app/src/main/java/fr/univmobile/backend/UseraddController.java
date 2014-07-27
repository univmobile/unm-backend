package fr.univmobile.backend;

import java.io.IOException;

import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.web.commons.Paths;

@Paths({ "useradd" })
public class UseraddController extends AbstractBackendController {

	public UseraddController(final UserDataSource users) {

		super(users);
	}

	@Override
	public String action() throws IOException {

		return "useradd.jsp";
	}
}
