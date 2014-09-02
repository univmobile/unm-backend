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

	@Override
	public final View action() {

		throw new NotImplementedException();
	}
}
