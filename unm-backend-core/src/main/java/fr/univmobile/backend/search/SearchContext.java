package fr.univmobile.backend.search;

import java.io.IOException;
import java.sql.SQLException;

import fr.univmobile.backend.core.EntryRef;

public interface SearchContext {

	int size() throws IOException, SQLException;

	void restrictTo(Class<? extends EntryRef> clazz);
}
