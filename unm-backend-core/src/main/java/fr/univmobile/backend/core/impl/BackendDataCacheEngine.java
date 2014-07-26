package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.capitalize;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.avcompris.lang.NotImplementedException;

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

		// 2.1. "id" ATTRIBUTE

		indexes.put("id", new HashMap<String, T>());

		putAttributeMethod("id");

		// 2.2. OTHERS

		for (final Method method : dataSourceClass.getMethods()) {

			final SearchAttribute searchAttribute = method
					.getAnnotation(SearchAttribute.class);

			if (searchAttribute == null) {
				continue;
			}

			final String attributeName = searchAttribute.value();

			indexes.put(attributeName, new HashMap<String, T>());

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

	public void cache(final T data) {

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

			indexes.get(attributeName).put(attributeValue.toString(), data);
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

		final Map<String, T> index = indexes.get(attributeName);

		final T data = index.get(attributeValue);

		if (data == null) {
			throw new NoSuchElementException("Cannot find cached data for: "
					+ attributeName + "=" + attributeValue);
		}

		return data;
	}

	public void clear() {

		for (final Map<String, T> index : indexes.values()) {

			index.clear();
		}
	}

	private final Map<String, Map<String, T>> indexes = new HashMap<String, Map<String, T>>();
}