package fr.univmobile.backend.search;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;

import fr.univmobile.backend.core.EntryRef;
import fr.univmobile.commons.datasource.Entry;

/**
 * Delegate to {@link SearchEngine}, that adds a method:
 * {@link #search(SearchContext, String)}
 */
public class SearchHelper implements SearchEngine {

	public SearchHelper(final SearchEngine delegate) {

		this.delegate = checkNotNull(delegate, "delegate");
	}

	private final SearchEngine delegate;

	@Override
	public EntryRef[] search(final SearchContext context,
			final SearchQuery query) throws IOException, SQLException {

		return delegate.search(context, query);
	}

	public final EntryRef[] search(final SearchContext context,
			final String query) throws IOException, SQLException {

		return search(context, new SearchQueryText(query));
	}

	public final boolean match(final String query, final Entry<?> entry)
			throws IOException, SQLException {

		return match(new SearchQueryText(query), entry);
	}

	public final boolean match(final SearchQuery query, final Entry<?> entry)
			throws IOException, SQLException {

		checkNotNull(entry, "entry");

		return new SearchEngineInMemory().search(new SearchContextInMemory(
				entry), query).length != 0;
	}

	public final <T extends EntryRef> T[] search(final Class<T> clazz,
			final String query) throws IOException, SQLException {

		final SearchContext context = delegate.newSearchContext(); // clazz);

		context.restrictTo(clazz);

		final EntryRef[] entryRefs = search(context, query);

		final Object array = Array.newInstance(clazz, entryRefs.length);

		for (int i = 0; i < entryRefs.length; ++i) {

			Array.set(array, i, entryRefs[i]);
		}

		@SuppressWarnings("unchecked")
		final T[] t = (T[]) array;

		return t;
	}

	@Override
	public SearchContext newSearchContext() throws IOException {

		return delegate.newSearchContext();
	}
}
