package fr.univmobile.backend.json;

import com.avcompris.lang.NotImplementedException;

// import fr.univmobile.backend.core.User;
import fr.univmobile.backend.domain.User;
import fr.univmobile.web.commons.AbstractJspController;
import fr.univmobile.web.commons.View;

public abstract class AbstractJSONController extends AbstractJspController {
	protected static final String DELEGATION_USER = "delegationUser";

	/**
	 * Return the JSON string.
	 */
	public abstract String actionJSON(String baseURL) throws Exception;

	protected static String composeJSONendPoint(final String baseURL,
			final String path) {

		return baseURL + (baseURL.endsWith("/") ? "" : "/") //
				+ "json" //
				+ (path.startsWith("/") || "".equals(path) ? "" : "/") + path;
	}

	protected static String composeEndPoint(final String baseURL,
			final String path) {

		final boolean questionMark = path.startsWith("?");

		return baseURL + (questionMark || baseURL.endsWith("/") ? "" : "/") //
				+ (path.startsWith("/") ? path.substring(1) : path);
	}

	protected static String composeEndPoint(final String baseURL) {

		return baseURL;
	}

	protected final User getUser() {

		final User user = getSessionAttribute("user", User.class);

		if (user == null) {
			throw new IllegalStateException("null user");
		}

		return user;
	}

	protected final boolean hasUser() {
		return hasSessionAttribute("user");
	}

	protected final boolean hasDelegationUser() {
		return hasSessionAttribute(DELEGATION_USER);
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

	@Override
	public final View action() {

		throw new NotImplementedException();
	}
}
