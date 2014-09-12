package fr.univmobile.backend.search;

import java.io.IOException;
import java.sql.SQLException;

import fr.univmobile.backend.core.EntryRef;

public interface SearchEngine {

	/**
	 * A search invocation returns an ordered list of {@link EntryRef} objects.
	 * The idea behind {@link EntryRef} objects is to manipulate only shallow
	 * objects, with identifiers mainly.
	 */
	EntryRef[] search(SearchContext context, SearchQuery query)
			throws IOException, SQLException;

	SearchContext newSearchContext() throws IOException;
}
