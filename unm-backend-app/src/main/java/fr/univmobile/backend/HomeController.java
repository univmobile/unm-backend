package fr.univmobile.backend;

import java.io.IOException;

import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "" })
public class HomeController extends AbstractBackendController {

	public HomeController(final UserDataSource users,
			final RegionDataSource regions, final PoiDataSource pois,
			final PoiTreeDataSource poiTrees) {

		super(users, regions, pois, poiTrees);
	}

	@Override
	public View action() throws IOException {

		if (getHttpInputs(Logout.class).isHttpValid()) {

			removeSessionAttribute(DELEGATION_USER);

			return new View("home.jsp");
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

				return new View("home.jsp");
			}

			final User delegationUser = users.getByUid(delegationUid);

			setSessionAttribute(DELEGATION_USER, delegationUser);

			return entered();
		}

		return new View("home.jsp");
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
