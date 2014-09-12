package fr.univmobile.backend.core;

import java.io.IOException;
import java.sql.SQLException;

import fr.univmobile.backend.search.SearchEngine;

public interface SearchManager extends SearchEngine {

	void inject(SearchEntry entry) throws IOException, SQLException;
}
