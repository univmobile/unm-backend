package fr.univmobile.backend.client;

import java.io.IOException;
import java.sql.SQLException;

public interface CommentClient {

	Comment[] getCommentsByPoiId(int poiId) throws IOException, SQLException;
}
