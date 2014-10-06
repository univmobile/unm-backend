package fr.univmobile.backend.twitter;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.univmobile.commons.http.HttpUtils.wpost;

import java.io.IOException;

import net.avcompris.binding.annotation.XPath;

import org.json.simple.JSONValue;

import fr.univmobile.commons.http.Authorization;
import fr.univmobile.commons.http.BasicAuthentication;

public class ApplicationOnly extends TwitterAccessImpl {

	public static final String OAUTH2_URL = "https://api.twitter.com/oauth2/token";

	public ApplicationOnly(final String consumerKey, final String consumerSecret) {

		this.consumerKey = checkNotNull(consumerKey, "consumerKey");
		this.consumerSecret = checkNotNull(consumerSecret, "consumerSecret");
	}

	private final String consumerKey;
	private final String consumerSecret;

	@Override
	protected Authorization getAuthorization() throws IOException {

		final String accessToken = getAccessToken();

		return new BearerAuthentication(accessToken);
	}

	private String getAccessToken() throws IOException {

		if (accessToken == null) {

			loadAccessToken();
		}

		return accessToken;
	}

	private synchronized void loadAccessToken() throws IOException {

		if (accessToken != null) {
			return;
		}

		final BasicAuthentication credentials = new BasicAuthentication(
				consumerKey, consumerSecret);

		final String json = wpost(credentials, OAUTH2_URL, "grant_type",
				"client_credentials");

		final Object jsonObject = JSONValue.parse(json);

		final OAuth2Token token = binder.bind(jsonObject, OAuth2Token.class);

		accessToken = token.getAccessToken();
	}

	private String accessToken = null;

	@XPath("/json")
	private interface OAuth2Token {

		@XPath("@access_token")
		String getAccessToken();
	}
}
