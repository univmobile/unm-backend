package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import net.avcompris.binding.dom.helper.DomBinderUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.backend.core.BackendDataSource;
import fr.univmobile.backend.core.Entry;
import fr.univmobile.backend.core.SearchAttribute;

public final class BackendDataSourceFileSystem<T extends BackendDataSource<U>, U extends Entry>
		extends BackendDataSourceImpl<U> implements InvocationHandler {

	public static <T extends BackendDataSource<U>, U extends Entry> T newDataSource(
			final Class<T> dataSourceClass, final Class<U> dataClass,
			final File dataDir) throws IOException {

		return new BackendDataSourceFileSystem<T, U>(dataSourceClass,
				dataClass, dataDir).getDataSource();
	}

	private final BackendDataCacheEngine<U> cacheEngine;

	private BackendDataSourceFileSystem(final Class<T> dataSourceClass,
			final Class<U> dataClass, final File dataDir) throws IOException {

		// 0. SUPER + INIT

		super(new BackendDataCacheEngine<U>(dataSourceClass, dataClass,
				new BackendDataEngineFileSystem<U>(dataDir)));

		cacheEngine = (BackendDataCacheEngine<U>) engine;

		this.dataDir = checkNotNull(dataDir, "dataDir");

		// 1. PROXY

		this.dataSourceClass = checkNotNull(dataSourceClass, "dataSourceClass");
		this.dataClass = checkNotNull(dataClass, "dataClass");

		final Object proxy = Proxy.newProxyInstance(
				BackendDataSourceFileSystem.class.getClassLoader(),
				new Class<?>[] { dataSourceClass }, this);

		dataSource = dataSourceClass.cast(proxy);

		// 9. DATA

		reload();
	}

	private final File dataDir;

	/**
	 * reload all data from the File System.
	 */
	public synchronized void reload() throws IOException {

		cacheEngine.clear();

		for (final File file : dataDir.listFiles()) {

			if (!file.isFile() || !file.getName().endsWith(".xml")) {
				continue;
			}

			final U data = DomBinderUtils.xmlContentToJava(file, dataClass);

			cacheEngine.cache(data);
		}
	}

	private final Class<T> dataSourceClass;
	private final Class<U> dataClass;

	private final T dataSource;

	public T getDataSource() {

		return dataSource;
	}

	private static final Log log = LogFactory.getLog( //
			BackendDataSourceFileSystem.class);

	@Override
	public Object invoke(final Object proxy, final Method method,
			final Object[] args) throws Throwable {

		final SearchAttribute searchAttribute = method
				.getAnnotation(SearchAttribute.class);

		final String methodName = method.getName();

		if (searchAttribute != null) {

			final String attributeName = searchAttribute.value();

			if (!methodName.startsWith("getBy")) {
				throw new NotImplementedException(
						"Method name should be of the form \"getByXxx()\": "
								+ methodName);
			}

			if (args == null || args.length != 1) {
				throw new NotImplementedException("Method " + methodName
						+ "(): args.length should be 1.");
			}

			return cacheEngine.getByAttribute(attributeName, args[0]);
		}

		if (log.isDebugEnabled()) {
			log.debug("No-@SearchAttribute method: " + methodName
					+ ". Forwarding invocation to \"this.\"");
		}

		if (methodName.startsWith("isNullBy") && args != null
				&& args.length == 1) {

			final String methodName2 = "getBy" + methodName.substring(8);

			final SearchAttribute searchAttribute2 = dataSourceClass.getMethod(
					methodName2, method.getParameterTypes()).getAnnotation(
					SearchAttribute.class);

			if (searchAttribute2 == null) {
				throw new RuntimeException("methodName: " + methodName
						+ ", inferring: " + methodName2
						+ ", but has no @SearchAttribute annotation.");
			}

			final String attributeName = searchAttribute2.value();

			return cacheEngine.isNullByAttribute(attributeName, args[0]);
		}

		try {

			return method.invoke(this, args);

		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Override
	public U getLatest(final U data) {

		checkNotNull(data, "data");

		return cacheEngine.getAllVersions(data).iterator().next();
	}

	@Override
	public boolean isLatest(final U data) {

		checkNotNull(data, "data");

		return data.getId().equals(getLatest(data).getId());
	}

	@Override
	public Map<String, U> getAllBy(final String attributeName) {

		return cacheEngine.getAllBy(attributeName);
	}
}
