package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.core.SessionManager;
import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "" })
public class HomeController extends AbstractBackendController {

	public HomeController(final UserDataSource users,
			final SessionManager sessionManager) {

		this.users = checkNotNull(users, "users");
		this.sessionManager = checkNotNull(sessionManager, "sessionManager");
	}

	private final UserDataSource users;
	private final SessionManager sessionManager;

	private static final Log log = LogFactory.getLog(HomeController.class);

	@Override
	public View action() throws IOException, SQLException, TransactionException {

		final ShibbolethCallback callback = getHttpInputs(ShibbolethCallback.class);

		if (callback.isHttpValid()) {

			log.debug("callback.isHttpValid()");
			
			final String loginToken = callback.loginToken();

			final User user = getUser();

			sessionManager.updateLoginConversation(loginToken, user);

			return sendRedirect(callback.callback() //
					+ "?loginToken=" + loginToken);
		}

		if (getHttpInputs(Logout.class).isHttpValid()) {

			log.debug("Logout.isHttpValid()");

			removeSessionAttribute(DELEGATION_USER);

			return new View("home.jsp");
		}

		if (hasSessionAttribute(DELEGATION_USER)) {

			log.debug("hasSessionAttribute(DELEGATION_USER)");

			return entered();
		}

		if (getHttpInputs(Myself.class).isHttpValid()) {

			log.debug("Myself.isHttpValid()");

			setSessionAttribute(DELEGATION_USER, getUser());

			return entered();
		}

		final Delegation form = getHttpInputs(Delegation.class);

		if (form.isHttpValid()) {

			log.debug("Delegation.isHttpValid()");

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

		log.debug("Default: home.jsp");
		
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

	@HttpMethods("GET")
	interface ShibbolethCallback extends HttpInputs {

		/**
		 * e.g. loginToken=79894e4d-a391-4381-b752-5ab88309c003-vnQ
		 */
		@HttpRequired
		@HttpParameter
		String loginToken();

		/**
		 * e.g.
		 * callback=http://univmobile.vswip.com/unm-mobileweb/login/shibboleth/
		 */
		@HttpRequired
		@HttpParameter
		String callback();
	}
}
