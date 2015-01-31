package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import fr.univmobile.backend.core.SessionManager;
import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "" })
public class HomeController extends AbstractBackendController {

	public HomeController(final UserRepository users,
			final SessionManager sessionManager) {

		this.users = checkNotNull(users, "users");
		this.sessionManager = checkNotNull(sessionManager, "sessionManager");
	}

	private final UserRepository users;
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

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				new SecurityContextLogoutHandler().logout(checkedRequest(), checkedResponse(), auth);
			}
	        SecurityContextHolder.getContext().setAuthentication(null);			
			removeSessionAttribute(DELEGATION_USER);
			removeSessionAttribute("remoteUserLoadedBySpringSecurity");
			removeSessionAttribute("user");
			checkedRequest().getSession().invalidate();

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

			final String delegationUid = form.username();
			final User delegationUser = users.findByUsername(delegationUid);

			if (delegationUser == null) {

				setAttribute("err_unknownDelegationUid", true);
				setAttribute("delegationUid", delegationUid);

				return new View("home.jsp");
			}

			if (delegationUser.getRole().equals("student")) {

				setAttribute("err_studentDelegationUid", true);
				setAttribute("delegationUid", delegationUid);

				return new View("home.jsp");
			}

			if (delegationUser.isClassicLoginAllowed()) {
				
				if (!delegationUser.getPassword().equals(form.password())) {

					setAttribute("err_incorrectPassword", true);
					setAttribute("delegationUid", delegationUid);

					return new View("home.jsp");
				}
				
			}

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
		String username();

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
