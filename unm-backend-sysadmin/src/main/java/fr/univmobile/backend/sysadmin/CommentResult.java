package fr.univmobile.backend.sysadmin;

import fr.univmobile.backend.core.Comment;

class CommentResult extends Result {

	public void addComment(final Comment comment) {
		
		incRowCount();
	}
}
