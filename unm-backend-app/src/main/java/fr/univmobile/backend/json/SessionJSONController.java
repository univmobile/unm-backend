package fr.univmobile.backend.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.json.SessionJSONClient;
import fr.univmobile.backend.core.AppSession;
import fr.univmobile.backend.core.InvalidSessionException;
import fr.univmobile.backend.core.SessionManager;
import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;

@Paths({ "json/session", "json/session/", "json/session.json" })
public class SessionJSONController extends AbstractJSONController {

	public SessionJSONController(final String ssoBaseURL,
			final String shibbolethTargetBaseURL,
			final String shibbolethCallbackURL,
			final SessionManager sessionManager,
			final SessionJSONClient sessionJSONClient) {

		this.ssoBaseURL = checkNotNull(ssoBaseURL, "ssoBaseURL");
		this.shibbolethTargetBaseURL = checkNotNull(shibbolethTargetBaseURL,
				"shibbolethTargetBaseURL");
		this.shibbolethCallbackURL = checkNotNull(shibbolethCallbackURL,
				"shibbolethCallbackURL");
		this.sessionManager = checkNotNull(sessionManager, "sessionManager");
		this.sessionJSONClient = checkNotNull(sessionJSONClient,
				"sessionJSONClient");
	}

	private final String ssoBaseURL;
	private final String shibbolethTargetBaseURL;
	private final String shibbolethCallbackURL;
	private final SessionManager sessionManager;
	private final SessionJSONClient sessionJSONClient;

	private static final Log log = LogFactory
			.getLog(SessionJSONController.class);

	@Override
	public String actionJSON(final String baseURL) throws IOException,
			TransactionException {

		log.debug("actionJSON()...");

		final Logout logout = getHttpInputs(Logout.class);

		if (logout.isHttpValid()) {

			return logout(logout);
		}

		final Login login = getHttpInputs(Login.class);

		if (login.isHttpValid()) {

			return login(login);
		}

		final Prepare prepare = getHttpInputs(Prepare.class);

		if (prepare.isHttpValid()) {

			return prepare(prepare);
		}

		final Refresh refresh = getHttpInputs(Refresh.class);

		if (refresh.isHttpValid()) {

			return refresh(refresh);
		}

		final Retrieve retrieve = getHttpInputs(Retrieve.class);

		if (retrieve.isHttpValid()) {

			return retrieve(retrieve);
		}

		return urls(baseURL);
	}

	private String urls(final String baseURL) throws IOException,
			TransactionException {

		log.debug("urls()...");

		final JSONMap json = new JSONMap();

		final String myURL = composeJSONendPoint(baseURL, "/session");

		json.put("url", myURL);

		json.put(
				"login",
				new JSONMap()
						.put("url", myURL)
						.put("POST_params",
								new JSONMap().put("apiKey", "xxx")
										.put("login", "xxx")
										.put("password", "xxx"))
						.put("returnType", "AppToken"));

		json.put(
				"refresh",
				new JSONMap()
						.put("url", myURL)
						.put("POST_params",
								new JSONMap().put("apiKey", "xxx").put(
										"appTokenId", "xxx"))
						.put("returnType", "AppToken"));

		json.put(
				"logout",
				new JSONMap().put("url", myURL).put(
						"POST_params",
						new JSONMap().put("logout", "").put("apiKey", "xxx")
								.put("appTokenId", "xxx")));

		json.put(
				"prepare",
				new JSONMap()
						.put("url", myURL)
						.put("POST_params",
								new JSONMap().put("prepare", "").put("apiKey",
										"xxx"))
						.put("return",
								new JSONMap().put("loginToken", "xxx").put(
										"key", "xxx")));

		final JSONMap sso = new JSONMap();

		json.put("sso", sso);

		sso.put("url", composeEndPoint(ssoBaseURL, //
				"?target=${target.url}&entityID=${shibboleth.entityProvider}"));

		sso.put("target", new JSONMap().put("url",
				composeEndPoint(shibbolethTargetBaseURL, //
						"?loginToken=${loginToken}&callback=${callback.url}")));

		sso.put("callback", new JSONMap().put("url",
				composeEndPoint(shibbolethCallbackURL)));

		json.put(
				"retrieve",
				new JSONMap()
						.put("url", myURL)
						.put("POST_params",
								new JSONMap().put("apiKey", "xxx")
										.put("loginToken", "xxx")
										.put("key", "xxx"))
						.put("returnType", "AppToken"));

		return json.toJSONString();
	}

