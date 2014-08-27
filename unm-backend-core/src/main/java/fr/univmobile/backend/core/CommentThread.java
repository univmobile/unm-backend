package fr.univmobile.backend.core;

import net.avcompris.binding.annotation.XPath;
import fr.univmobile.commons.datasource.Entry;

public interface CommentThread extends Entry<CommentThread> {

	@XPath("atom:content/atom:comment")
	CommentThread[] getRoots();

	@XPath("atom:content/@uid")
	int getUid();

	@XPath("@uid")
	int getCommentUid();

	@XPath("atom:content/atom:context[@type = 'local:poi'][1]/@uid")
	int getPoiId();

	@XPath("atom:comment")
	CommentThread[] getChildren();

	CommentThread addToChildren();

	@XPath("descendant-or-self::atom:comment")
	CommentRef[] getAllComments();

	interface Context {

		@XPath("@id")
		String getId();

		@XPath("@type")
		String getType();

		@XPath("@uid")
		int getUid();
	}

	interface CommentRef {

		@XPath("@uid")
		int getUid();
	}

	interface Content {

		@XPath("atom:context")
		Context[] getContexts();

		@XPath("atom:comment")
		CommentRef getComments();
	}

	@XPath("atom:content")
	Content getContent();
}
