package fr.univmobile.backend.core;

import fr.univmobile.commons.tx.TransactionException;

public interface CommentManager {

	// int newCommentUid();

	void addToCommentThreadByPoiId(int poiId, CommentBuilder comment)
			throws TransactionException;
}
