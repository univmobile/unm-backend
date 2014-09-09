package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.RevDataSource;
import fr.univmobile.commons.datasource.Category;
import fr.univmobile.commons.datasource.PrimaryKey;
import fr.univmobile.commons.datasource.SearchAttribute;
import fr.univmobile.commons.datasource.Support;

@Category("comments")
@PrimaryKey("uid")
@Support(data = Comment.class, builder = CommentBuilder.class)
public interface CommentDataSource extends
		RevDataSource<Comment, CommentBuilder> {

	@SearchAttribute("uid")
	Comment getByUid(int uid);

	boolean isNullByUid(int uid);
}
