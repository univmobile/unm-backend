package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.web.commons.AbstractController;

abstract class AbstractBackendController extends AbstractController {

	protected AbstractBackendController(final UserDataSource users) {
		
		this.users=checkNotNull(users,"users");
	}
	
	protected final UserDataSource users;

	protected final User getUser() {
		
		return getSessionAttribute("user",User.class);
	}
}
