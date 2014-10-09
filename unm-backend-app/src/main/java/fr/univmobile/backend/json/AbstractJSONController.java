package fr.univmobile.backend.json;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.web.commons.AbstractController;
import fr.univmobile.web.commons.View;

public abstract class AbstractJSONController extends AbstractController {

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

	@Override
	public final View action() {

		throw new NotImplementedException();
	}
}
