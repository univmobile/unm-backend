package fr.univmobile.backend;

import java.io.IOException;

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

		System.out.println("HELLO HERE");
		
		// final String uid = getAttribute("uid", String.class);
		// final String displayName = getAttribute("displayName", String.class);
		// final String remoteUser = getRemoteUser();

		if (getHttpInputs(Myself.class).isValid()) {

			setSessionAttribute("delegationUser", getUser());

			return "entered.jsp";
		}

		System.out.println("HELLO HERE2");

		final Delegation delegation = getHttpInputs(Delegation.class);

		if (delegation.isValid()) {

			final String delegationUid = delegation.uid();

			if (users.isNullByUid(delegationUid)) {

				setAttribute("errorUnknownDelegationUid", true);
				setAttribute("delegationUid", delegationUid);

				return "home.jsp";
			}

			final User delegationUser = users.getByUid(delegationUid);

			setSessionAttribute("delegationUser", delegationUser);

			return "entered.jsp";
		}

		System.out.println("HELLO HERE3");

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
}
