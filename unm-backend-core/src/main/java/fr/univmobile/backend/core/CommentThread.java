package fr.univmobile.backend.core;


public interface CommentThread {

	CommentRef[] getAllComments();
	
	int sizeOfAllComments();

	interface CommentRef {

		int getUid();
	}

	/*
	CommentThread[] getRoots();

	int getCommentUid();

	int getPoiId();

	CommentThread[] getChildren();

	CommentThread addToChildren();

	interface Context {

		String getId();

		String getType();

		int getUid();
	}

	interface Content {

		Context[] getContexts();

		CommentRef getComments();
	}

	Content getContent();
	*/
}
