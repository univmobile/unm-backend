package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.backend.core.Entry;

final class BackendDataEngineFileSystem<T extends Entry> implements
		BackendDataEngine<T> {

	public BackendDataEngineFileSystem(final File dataDir) throws IOException {

		this.dataDir = checkNotNull(dataDir, "dataDir");

		if (!dataDir.isDirectory()) {
			throw new FileNotFoundException("dataDir should be a directory: "
					+ dataDir.getCanonicalPath());
		}
	}
	
	private final File dataDir;

	@Override
	public void store(final T data) {

		throw new NotImplementedException();
	}

	@Override
	public T getById(final String id) {

		throw new NotImplementedException();
	}
}
