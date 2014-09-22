package fr.univmobile.backend.client.json;

import java.io.IOException;

public interface SessionJSONClient {

	String login(String apiKey, String login, String password)
			throws IOException;

	String logout(String apiKey, String appTokenId) throws IOException;
}
