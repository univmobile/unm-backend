package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.annotation.Nullable;

abstract class AbstractClientFromLocal {

	protected AbstractClientFromLocal( //
			final String baseURL) {

		if (isBlank(baseURL)) {
			throw new IllegalArgumentException("Argument is mandatory: baseURL");
		}

		this.baseURL = checkNotNull(baseURL, "baseURL");
	}

	private final String baseURL;

	// private static final Log log =
	// LogFactory.getLog(AbstractClientFromLocal.class);

	protected final String composeURL(final String path) {

		return composeURL(baseURL, path);
	}

	public static String composeURL(final String baseURL, final String path) {

		checkNotNull(baseURL, "baseURL");
		checkNotNull(path, "path");

		if (baseURL.endsWith("/")) {

			if (path.startsWith("/")) {

				return baseURL + path.substring(1);

			} else {

				return baseURL + path;
			}

		} else {

			if (path.startsWith("/")) {

				return baseURL + path;

			} else {

				return baseURL + "/" + path;
			}
		}
	}

	protected final @Nullable
	String filterURL(@Nullable final String url) {

		return filterURL(baseURL, url);
	}

	public static @Nullable
	String filterURL(final String baseURL, @Nullable final String url) {

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
