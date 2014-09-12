package fr.univmobile.backend.search;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.avcompris.lang.NotImplementedException;
import com.google.common.collect.Iterables;

import fr.univmobile.backend.core.EntryRef;
import fr.univmobile.commons.datasource.Entry;

/**
 * Implementation of {@link SearchEngine} that only knows to parse entities
 * loaded in memory.
 */
public final class SearchEngineInMemory implements SearchEngine {

	public static final EntryRef[] EMPTY = new EntryRef[0];

	@Override
	public EntryRef[] search(final SearchContext context,
			final SearchQuery query) throws IOException, SQLException {

		checkNotNull(query, "query");
		checkNotNull(context, "context");

		if (context.size() == 0) {
			return EMPTY;
		}

		final Matcher matcher = new Matcher(query);

		if (!SearchContextInMemory.class.isInstance(context)) {
			throw new NotImplementedException("context.class: "
					+ context.getClass());
		}

		final List<EntryRef> entries = new ArrayList<EntryRef>();

		for (final Entry<?> entry : ((SearchContextInMemory) context).entries()) {

			if (matcher.match(entry)) {

				if (!EntryRef.class.isInstance(entry)) {

					throw new ClassCastException(
							"Entry should be a subclass of EntryRef.class: "
									+ entry.getClass().getName());
				}

				entries.add((EntryRef) entry);
			}
		}

		return Iterables.toArray(entries, EntryRef.class);
	}

	@Override
	public SearchContext newSearchContext() {

		throw new NotImplementedException();
	}
}
