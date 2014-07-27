package fr.univmobile.backend.core.impl;

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

import net.avcompris.binding.dom.helper.DomBinderUtils;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.univmobile.backend.core.BackendDataSource;
import fr.univmobile.backend.core.Category;
import fr.univmobile.backend.core.Entry;
import fr.univmobile.backend.core.EntryBuilder;

abstract class BackendDataSourceImpl<S extends BackendDataSource<E, EB>, E extends Entry, EB extends EntryBuilder<E>>
		implements BackendDataSource<E, EB> {

	protected BackendDataSourceImpl(final Class<S> dataSourceClass,
			final BackendDataEngine<E, EB> engine
	) {
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

		final Object proxy = Proxy.newProxyInstance(
				dataSourceClass.getClassLoader(), new Class[] { builderClass },
				new EntryBuilderWrapper<E, EB>(document, entryBuilder));

		return builderClass.cast(proxy);
	}

	private class EntryBuilderWrapper<E_ extends E, EB_ extends EB> implements
			InvocationHandler {

		public EntryBuilderWrapper(final Document document, final EB delegate) {

			this.document = checkNotNull(document, "document");
			this.delegate = checkNotNull(delegate, "delegate");
			
			@SuppressWarnings("unchecked")
			final E entry = (E) delegate;

			data = entry;
		}

		private final Document document;
		private final EB delegate;
		private final E data;

		@Override
		public Object invoke(final Object proxy, final Method method,
				final Object[] args) throws Throwable {

			if ("save".equals(method.getName())) {

				BackendDataSourceImpl.this.save(document, data);

				return delegate;
			}

			if ("dump".equals(method.getName())) {

				dump(document, (File)args[0]);
				
				return null;
			}

			return method.invoke(delegate, args);
		}
	}

	protected abstract void save(Document document, E data) throws IOException;

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

	private static Document createDocument()
			throws ParserConfigurationException {

		final DocumentBuilder documentBuilder = DocumentBuilderFactory
				.newInstance().newDocumentBuilder();

		final Document document = documentBuilder.newDocument();

		final Element rootElement = document.createElementNS(NSURI, "atom:entry");
		
		document.appendChild(rootElement);

		final Element updatedElement = document.createElementNS(NSURI,
				"atom:updated");

		rootElement.appendChild(updatedElement);

		final DateTime now = new DateTime();

		updatedElement.appendChild(document.createTextNode(now.toString()));

		return document;
	}
}
