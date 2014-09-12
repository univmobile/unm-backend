package fr.univmobile.backend.search;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

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
			final SearchQuery query) throws IOException {

		return delegate.search(context, query);
	}

	public final EntryRef[] search(final SearchContext context,
			final String query) throws IOException {

		return search(context, new SearchQueryText(query));
	}

	public final boolean match(final String query, final Entry<?> entry)
			throws IOException {

		return match(new SearchQueryText(query), entry);
	}

	public final boolean match(final SearchQuery query, final Entry<?> entry)
			throws IOException {

		checkNotNull(entry,"entry");
		
		return new SearchEngineInMemory().search(
				new SearchContextInMemory(entry),
				query).length != 0;
	}
}
