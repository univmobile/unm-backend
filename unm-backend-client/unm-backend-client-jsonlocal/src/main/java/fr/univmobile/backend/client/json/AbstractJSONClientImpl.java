package fr.univmobile.backend.client.json;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.annotation.Nullable;

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

	protected final @Nullable String filterURL(@Nullable final String url) {

		if (url == null || !url.contains("${baseURL}")) {
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
