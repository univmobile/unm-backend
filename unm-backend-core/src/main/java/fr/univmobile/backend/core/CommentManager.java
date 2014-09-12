package fr.univmobile.backend.core;

import java.io.IOException;
import java.sql.SQLException;

import fr.univmobile.commons.datasource.DataSource;
import fr.univmobile.commons.tx.TransactionException;

public interface CommentManager extends DataSource<CommentThread> {

	// int newCommentUid();

	void addToCommentThreadByPoiId(int poiId, CommentBuilder comment)
			throws TransactionException, SQLException, IOException;

	Comment[] getMostRecentComments(int limit) throws SQLException, IOException;

	int sizeOfThreads() throws SQLException;

	int sizeOfCommentsByPoiId(int poiId) throws SQLException;

	Comment getByUid(int uid);

	// boolean isNullByUid(int uid);

	CommentThread getByPoiId(int poiId) throws SQLException, IOException;

	boolean isNullByPoiId(int poiId) throws SQLException;
}
