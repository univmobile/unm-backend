package fr.univmobile.backend.client;

import java.io.IOException;

public interface CommentClient {

	Comment[] getCommentsByPoiId(int poiId) throws IOException;
}
