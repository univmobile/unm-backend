package fr.univmobile.commons.datasource;

import java.io.IOException;
import java.util.Map;

public interface BackendDataSource<E extends Entry<E>, EB extends EntryBuilder<E>> {

	E getById(String id);

	E getParent(E data);

	boolean hasParent(E data);

	E getLatest(E data);

	boolean isLatest(E data);

	public <K> Map<K, E> getAllBy(Class<K> keyClass, String attributeName);

	public Map<String, E> getAllByString(String attributeName);

	public Map<Integer, E> getAllByInt(String attributeName);

	EB create();

	EB update(E data);
	
	E reload(E data) throws IOException;

	void reload() throws IOException;
}
