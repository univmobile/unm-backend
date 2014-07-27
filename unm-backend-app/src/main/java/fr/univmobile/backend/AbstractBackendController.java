package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.web.commons.AbstractController;

abstract class AbstractBackendController extends AbstractController {

	protected static final String DELEGATION_USER = "delegationUser";

	protected AbstractBackendController(final UserDataSource users) {
		
		this.users=checkNotNull(users,"users");
	}
	
	protected final UserDataSource users;

	protected final User getUser() {
		
		return getSessionAttribute("user",User.class);
	}

	protected final User getDelegationUser() {
		
		return getSessionAttribute("delegationUser",User.class);
	}
}
