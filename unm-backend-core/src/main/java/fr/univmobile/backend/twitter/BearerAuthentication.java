package fr.univmobile.backend.twitter;

import static com.google.common.base.Preconditions.checkNotNull;
import fr.univmobile.commons.http.Authorization;

public class BearerAuthentication extends Authorization {

	public BearerAuthentication(final String accessToken) {

		this.accessToken = checkNotNull(accessToken, "accessToken");
	}

	private final String accessToken;

	@Override
	public String getAuthorizationRequestProperty() {

		return "Bearer " + accessToken;
	}

}
