package fr.univmobile.backend.client;

import java.io.IOException;
import java.sql.SQLException;

public interface CommentClient {

	Comment[] getMostRecentComments(int limit) throws IOException, SQLException;

	Comment[] getCommentsByPoiId(int poiId) throws IOException, SQLException;

	Comment[] searchComments(String query, int limit) throws IOException, SQLException;
}
