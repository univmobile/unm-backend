package fr.univmobile.backend.client.json;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

abstract class AbstractJSONClientImpl {

	protected AbstractJSONClientImpl( //
			final String baseURL) {

		if (isBlank(baseURL)) {
			throw new IllegalArgumentException("Argument is mandatory: baseURL");
		}

		this.baseURL = checkNotNull(baseURL, "baseURL");
	}

	private final String baseURL;

	// private static final Log log =
	// LogFactory.getLog(AbstractJSONClientImpl.class);

	protected final String filterURL(final String url) {

		if (!url.contains("${baseURL}")) {
			return url;
		}

		String u = url;

		if (baseURL.endsWith("/")) {
			u = u.replace("${baseURL}/", baseURL);
		}

		u = u.replace("${baseURL}", baseURL);

		return u;
	}
}
