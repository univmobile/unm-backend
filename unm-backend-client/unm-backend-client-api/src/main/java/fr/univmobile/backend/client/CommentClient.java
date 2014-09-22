package fr.univmobile.backend.client;

import java.io.IOException;

public interface CommentClient {

	Comment[] getMostRecentComments(int limit) throws IOException,
			ClientException;

	Comment[] getCommentsByPoiId(int poiId) throws IOException, ClientException;

	Comment[] searchComments(String query, int limit) throws IOException,
			ClientException;
}
