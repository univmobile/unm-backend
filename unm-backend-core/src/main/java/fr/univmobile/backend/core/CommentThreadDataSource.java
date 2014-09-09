package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.DataSource;

public interface CommentThreadDataSource extends DataSource<CommentThread> {

	CommentThread getByUid(int uid);

	boolean isNullByUid(int uid);

	CommentThread getByPoiId(int poiId);

	boolean isNullByPoiId(int poiId);
}