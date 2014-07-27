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
import fr.univmobile.web.commons.Required;

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

		if (getHttpInputs(Logout.class).isValid()) {

			removeSessionAttribute(DELEGATION_USER);

			return "home.jsp";
		}

		if (hasSessionAttribute(DELEGATION_USER)) {

			return entered();
		}

		if (getHttpInputs(Myself.class).isValid()) {

			setSessionAttribute(DELEGATION_USER, getUser());

			return entered();
		}

		final Delegation delegation = getHttpInputs(Delegation.class);

		if (delegation.isValid()) {

			final String delegationUid = delegation.uid();

			if (users.isNullByUid(delegationUid)) {

				setAttribute("errorUnknownDelegationUid", true);
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

		@Required
		@HttpParameter("myself")
		String _(); // ignored
	}

	@HttpMethods("POST")
	interface Delegation extends HttpInputs {

		@Required
		@HttpParameter("delegationUid")
		String uid();

		@Required
		@HttpParameter("delegationPassword")
		String password();
	}

	@HttpMethods({ "GET", "POST" })
	interface Logout extends HttpInputs {

		@Required
		@HttpParameter("logout")
		String _(); // ignored
	}

	private String entered() {

		final Map<String, User> allUsers = users.getAllBy("uid");

		final List<User> list = new ArrayList<User>();

		for (final String uid : new TreeSet<String>(allUsers.keySet())) {

			list.add(allUsers.get(uid));
		}

		setAttribute("users", list);

		return "entered.jsp";
	}
}
