package fr.univmobile.commons.datasource;

import java.io.IOException;
import java.util.Map;

public interface BackendDataSource<E extends Entry, EB extends EntryBuilder<E>> {

	E getById(String id);

	E getParent(E data);

	boolean hasParent(E data);

	E getLatest(E data);

	boolean isLatest(E data);

	public Map<String, E> getAllBy(String attributeName);
	
	EB create();
	
	EB update(E data);
	
	void reload() throws IOException;
}
