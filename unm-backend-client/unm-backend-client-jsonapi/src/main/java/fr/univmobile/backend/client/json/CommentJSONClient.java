package fr.univmobile.backend.client.json;

import java.io.IOException;

public interface CommentJSONClient {

	String getCommentsJSONByPoiId(int poiId) throws IOException;
}
