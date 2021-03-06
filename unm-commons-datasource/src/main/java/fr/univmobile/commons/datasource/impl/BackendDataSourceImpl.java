package fr.univmobile.commons.datasource.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.CharEncoding.UTF_8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.avcompris.binding.Binding;
import net.avcompris.binding.dom.helper.DomBinderUtils;
import net.avcompris.binding.helper.BinderUtils;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.univmobile.commons.datasource.RevDataSource;
import fr.univmobile.commons.datasource.Category;
import fr.univmobile.commons.datasource.Entry;
import fr.univmobile.commons.datasource.EntryBuilder;

abstract class BackendDataSourceImpl<S extends RevDataSource<E, EB>, //
E extends Entry<E>, //
EB extends EntryBuilder<E>> //
		implements RevDataSource<E, EB> {

	protected BackendDataSourceImpl(final Class<S> dataSourceClass,
			final BackendDataEngine<E, EB> engine) {
		this.dataSourceClass = checkNotNull(dataSourceClass, "dataSourceClass");
		this.engine = checkNotNull(engine, "engine");
		this.builderClass = BackendDataUtils
				.<E, EB> getBuilderClass(dataSourceClass);
	}

	protected final BackendDataEngine<E, EB> engine;

	private final Class<S> dataSourceClass;
	private final Class<? extends EB> builderClass;

	@Override
	public final E getById(final String id) {

		return engine.getById(id);
	}

	@Override
	public final E getParent(final E data) {

		checkNotNull(data, "data");

		if (data.isNullParent()) {
			throw new NoSuchElementException("data has no parent: " + data);
		}

		final String parentId = data.getParentId();

		return getById(parentId);
	}

	@Override
	public final boolean hasParent(final E data) {

		checkNotNull(data, "data");

		return !data.isNullParent();
	}

	@Override
	public final EB create() {

		final Document document;

		try {

			document = createDocument();

		} catch (final ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

		final Element rootElement = document.getDocumentElement();

		final String category = BackendDataUtils.getInheritedAnnotation(
				dataSourceClass, Category.class).value();

		final Element categoryElement = document.createElementNS(NSURI,
				"atom:category");

		rootElement.appendChild(categoryElement);

		categoryElement.setAttribute("term", category);

		final EB entryBuilder = DomBinderUtils.xmlContentToJava(document,
				builderClass);

		final EntryBuilderWrapper<E, EB> wrapper = new EntryBuilderWrapper<E, EB>(
				document, entryBuilder);

		final Object proxy = Proxy.newProxyInstance(
				dataSourceClass.getClassLoader(), new Class[] { //
				builderClass, EntryBuilderImpl.class //
				}, wrapper);

		final EB builder = builderClass.cast(proxy);

		wrapper.setBuilder(builder);

		return builder;
	}

	@Override
	public final EB update(final E data) {

		checkNotNull(data, "data");

		// final EB rebind = BinderUtils.rebind(data, builderClass);

		final Binding<?> node = BinderUtils.rebind(data, Binding.class);

		final Document document = ((Node) node.node()).getOwnerDocument();

		final Document newDocument;

		try {

			newDocument = getDocumentBuilder().newDocument();

		} catch (final ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

		final Node newRoot = newDocument.importNode(
				document.getDocumentElement(), true);

		newDocument.appendChild(newRoot);

		final EB rebind = DomBinderUtils.xmlContentToJava(newDocument,
				builderClass);

		final EntryBuilderWrapper<E, EB> wrapper = new EntryBuilderWrapper<E, EB>(
				newDocument, rebind);

		final Object proxy = Proxy.newProxyInstance(
				dataSourceClass.getClassLoader(), new Class<?>[] { //
				builderClass, EntryBuilderImpl.class //
				}, wrapper);

		final EB builder = builderClass.cast(proxy);

		wrapper.setBuilder(builder);

		builder.setParentId(data.getId());

		return builder;
	}

	private class EntryBuilderWrapper<E_ extends E, EB_ extends EB> implements
			InvocationHandler {

		public EntryBuilderWrapper(final Document document, final EB delegate) {

			this.document = checkNotNull(document, "document");
			this.delegate = checkNotNull(delegate, "delegate");

			@SuppressWarnings("unchecked")
			final E entry = (E) delegate;
			
			this.entry=entry;

			wrapper = delegate;
		}

		private void setBuilder(final EB wrapper) {

			this.wrapper = checkNotNull(wrapper, "wrapper");
		}

		private final Document document;
		private final EB delegate;
		private final E entry;
		private EB wrapper;

		@Override
		public Object invoke(final Object proxy, final Method method,
				final Object[] args) throws Throwable {

			if ("save".equals(method.getName())) {

				BackendDataSourceImpl.this.save(document, delegate);

				return delegate;
			}

			if ("cache".equals(method.getName())) {

				BackendDataSourceImpl.this.cache(entry);

				return null;
			}

			if ("dump".equals(method.getName())) {

				dump(document, (File) args[0]);

				return null;
			}

			final Class<?> returnType = method.getReturnType();

			final Object value = method.invoke(delegate, args);

			if (returnType != null && returnType.isAssignableFrom(builderClass)) {

				return wrapper;

			} else {

				return value;
			}
		}
	}

	protected abstract void save(Document document, EB builder)
			throws IOException;

	protected abstract void cache(E data) throws IOException;

	protected static String dump(final Document document, final File outFile) {

		final Transformer transformer;

		try {

			transformer = TransformerFactory.newInstance().newTransformer();

		} catch (final TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}

		try {

			final Source input = new DOMSource(document);

			final OutputStream os = new FileOutputStream(outFile);
			try {

				final Writer writer = new OutputStreamWriter(os, UTF_8);

				final Result output = new StreamResult(writer);

				transformer.transform(input, output);

			} finally {
				os.close();
			}

			return FileUtils.readFileToString(outFile, UTF_8);

		} catch (final IOException e) {
			throw new RuntimeException(e);
		} catch (final TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	private static final String NSURI = "http://www.w3.org/2005/Atom";

	private static DocumentBuilder getDocumentBuilder()
			throws ParserConfigurationException {

		return DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	private static Document createDocument()
			throws ParserConfigurationException {

		final Document document = getDocumentBuilder().newDocument();

		final Element rootElement = document.createElementNS(NSURI,
				"atom:entry");

		document.appendChild(rootElement);

		final Element updatedElement = document.createElementNS(NSURI,
				"atom:updated");

		rootElement.appendChild(updatedElement);

		final DateTime now = new DateTime();

		updatedElement.appendChild(document.createTextNode(now.toString()));

		return document;
	}
}
