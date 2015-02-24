package fr.univmobile.backend;

import fr.univmobile.backend.domain.User;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.AbstractJspController;
import fr.univmobile.web.commons.View;

abstract class AbstractBackendController extends AbstractJspController {

	protected static final String DELEGATION_USER = "delegationUser";

	private String contextUrl;

	protected AbstractBackendController() {

	}

	protected final User getUser() {

		final User user = getSessionAttribute("user", User.class);

		if (user == null) {
			throw new IllegalStateException("null user");
		}

		return user;
	}

	protected final User getDelegationUser() {

		final User delegationUser = getSessionAttribute(DELEGATION_USER,
				User.class);

		if (delegationUser != null) {

			return delegationUser;
		}

		final User user = getUser();

		setSessionAttribute(DELEGATION_USER, user);

		return user;
	}

	protected final View entered() throws TransactionException {

		return new View("admin.jsp");
	}

	protected final String getContextBaseUrl(){
		if (contextUrl == null){
			contextUrl = getServletContext().getInitParameter("baseURL");
		}
		return contextUrl;
	}
}
