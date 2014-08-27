package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.EntryBuilder;


/**
 * Setter methods that cannot exist in the {@link Comment} interface (when data
 * is already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface CommentThreadBuilder extends EntryBuilder<CommentThread>, CommentThread{

	CommentThreadBuilder setUid(int uid);

	CommentThreadBuilder addToRoots();
	
	interface ContextBuilder extends Context {
		
		ContextBuilder setId(String id);
		
		ContextBuilder setType(String type);
		
		ContextBuilder setUid(int uid);
	}
	
	interface CommentRefBuilder extends CommentRef {
				
		CommentRef setUid(int uid);
	}
	
	@Override
	ContentBuilder getContent();
	
	interface ContentBuilder extends Content {
		
		CommentRefBuilder addToComments();

		ContextBuilder addToContexts();
	}
}
