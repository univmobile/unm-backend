package fr.univmobile.backend.core;


public interface CommentThread {

	CommentThread[] getRoots();

	int getCommentUid();

	int getPoiId();

	CommentThread[] getChildren();

	CommentThread addToChildren();

	CommentRef[] getAllComments();

	int sizeOfAllComments();

	interface Context {

		String getId();

		String getType();

		int getUid();
	}

	interface CommentRef {

		int getUid();
	}

	interface Content {

		Context[] getContexts();

		CommentRef getComments();
	}

	Content getContent();
}
