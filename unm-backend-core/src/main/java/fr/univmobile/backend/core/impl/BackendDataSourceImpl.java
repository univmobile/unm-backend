package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.backend.core.BackendDataSource;
import fr.univmobile.backend.core.Entry;

abstract class BackendDataSourceImpl<T extends Entry> implements
		BackendDataSource<T> {

	protected BackendDataSourceImpl(final BackendDataEngine<T> engine) {

		this.engine = checkNotNull(engine, "engine");
	}

	protected final BackendDataEngine<T> engine;

	@Override
	public final T getById(final String id) {

		return engine.getById(id);
	}

	@Override
	public final T getParent(final T data) {

		checkNotNull(data, "data");
		
		if (data.isNullParent()) {
			throw new NoSuchElementException("data has no parent: "+data);
		}
		
		final String parentId = data.getParentId();
		
		return getById(parentId);
	}

	@Override
	public final boolean hasParent(final T data) {

		checkNotNull(data, "data");
		
		return !data.isNullParent();
	}

	@Override
	public final T getLatest(final T data) {

		checkNotNull(data, "data");
		throw new NotImplementedException();
	}

	@Override
	public final boolean isLatest(final T data) {
		checkNotNull(data, "data");

		throw new NotImplementedException();
	}
}
