package fr.univmobile.backend.core;

import java.io.IOException;
import java.sql.SQLException;

import fr.univmobile.commons.tx.TransactionException;

public interface CommentManager {

	// int newCommentUid();

	void addToCommentThreadByPoiId(int poiId, CommentBuilder comment)
			throws TransactionException;

	Comment[] getMostRecentComments(int limit) throws SQLException, IOException;
}
