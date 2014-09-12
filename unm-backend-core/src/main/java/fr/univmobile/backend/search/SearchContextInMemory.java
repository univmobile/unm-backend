package fr.univmobile.backend.search;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.backend.core.EntryRef;
import fr.univmobile.commons.datasource.Entry;

public final class SearchContextInMemory implements SearchContext {

	public SearchContextInMemory() {

	}

	public SearchContextInMemory(final Entry<?> entry) {

		add(entry);
	}

	public SearchContextInMemory add(final Entry<?> entry) {

		checkNotNull(entry, "entry");

		entries.add(entry);

		return this;
	}

	private final Set<Entry<?>> entries = new HashSet<Entry<?>>();

	@Override
	public int size() throws IOException {

		if (clazz != null) {
			throw new NotImplementedException();
		}

		return entries.size();
	}

	public Iterable<Entry<?>> entries() {

		if (clazz != null) {
			throw new NotImplementedException();
		}

		return entries;
	}

	@Override
	public void restrictTo(final Class<? extends EntryRef> clazz) {

		this.clazz = checkNotNull(clazz, "clazz");
	}

	private Class<? extends EntryRef> clazz = null;
}
