package fr.univmobile.backend.client.json;

import java.io.IOException;
import java.sql.SQLException;

public interface CommentJSONClient {

	String getCommentsJSONByPoiId(int poiId) throws IOException, SQLException;

	String getMostRecentCommentsJSON(int limit) throws IOException,
			SQLException;

	String searchCommentsJSON(String query, int limit) throws IOException,
			SQLException;
}