	private String refresh(final Refresh refresh) throws IOException {

		log.debug("refresh()...");

		final String appTokenId = refresh.appTokenId();

		final AppSession appSession;

		try {

			appSession = sessionManager.getAppSession(appTokenId);

		} catch (final InvalidSessionException e) {

			log.error(e);

			return ""; // empty string

		} catch (final SQLException e) {

			log.error(e);

			return ""; // empty string
		}

		LogQueueDbImpl.setPrincipal(appSession.getUser().getUid());

		final String json = sessionJSONClient.getAppTokenJSON(refresh.apiKey(),
				appTokenId);

		return json;
	}

	private String login(final Login login) throws IOException {

		log.debug("login()...");

		LogQueueDbImpl.setAnonymous();

		final String json = sessionJSONClient.loginJSON(login.apiKey(),
				login.login(), login.password());

		return json;
	}

	private String logout(final Logout logout) throws IOException {

		log.debug("logout()...");

		final String appTokenId = logout.appTokenId();

		final AppSession appSession;

		try {

			appSession = sessionManager.getAppSession(appTokenId);

		} catch (final InvalidSessionException e) {

			log.error(e);

			return ""; // empty string

		} catch (final SQLException e) {

			log.error(e);

			return ""; // empty string
		}

		LogQueueDbImpl.setPrincipal(appSession.getUser().getUid());

		final String json = sessionJSONClient.logoutJSON(logout.apiKey(),
				appTokenId);

		return json;
	}

	private String prepare(final Prepare prepare) throws IOException {

		log.debug("prepare()...");

		LogQueueDbImpl.setAnonymous();

		final String json = sessionJSONClient.prepareJSON(prepare.apiKey());

		return json;
	}

	private String retrieve(final Retrieve retrieve) throws IOException {

		log.debug("retrieve()...");

		final String apiKey = retrieve.apiKey();
		final String loginToken = retrieve.loginToken();
		final String key = retrieve.key();

		final AppSession appSession;

		try {

			appSession = sessionManager.retrieve(apiKey, loginToken, key);

		} catch (final SQLException e) {

			log.error(e);

			return ""; // empty string
		}

		if (appSession == null) {

			log.error("Retrieve: Unknown appSession for loginToken: "
					+ loginToken);

			return ""; // empty string
		}

		LogQueueDbImpl.setPrincipal(appSession.getUser().getUid());

		final String json = sessionJSONClient.getAppTokenJSON(apiKey,
				appSession.getId());

		return json;
	}

	@HttpMethods("POST")
	private interface Login extends HttpInputs {

		@HttpRequired
		@HttpParameter
		String apiKey();

		@HttpRequired
		@HttpParameter
		String login();

		@HttpRequired
		@HttpParameter
		String password();
	}

	@HttpMethods("POST")
	private interface Logout extends HttpInputs {

		@HttpRequired
		@HttpParameter
		String apiKey();

		@HttpRequired
		@HttpParameter
		String appTokenId();

		@HttpRequired
		@HttpParameter
		String logout();
	}

	@HttpMethods("POST")
	private interface Refresh extends HttpInputs {

		@HttpRequired
		@HttpParameter
		String apiKey();

		@HttpRequired
		@HttpParameter
		String appTokenId();
	}

	@HttpMethods("POST")
	private interface Prepare extends HttpInputs {

		@HttpRequired
		@HttpParameter
		String apiKey();

		@HttpRequired
		@HttpParameter
		String prepare();
	}

	@HttpMethods("POST")
	private interface Retrieve extends HttpInputs {

		@HttpRequired
		@HttpParameter
		String apiKey();

		@HttpRequired
		@HttpParameter
		String loginToken();

		@HttpRequired
		@HttpParameter
		String key();
	}
}
