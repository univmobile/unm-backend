package fr.univmobile.backend.core.impl;

import java.sql.Connection;
import java.util.Map;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.backend.core.CommentThread;
import fr.univmobile.backend.core.CommentThreadDataSource;

public class CommentThreadDataSourceImpl extends AbstractDbDataSource implements
		CommentThreadDataSource {

	public CommentThreadDataSourceImpl(final ConnectionType dbType,
			final Connection cxn) {

		super(dbType, cxn);
	}

	@Override
	public CommentThread getByUid(final int uid) {

		throw new NotImplementedException();
	}

	@Override
	public boolean isNullByUid(final int uid) {

		throw new NotImplementedException();
	}

	@Override
	public CommentThread getByPoiId(final int poiId) {

		throw new NotImplementedException();
	}

	@Override
	public boolean isNullByPoiId(final int poiId) {

		throw new NotImplementedException();
	}

	@Override
	public <K> Map<K, CommentThread> getAllBy(final Class<K> keyClass,
			final String attributeName) {

		throw new NotImplementedException();
	}

	@Override
	public Map<String, CommentThread> getAllByString(final String attributeName) {

		throw new NotImplementedException();
	}

	@Override
	public Map<Integer, CommentThread> getAllByInt(final String attributeName) {

		throw new NotImplementedException();
	}
}
