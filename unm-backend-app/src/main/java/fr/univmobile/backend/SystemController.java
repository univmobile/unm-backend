package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.univmobile.commons.DataBeans.instantiate;
import static fr.univmobile.commons.FormatUtils.formatFileLength;
import static fr.univmobile.commons.FormatUtils.formatMemory;
import static org.apache.commons.lang3.StringUtils.split;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.avcompris.binding.annotation.Namespaces;
import net.avcompris.binding.annotation.XPath;
import net.avcompris.binding.dom.helper.DomBinderUtils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.univmobile.backend.core.impl.DbEnabled;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "system", "system/" })
public class SystemController extends AbstractBackendController {

	public SystemController(final DataSource ds) {

		this.ds = checkNotNull(ds, "ds");
	}

	private final DataSource ds;

	@Override
	public View action() throws Exception {

		final WebXml webXml;

		final InputStream is1 = getServletContext().getResourceAsStream(
				"/WEB-INF/web.xml");
		if (is1 == null) {
			throw new FileNotFoundException(
					"Cannot find resource: /WEB-INF/web.xml");
		}
		try {

			webXml = DomBinderUtils.xmlContentToJava(is1, WebXml.class);

		} finally {
			is1.close();
		}

		final String logFilePath = loadLogFilePath();

		final Runtime runtime = Runtime.getRuntime();

		final String dbUrl;

		final Connection cxn = ds.getConnection();
		try {

			dbUrl = cxn.getMetaData().getURL();

		} finally {
			cxn.close();
		}

		final String dataDirPath = webXml.getDataDir();

		final String tablePrefix = DbEnabled.TABLE_PREFIX;

		final SystemInfo systemInfo = instantiate(SystemInfo.class) //
				.setDataDir(dataDirPath) //
				.setLogFile(logFilePath) //
				.setDbUrl(dbUrl) //
				.setTablePrefix(tablePrefix) //
				.setFreeMemory(formatMemory(runtime.freeMemory())) //
				.setTotalMemory(formatMemory(runtime.totalMemory())) //
				.setMaxMemory(formatMemory(runtime.maxMemory()));

		setAttribute("systemInfo", systemInfo);

		final String optionalJsonBaseURLs = webXml.getOptionalJsonBaseURLs();

		if (optionalJsonBaseURLs != null) {
			for (final String optionalJsonBaseURL : split(optionalJsonBaseURLs)) {
				systemInfo.addToOptionalJsonBaseURLs(optionalJsonBaseURL);
			}
		}

		if (dataDirPath == null) {
			systemInfo.setLogFileError("No Data Directory is declared.");
		} else {
			final File dataDir = new File(dataDirPath);
			if (dataDir.isDirectory()) {
				systemInfo.setDataDir(dataDir.getCanonicalPath());
			} else {
				systemInfo.setDataDirError("Cannot access Data Directory");
			}
		}

		if (logFilePath == null) {
			systemInfo.setLogFileError("No Log File is declared.");
		} else {
			final File logFile = new File(logFilePath);
			if (logFile.isFile()) {
				systemInfo.setLogFileLength(formatFileLength(logFile.length()));
				systemInfo.setLogFile(logFile.getCanonicalPath());
			} else {
				systemInfo.setLogFileError("Cannot access Log File");
			}
		}

		addDbTableInfo(systemInfo, tablePrefix + "categories");
		addDbTableInfo(systemInfo, tablePrefix + "revfiles");
		addDbTableInfo(systemInfo, tablePrefix + "entities_users");
		addDbTableInfo(systemInfo, tablePrefix + "entities_regions");
		addDbTableInfo(systemInfo, tablePrefix + "entities_comments");
		addDbTableInfo(systemInfo, tablePrefix + "entities_pois");
		addDbTableInfo(systemInfo, tablePrefix + "searchtokens");
		addDbTableInfo(systemInfo, tablePrefix + "search");

		// 9. END

		return new View("system.jsp");
	}

