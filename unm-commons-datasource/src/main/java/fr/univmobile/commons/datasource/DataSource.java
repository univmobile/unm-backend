package fr.univmobile.commons.datasource;

import java.util.Map;

public interface DataSource<E> {

	public <K> Map<K, E> getAllBy(Class<K> keyClass, String attributeName);

	public Map<String, E> getAllByString(String attributeName);

	public Map<Integer, E> getAllByInt(String attributeName);
}
