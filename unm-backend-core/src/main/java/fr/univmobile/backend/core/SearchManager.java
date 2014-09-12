package fr.univmobile.backend.core;

import java.io.IOException;
import java.sql.SQLException;

public interface SearchManager {

	void inject(SearchEntry entry) throws IOException, SQLException;
}
