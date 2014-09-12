package fr.univmobile.backend.sysadmin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.avcompris.binding.dom.DomBinder;
import net.avcompris.binding.dom.impl.DefaultDomBinder;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.univmobile.backend.core.impl.ConnectionType;
import fr.univmobile.backend.core.impl.DbEnabled;
import fr.univmobile.commons.datasource.Entry;

/**
 * Common code for the command-line tools.
 */
abstract class AbstractTool extends DbEnabled {

	protected AbstractTool(final ConnectionType dbType, final Connection cxn)
			throws IOException, ParserConfigurationException {

		super(dbType, cxn, //
				"sysadmin_sql.yaml", "indexation_sql.yaml", "core_sql.yaml");

		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();

		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setValidating(false);

		documentBuilder = documentBuilderFactory.newDocumentBuilder();
	}

	private final DomBinder domBinder = DefaultDomBinder.getInstance();
	private final DocumentBuilder documentBuilder;

	// private static final Log log = LogFactory.getLog(AbstractTool.class);

	@Nullable
	public abstract Result run() throws IOException, SQLException, SAXException;

	protected final File getCategoryDir(final String category)
			throws SQLException {

		File categoryDir = categoryDirs.get(category);

		if (categoryDir != null) {
			return categoryDir;
		}

		final String categoryPath = executeQueryGetString("getCategoryPath",
				category);

		categoryDir = new File(categoryPath);

		categoryDirs.put(category, categoryDir);

		return categoryDir;
	}

	protected final void setCategoryDir(final String category,
			final File categoryDir) {

		categoryDirs.put(category, categoryDir);
	}

	private final Map<String, File> categoryDirs = new HashMap<String, File>();

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
