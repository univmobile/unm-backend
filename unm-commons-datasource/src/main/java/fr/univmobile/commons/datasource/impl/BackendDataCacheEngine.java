package fr.univmobile.commons.datasource.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.commons.datasource.BackendDataSource;
import fr.univmobile.commons.datasource.Entry;
import fr.univmobile.commons.datasource.EntryBuilder;
import fr.univmobile.commons.datasource.SearchAttribute;

final class BackendDataCacheEngine<E extends Entry, EB extends EntryBuilder<E>>
		implements BackendDataEngine<E, EB> {

	public BackendDataCacheEngine(
			final Class<? extends BackendDataSource<E, EB>> dataSourceClass) {

		checkNotNull(dataSourceClass, "dataSourceClass");

		this.dataClass = BackendDataUtils.getDataClass(dataSourceClass);

		// 2. INDEXES

		// 2.1. COREATTRIBUTES

		indexes.put("id", new HashMap<Object, List<E>>());

		putAttributeMethod("id");

		indexes.put("parentId", new HashMap<Object, List<E>>());

		putAttributeMethod("parentId");

		// 2.2. OTHERS

		for (final Method method : dataSourceClass.getMethods()) {

			final SearchAttribute searchAttribute = method
					.getAnnotation(SearchAttribute.class);

			if (searchAttribute == null) {
				continue;
			}

			final String attributeName = searchAttribute.value();

			indexes.put(attributeName, new HashMap<Object, List<E>>());

			putAttributeMethod(attributeName);
		}
	}

	private final Class<E> dataClass;

	private void putAttributeMethod(final String attributeName) {

		final Method dataMethod;
		try {

			dataMethod = dataClass.getMethod("get" + capitalize(attributeName));

		} catch (final NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		methods.put(attributeName, dataMethod);

	}

	private final Map<String, Method> methods = new HashMap<String, Method>();

	@Override
	public void store(final E data) {

		cache(data);

		// delegate.store(data);

		throw new NotImplementedException();
	}

	public synchronized void cache(final E data) {

		checkNotNull(data, "data");

		for (final Map.Entry<String, Method> entry : methods.entrySet()) {

			final String attributeName = entry.getKey(); // e.g. "remoteUser"
			final Method method = entry.getValue(); // e.g. "getRemoteUser()"

			final Object attributeValue;

			try {

				attributeValue = method.invoke(data);

			} catch (final InvocationTargetException e) {
				throw new RuntimeException(e.getTargetException());
			} catch (final IllegalAccessException e) {
				throw new RuntimeException(e);
			}

			if (attributeValue == null) {
				continue;
			}
			
			if (attributeValue instanceof String) {
				
				if (isBlank((String) attributeValue)) {
					continue;
				}

				// OK

			} else if (attributeValue instanceof Integer) {
				
				// OK
				
			} else {
				throw new IllegalStateException("Unknown key type: "
						+attributeValue.getClass().getName()
						+" for attribute: "+attributeName);
			}

			final Map<Object, List<E>> index = indexes.get(attributeName);

			final String attributeValueAsString = attributeValue.toString();

			List<E> cached = index.get(attributeValueAsString);

			if (cached == null) {
				cached = new ArrayList<E>();
				index.put(attributeValueAsString, cached);
			}

			boolean isParent = false;

			final String id = data.getId();

			for (final E E : cached) {
				if (!E.isNullParent() && id.equals(E.getParentId())) {
					isParent = true;
					break;
				}
			}

			if (isParent) {
				cached.add(data);
			} else {
				cached.add(0, data); // First element is HEAD
			}
		}
	}

	@Override
	public E getById(final String id) {

		return getByAttribute("id", id);
	}

	public E getByAttribute(final String attributeName,
			final Object attributeValue) {

		if (attributeValue == null) {
			throw new IllegalArgumentException("getBy: " + attributeName + "="
					+ attributeValue);
		}

		final Map<Object, List<E>> index = indexes.get(attributeName);

		final List<E> data = index.get(attributeValue);

		if (data == null || data.isEmpty()) {
			throw new NoSuchElementException("Cannot find cached data for: "
					+ attributeName + "=" + attributeValue);
		}

		return data.iterator().next(); // HEAD is first element.
	}

	public boolean isNullByAttribute(final String attributeName,
			final Object attributeValue) {

		if (attributeValue == null) {
			throw new IllegalArgumentException("isNullBy: " + attributeName
					+ "=" + attributeValue);
		}

		final Map<Object, List<E>> index = indexes.get(attributeName);

		final List<E> data = index.get(attributeValue);

		return data == null || data.isEmpty();
	}

	public synchronized void clear() {

		for (final Map<Object, List<E>> index : indexes.values()) {

			index.clear();
		}
	}

	private final Map<String, Map<Object, List<E>>> indexes = //
	new HashMap<String, Map<Object, List<E>>>();

	public List<E> getAllVersions(final E data) {

		final List<E> allVersions = new ArrayList<E>();

		allVersions.add(data);

		addAllDescendants(allVersions, data);

		addAllAscendants(allVersions, data);

		return allVersions;
	}

	private void addAllDescendants(final List<E> allVersions, final E data) {

		final List<E> children = indexes.get("parentId").get(data.getId());

		if (children == null) {
			return;
		}

		for (final E child : children) {

			allVersions.add(0, child);

			addAllDescendants(allVersions, child);
		}
	}

	private void addAllAscendants(final List<E> allVersions, final E data) {

		if (data.isNullParent()) {
			return;
		}

		for (final E parent : indexes.get("id").get(data.getParentId())) {

			allVersions.add(parent);

			addAllAscendants(allVersions, parent);
		}
	}

	public <K> Map<K, E> getAllBy(
			final Class<K> keyClass, 
			final String attributeName) {

		final Map<K, E> all = new HashMap<K, E>();

		for (final Map.Entry<Object, List<E>> entry : indexes
				.get(attributeName).entrySet()) {

			final Object attributeValue = entry.getKey();

			all.put(keyClass.cast(attributeValue), //
					entry.getValue().iterator().next());
		}

		return all;
	}
}