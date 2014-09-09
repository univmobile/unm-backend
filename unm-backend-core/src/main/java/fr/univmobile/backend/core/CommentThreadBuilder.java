package fr.univmobile.backend.core;


/**
 * Setter methods that cannot exist in the {@link Comment} interface (when data
 * is already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface CommentThreadBuilder extends CommentThread {

	CommentThreadBuilder addToRoots();

	interface ContextBuilder extends Context {

		//ContextBuilder setId(String id);

		//ContextBuilder setType(String type);

		//ContextBuilder setUid(int uid);
	}

	interface CommentRefBuilder extends CommentRef {

		//CommentRef setUid(int uid);
	}

	@Override
	ContentBuilder getContent();

	interface ContentBuilder extends Content {

		CommentRefBuilder addToComments();

		ContextBuilder addToContexts();
	}
}