	private void addDbTableInfo(final SystemInfo systemInfo,
			final String tablename) throws SQLException {

		final SystemInfo.DbTable dbTable = instantiate(SystemInfo.DbTable.class)
				.setName(tablename);

		systemInfo.addToDbTables(dbTable);

		final int rowCount;

		final Connection cxn = ds.getConnection();
		try {
			final Statement stmt = cxn.createStatement();
			try {
				try {
					final ResultSet rs = stmt
							.executeQuery("SELECT COUNT(1) FROM " + tablename);
					try {

						rs.next();

						rowCount = rs.getInt(1);

					} finally {
						rs.close();
					}
				} catch (final SQLException e) {

					dbTable.setError(e.getMessage());

					return;
				}
			} finally {
				stmt.close();
			}
		} finally {
			cxn.close();
		}

		dbTable.setRowCount(rowCount);
	}

	@Namespaces("xmlns:j2ee=http://java.sun.com/xml/ns/j2ee")
	@XPath("/j2ee:web-app")
	private interface WebXml {

		@XPath("j2ee:servlet/j2ee:init-param"
				+ "[j2ee:param-name = 'dataDir']/j2ee:param-value")
		@Nullable
		String getDataDir();

		@XPath("j2ee:servlet/j2ee:init-param"
				+ "[j2ee:param-name = 'optional-jsonBaseURLs']/j2ee:param-value")
		@Nullable
		String getOptionalJsonBaseURLs();
	}

	@Namespaces("xmlns:log4j=http://jakarta.apache.org/log4j/")
	@XPath("/log4j:configuration")
	private interface Log4JXml {

		@XPath("root/appender-ref/@ref")
		@Nullable
		String getRootAppenderRef();

		@XPath("appender[@name = $arg0]/param[@name = 'File']/@value")
		String getAppenderFile(String appenderName);

		boolean isNullAppenderFile(String appenderName);
	}

	@Nullable
	static String loadLogFilePath() throws IOException,
			ParserConfigurationException, SAXException {

		final Log4JXml log4jXml;

		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
		documentBuilderFactory.setFeature(LOAD_EXTERNAL_DTD, false);
		final DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		final Document dom4;

		final InputStream is4 = SystemController.class.getClassLoader()
				.getResourceAsStream("log4j.xml");
		if (is4 == null) {
			throw new FileNotFoundException("Cannot find resource: log4j.xml");
		}
		try {

			dom4 = documentBuilder.parse(is4);

		} finally {
			is4.close();
		}

		log4jXml = DomBinderUtils.xmlContentToJava(dom4, Log4JXml.class);

		final String logFilePath;

		final String appenderName = log4jXml.getRootAppenderRef();
		if (appenderName == null) {
			logFilePath = null;
		} else if (log4jXml.isNullAppenderFile(appenderName)) {
			logFilePath = null;
		} else {
			logFilePath = log4jXml.getAppenderFile(appenderName);
		}

		return logFilePath;
	}
}

interface SystemInfo {

	String[] getOptionalJsonBaseURLs();

	SystemInfo addToOptionalJsonBaseURLs(String optionalJsonBaseURL);

	String getDataDir();

	@Nullable
	String getDataDirError();

	SystemInfo setDataDir(String dataDir);

	SystemInfo setDataDirError(String dataDirError);

	String getTablePrefix();

	SystemInfo setTablePrefix(String tablePrefix);

	String getDbUrl();

	SystemInfo setDbUrl(String dbUrl);

	String getFreeMemory();

	SystemInfo setFreeMemory(String freeMemory);

	String getTotalMemory();

	SystemInfo setTotalMemory(String totalMemory);

	String getMaxMemory();

	SystemInfo setMaxMemory(String maxMemory);

	String getLogFile();

	SystemInfo setLogFile(String logFile);

	String getLogFileLength();

	@Nullable
	String getLogFileError();

	SystemInfo setLogFileLength(String logFileLength);

	SystemInfo setLogFileError(String logFileError);

	DbTable[] getDbTables();

	SystemInfo addToDbTables(DbTable dbTable);

	interface DbTable {

		String getName();

		DbTable setName(String name);

		@Nullable
		String getError();

		DbTable setError(String error);

		int getRowCount();

		DbTable setRowCount(int rowCount);
	}

	MonitoringGraph[] getMonitoringGraphs();

	SystemInfo addToMonitoringGraphs(MonitoringGraph monitoringGraph);

	interface MonitoringGraph {

		String getUrl();

		MonitoringGraph setURL(String id);
	}
}
