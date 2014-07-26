package fr.univmobile.backend.core.impl;

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

import fr.univmobile.backend.core.BackendDataSource;
import fr.univmobile.backend.core.Entry;
import fr.univmobile.backend.core.SearchAttribute;

final class BackendDataCacheEngine<T extends Entry> implements
		BackendDataEngine<T> {

	public BackendDataCacheEngine(
			final Class<? extends BackendDataSource<T>> dataSourceClass,
			final Class<T> dataClass, final BackendDataEngine<T> delegate) {

		this.delegate = checkNotNull(delegate, "delegate");

		checkNotNull(dataSourceClass, "dataSourceClass");

		this.dataClass = checkNotNull(dataClass, "dataClass");

		// 2. INDEXES

		// 2.1. COREATTRIBUTES

		indexes.put("id", new HashMap<String, List<T>>());

		putAttributeMethod("id");

		indexes.put("parentId", new HashMap<String, List<T>>());

		putAttributeMethod("parentId");

		// 2.2. OTHERS

		for (final Method method : dataSourceClass.getMethods()) {

			final SearchAttribute searchAttribute = method
					.getAnnotation(SearchAttribute.class);

			if (searchAttribute == null) {
				continue;
			}

			final String attributeName = searchAttribute.value();

			indexes.put(attributeName, new HashMap<String, List<T>>());

			putAttributeMethod(attributeName);
		}
	}

	private final Class<T> dataClass;

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

	private final BackendDataEngine<T> delegate;

	@Override
	public void store(final T data) {

		cache(data);

		delegate.store(data);
	}

	public synchronized void cache(final T data) {

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

			if (isBlank((String) attributeValue)) {
				continue;
			}

			final Map<String, List<T>> index = indexes.get(attributeName);

			final String attributeValueAsString = attributeValue.toString();

			List<T> cached = index.get(attributeValueAsString);

			if (cached == null) {
				cached = new ArrayList<T>();
				index.put(attributeValueAsString, cached);
			}

			boolean isParent = false;

			final String id = data.getId();

			for (final T t : cached) {
				if (!t.isNullParent() && id.equals(t.getParentId())) {
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
	public T getById(final String id) {

		return getByAttribute("id", id);
	}

	public T getByAttribute(final String attributeName,
			final Object attributeValue) {

		if (attributeValue == null) {
			throw new IllegalArgumentException("getBy: " + attributeName + "="
					+ attributeValue);
		}

		final Map<String, List<T>> index = indexes.get(attributeName);

		final List<T> data = index.get(attributeValue);

		if (data == null || data.isEmpty()) {
			throw new NoSuchElementException("Cannot find cached data for: "
					+ attributeName + "=" + attributeValue);
		}

		return data.iterator().next(); // HEAD is first element.
	}

	public boolean isNullByAttribute(final String attributeName,
			final Object attributeValue) {

		if (attributeValue == null) {
			throw new IllegalArgumentException("isNullBy: " + attributeName + "="
					+ attributeValue);
		}

		final Map<String, List<T>> index = indexes.get(attributeName);

		final List<T> data = index.get(attributeValue);

		return data == null || data.isEmpty();
	}

	public synchronized void clear() {

		for (final Map<String, List<T>> index : indexes.values()) {

			index.clear();
		}
	}

	private final Map<String, Map<String, List<T>>> indexes = //
	new HashMap<String, Map<String, List<T>>>();

	public List<T> getAllVersions(final T data) {

		final List<T> allVersions = new ArrayList<T>();

		allVersions.add(data);

		addAllDescendants(allVersions, data);

		addAllAscendants(allVersions, data);

		return allVersions;
	}

	private void addAllDescendants(final List<T> allVersions, final T data) {

		final List<T> children = indexes.get("parentId").get(data.getId());

		if (children == null) {
			return;
		}

		for (final T child : children) {

			allVersions.add(0,child);

			addAllDescendants(allVersions, child);
		}
	}

	private void addAllAscendants(final List<T> allVersions, final T data) {

		if (data.isNullParent()) {
			return;
		}

		for (final T parent : indexes.get("id").get(data.getParentId())) {

			allVersions.add(parent);

			addAllAscendants(allVersions, parent);
		}
	}
}