package fr.univmobile.backend.core;

import org.joda.time.DateTime;

import fr.univmobile.commons.datasource.EntryBuilder;


/**
 * Setter methods that cannot exist in the {@link Comment} interface (when data
 * is already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface CommentBuilder extends EntryBuilder<Comment>, Comment{

	CommentBuilder setUid(int uid);

	// CommentBuilder setName(String name);

	CommentBuilder setPostedBy(String postedBy);

	CommentBuilder setMessage(String message);

	CommentBuilder setPostedAt(DateTime date);
	
	// Author: Mauricio
	CommentBuilder setActive(String active);
	
	boolean isNullPostedBy();

	boolean isNullMessage();
	
	boolean isNullPostedAt();
}
