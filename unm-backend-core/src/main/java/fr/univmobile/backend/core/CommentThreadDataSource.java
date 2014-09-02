package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.BackendDataSource;
import fr.univmobile.commons.datasource.Category;
import fr.univmobile.commons.datasource.PrimaryKey;
import fr.univmobile.commons.datasource.SearchAttribute;
import fr.univmobile.commons.datasource.Support;

@Category("comment_threads")
@PrimaryKey("uid")
@Support(data = CommentThread.class, builder = CommentThreadBuilder.class)
public interface CommentThreadDataSource extends
		BackendDataSource<CommentThread, CommentThreadBuilder> {

	@SearchAttribute("uid")
	CommentThread getByUid(int uid);

	boolean isNullByUid(int uid);

	@SearchAttribute("poiId")
	CommentThread getByPoiId(int poiId);
	
	boolean isNullByPoiId(int poiId);
}