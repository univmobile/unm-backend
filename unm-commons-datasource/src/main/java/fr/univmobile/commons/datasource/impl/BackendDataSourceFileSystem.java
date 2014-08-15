package fr.univmobile.commons.datasource.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import net.avcompris.binding.dom.helper.DomBinderUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.commons.datasource.BackendDataSource;
import fr.univmobile.commons.datasource.Entry;
import fr.univmobile.commons.datasource.EntryBuilder;
import fr.univmobile.commons.datasource.SearchAttribute;

public final class BackendDataSourceFileSystem<S extends BackendDataSource<E, EB>, E extends Entry, EB extends EntryBuilder<E>>
		extends BackendDataSourceImpl<S, E, EB> implements InvocationHandler {

	public static <S extends BackendDataSource<E, EB>, E extends Entry, EB extends EntryBuilder<E>> S newDataSource(
			final Class<S> dataSourceClass, final File dataDir)
			throws IOException {

		return new BackendDataSourceFileSystem<S, E, EB>(dataSourceClass,
				dataDir).getDataSource();
	}

	private final BackendDataCacheEngine<E, EB> cacheEngine;

	private BackendDataSourceFileSystem(final Class<S> dataSourceClass,
			final File dataDir) throws IOException {

		// 0. SUPER + INIT

		super(dataSourceClass, new BackendDataCacheEngine<E, EB>(
				dataSourceClass));

		cacheEngine = (BackendDataCacheEngine<E, EB>) engine;

		this.dataDir = checkNotNull(dataDir, "dataDir");

		if (!dataDir.isDirectory()) {
			throw new FileNotFoundException("Cannot find dataDir: "
					+ dataDir.getCanonicalPath());
		}

		// 1. PROXY

		this.dataSourceClass = checkNotNull(dataSourceClass, "dataSourceClass");
		this.dataClass = BackendDataUtils.getDataClass(dataSourceClass);

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

		if (log.isDebugEnabled()) {
			log.debug("Listing files in: " + dataDir.getCanonicalPath() + "...");
		}

		final File[] files = dataDir.listFiles();

		if (files == null) {
			throw new IOException("Cannot list files in dataDir: "
					+ dataDir.getCanonicalPath());
		}

		for (final File file : files) {

			if (!file.isFile() || !file.getName().endsWith(".xml")) {
				continue;
			}

			if (log.isDebugEnabled()) {
				log.debug("Reading XML file: " + file.getCanonicalPath()
						+ "...");
			}

			final E data = DomBinderUtils.xmlContentToJava(file, dataClass);

			cacheEngine.cache(data);
		}
	}

	private final Class<S> dataSourceClass;
	private final Class<E> dataClass;

	private final S dataSource;

	public S getDataSource() {

		return dataSource;
	}

	private static final Log log = LogFactory.getLog( //
			BackendDataSourceFileSystem.class);

	@Override
	public Object invoke(final Object proxy, final Method method,
			final Object[] args) throws Throwable {

		final String methodName = method.getName();

		if (log.isDebugEnabled()) {
			log.debug("invoke:" + methodName + "()...");
		}

		if ("reload".equals(methodName)) {

			reload();

			return null;
		}

		final SearchAttribute searchAttribute = method
				.getAnnotation(SearchAttribute.class);

		if (searchAttribute != null) {

			final String attributeName = searchAttribute.value();

			if (!methodName.startsWith("getBy")) {
				throw new NotImplementedException(
						"Method name should be of the form \"getByXxx()\": "
								+ methodName + "()");
			}

			if (args == null || args.length != 1) {
				throw new NotImplementedException("Method " + methodName
						+ "(): args.length should be 1.");
			}

			return cacheEngine.getByAttribute(attributeName, args[0]);
		}

		if (log.isDebugEnabled()) {
			log.debug("No-@SearchAttribute method: " + methodName
					+ "(). Forwarding invocation to \"this.\"");
		}

		if (methodName.startsWith("isNullBy") && args != null
				&& args.length == 1) {

			final String methodName2 = "getBy" + methodName.substring(8);

			final SearchAttribute searchAttribute2 = dataSourceClass.getMethod(
					methodName2, method.getParameterTypes()).getAnnotation(
					SearchAttribute.class);

			if (searchAttribute2 == null) {
				throw new RuntimeException("methodName: " + methodName
						+ "(), inferring: " + methodName2
						+ "(), but has no @SearchAttribute annotation.");
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
	public E getLatest(final E data) {

		checkNotNull(data, "data");

		return cacheEngine.getAllVersions(data).iterator().next();
	}

	@Override
	public boolean isLatest(final E data) {

		checkNotNull(data, "data");

		return data.getId().equals(getLatest(data).getId());
	}

	@Override
	public <K> Map<K, E> getAllBy(final Class<K> keyClass,
			final String attributeName) {

		return cacheEngine.getAllBy(keyClass, attributeName);
	}

	@Override
	public final Map<String, E> getAllByString(final String attributeName) {

		return getAllBy(String.class, attributeName);
	}

	@Override
	public final Map<Integer, E> getAllByInt(final String attributeName) {

		return getAllBy(Integer.class, attributeName);
	}

	@Override
	protected void save(final Document document, final EB builder)
			throws IOException {

		@SuppressWarnings("unchecked")
		final E data = (E) builder;

		final String primaryKey = BackendDataUtils.getPrimaryKey(data,
				dataSourceClass);
		File file = null;

		synchronized (this) { // TODO Use a real transactional lock.

			while (true) {

				final String filename = primaryKey + "_"
						+ System.currentTimeMillis() + "_"
						+ RandomUtils.nextInt(10000000, 99999999) + ".xml";

				file = new File(dataDir, filename);

				if (file.isFile()) { // If file already exists, use another name
					continue;
				}

				FileUtils.touch(file); // Acquire lock

				break;
			}
		}

		if (log.isWarnEnabled()) {
			log.warn("Saving: " + file.getCanonicalPath());
		}

		final String id = "fr.univmobile:unm-backend:" //
				+ dataDir.getName() + ":" //
				+ substringBeforeLast(file.getName(), ".xml");

		builder.setId(id);
		builder.setSelf(id);

		dump(document, file);

		cacheEngine.cache(data);
	}
}
