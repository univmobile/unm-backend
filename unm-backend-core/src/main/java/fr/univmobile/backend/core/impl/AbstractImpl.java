package fr.univmobile.backend.core.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.avcompris.binding.dom.DomBinder;
import net.avcompris.binding.dom.impl.DefaultDomBinder;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.univmobile.commons.datasource.Entry;

/**
 * Common code for the core implementations.
 */
abstract class AbstractImpl extends DbEnabled {

	protected AbstractImpl(final ConnectionType dbType, final Connection cxn,
			final String... resourceNames) throws IOException,
			ParserConfigurationException {

		super(dbType, cxn, resourceNames);

		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();

		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setValidating(false);

		documentBuilder = documentBuilderFactory.newDocumentBuilder();
	}

	private final DomBinder domBinder = DefaultDomBinder.getInstance();
	private final DocumentBuilder documentBuilder;

	// private static final Log log = LogFactory.getLog(AbstractImpl.class);

	private final Document loadDocument(final File xmlFile) throws IOException,
			SAXException {

		final InputStream is = new FileInputStream(xmlFile);
		try {

			return documentBuilder.parse(is);

		} finally {
			is.close();
		}
	}

	protected final <U extends Entry<U>> U loadEntity(final File xmlFile,
			final Class<U> clazz) throws IOException, SAXException {

		return domBinder.bind(loadDocument(xmlFile), clazz);
	}

	protected final Entry<?> loadEntity(final File xmlFile) throws IOException,
			SAXException {

		return domBinder.bind(loadDocument(xmlFile), Entry.class);
	}
}
