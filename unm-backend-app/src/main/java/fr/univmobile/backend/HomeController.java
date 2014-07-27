package fr.univmobile.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.HttpRequired;

@Paths({ "" })
public class HomeController extends AbstractBackendController {

	public HomeController(final UserDataSource users) {

		super(users);
	}

	@Override
	public String action() throws IOException {

		// final String uid = getAttribute("uid", String.class);
		// final String displayName = getAttribute("displayName", String.class);
		// final String remoteUser = getRemoteUser();

		if (getHttpInputs(Logout.class).isHttpValid()) {

			removeSessionAttribute(DELEGATION_USER);

			return "home.jsp";
		}

		if (hasSessionAttribute(DELEGATION_USER)) {

			return entered();
		}

		if (getHttpInputs(Myself.class).isHttpValid()) {

			setSessionAttribute(DELEGATION_USER, getUser());

			return entered();
		}

		final Delegation form = getHttpInputs(Delegation.class);

		if (form.isHttpValid()) {

			final String delegationUid = form.uid();

			if (users.isNullByUid(delegationUid)) {

				setAttribute("err_unknownDelegationUid", true);
				setAttribute("delegationUid", delegationUid);

				return "home.jsp";
			}

			final User delegationUser = users.getByUid(delegationUid);

			setSessionAttribute(DELEGATION_USER, delegationUser);

			return entered();
		}

		return "home.jsp";
	}

	@HttpMethods("POST")
	interface Myself extends HttpInputs {

		@HttpRequired
		@HttpParameter("myself")
		String _(); // ignored
	}

	@HttpMethods("POST")
	interface Delegation extends HttpInputs {

		@HttpRequired
		@HttpParameter("delegationUid")
		String uid();

		@HttpRequired
		@HttpParameter("delegationPassword")
		String password();
	}

	@HttpMethods({ "GET", "POST" })
	interface Logout extends HttpInputs {

		@HttpRequired
		@HttpParameter("logout")
		String _(); // ignored
	}
}
