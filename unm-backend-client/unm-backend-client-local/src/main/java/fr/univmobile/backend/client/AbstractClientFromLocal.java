package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractClientFromLocal {

	/**
	 * Constructor, with a default <tt>baseURL</tt> configuration parameter. The
	 * Controller layer may change this value for a given request.
	 * 
	 * @param baseURL
	 */
	protected AbstractClientFromLocal(final String baseURL) {

		if (isBlank(baseURL)) {
			throw new IllegalArgumentException("Argument is mandatory: baseURL");
		}

		this.baseURL = checkNotNull(baseURL, "baseURL");
	}

	private final String baseURL;

	public final String getBaseURL() {

		final String threadLocalBaseURL = //
		AbstractClientFromLocal.threadLocalBaseURL.get();

		return threadLocalBaseURL != null ? threadLocalBaseURL : this.baseURL;
	}

	private static final ThreadLocal<String> threadLocalBaseURL = new ThreadLocal<String>();

	public static void setThreadLocalBaseURL(final String baseURL) {

		if (log.isDebugEnabled()) {
			log.debug("setThreadLocalBaseURL(): " + baseURL);
		}

		checkNotNull(baseURL, "baseURL");

		threadLocalBaseURL.set(baseURL);
	}

	private static final Log log = LogFactory
			.getLog(AbstractClientFromLocal.class);

	protected final String composeURL(final String path) {

		return composeURL(getBaseURL(), path);
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

		return filterURL(getBaseURL(), url);
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
