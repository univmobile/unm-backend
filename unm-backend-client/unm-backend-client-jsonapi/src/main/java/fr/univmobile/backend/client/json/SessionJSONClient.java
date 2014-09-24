package fr.univmobile.backend.client.json;

import java.io.IOException;

public interface SessionJSONClient {

	String loginJSON(String apiKey, String login, String password)
			throws IOException;

	String getAppTokenJSON(String apiKey, String appTokenId) throws IOException;
	
	String logoutJSON(String apiKey, String appTokenId) throws IOException;
}
