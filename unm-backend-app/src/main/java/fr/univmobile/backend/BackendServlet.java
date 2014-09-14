package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.univmobile.backend.core.impl.ConnectionType.MYSQL;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.annotation.Nullable;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.AbstractClientFromLocal;
import fr.univmobile.backend.client.CommentClient;
import fr.univmobile.backend.client.CommentClientFromLocal;
import fr.univmobile.backend.client.PoiClient;
import fr.univmobile.backend.client.PoiClientFromLocal;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.RegionClientFromLocal;
import fr.univmobile.backend.client.json.CommentJSONClient;
import fr.univmobile.backend.client.json.CommentJSONClientImpl;
import fr.univmobile.backend.client.json.PoiJSONClient;
import fr.univmobile.backend.client.json.PoiJSONClientImpl;
import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.backend.client.json.RegionJSONClientImpl;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
//import fr.univmobile.backend.core.CommentThreadDataSource;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.SearchManager;
import fr.univmobile.backend.core.UploadManager;
import fr.univmobile.backend.core.UploadNotFoundException;
import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.backend.core.impl.CommentManagerImpl;
import fr.univmobile.backend.core.impl.SearchManagerImpl;
import fr.univmobile.backend.core.impl.UploadManagerImpl;
import fr.univmobile.backend.json.AbstractJSONController;
import fr.univmobile.backend.json.CommentsJSONController;
import fr.univmobile.backend.json.EndpointsJSONController;
import fr.univmobile.backend.json.JsonHtmler;
import fr.univmobile.backend.json.PoisJSONController;
import fr.univmobile.backend.json.RegionsJSONController;
import fr.univmobile.backend.json.UniversitiesJSONController;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.AbstractUnivMobileServlet;
import fr.univmobile.web.commons.BuildInfoUtils;
import fr.univmobile.web.commons.PageNotFoundException;
import fr.univmobile.web.commons.UnivMobileHttpUtils;

public final class BackendServlet extends AbstractUnivMobileServlet {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = -4796360020211862333L;

	private UserDataSource users;
	private RegionDataSource regions;
	private PoiDataSource pois;
	// private PoiTreeDataSource poiTrees;
	private UploadManager uploadManager;

	private AbstractJSONController[] jsonControllers;

	@Nullable
	private String[] optional_jsonBaseURLs;

	@Override
	public void init() throws ServletException {

		if (log.isInfoEnabled()) {
			log.info(this + ": init()...");
		}

		// 0. SERVLET INIT PARAMETERS

		final String dataDir = getServletConfig().getInitParameter("dataDir");

		if (isBlank(dataDir)) {
			throw new UnavailableException("Cannot find init-param: dataDir");
		}

		final String optional_jsonBaseURLs = getServletConfig()
				.getInitParameter("optional-jsonBaseURLs");

		if (isBlank(optional_jsonBaseURLs)) {

			this.optional_jsonBaseURLs = null;

		} else {

			this.optional_jsonBaseURLs = //
			split(optional_jsonBaseURLs.replace(',', ' '));

			for (int i = 0; i < this.optional_jsonBaseURLs.length; ++i) {

				final String optional_jsonBaseURL = this.optional_jsonBaseURLs[i];

				if (optional_jsonBaseURL.endsWith("/json/")) {

					this.optional_jsonBaseURLs[i] = substringBeforeLast(
							optional_jsonBaseURL, "json/");

				} else if (optional_jsonBaseURL.endsWith("/json")) {

					this.optional_jsonBaseURLs[i] = substringBeforeLast(
							optional_jsonBaseURL, "json");

				} else {

					throw new UnavailableException(
							"optional-jsonBaseURL should end with \"/json/\" or \"/json\": "
									+ optional_jsonBaseURL);
				}
			}
		}

		// 1. TTRANSACTION MANAGER + DATA

		final TransactionManager tx = TransactionManager.getInstance();

		final File usersDir = new File(dataDir, "users");
		final File regionsDir = new File(dataDir, "regions");
		final File poisDir = new File(dataDir, "pois");
		final File poiTreesDir = new File(dataDir, "poitrees");
		final File uploadsDir = new File(dataDir, "uploads");
		final File commentsDir = new File(dataDir, "comments");

		// 2. DATASOURCES AND CLIENTS

		final PoiTreeDataSource poiTrees;
		final CommentDataSource comments;
		final CommentManager commentManager;
		final DataSource ds;

		try {

			users = BackendDataSourceFileSystem.newDataSource(
					UserDataSource.class, usersDir);

			regions = BackendDataSourceFileSystem.newDataSource(
					RegionDataSource.class, regionsDir);

			poiTrees = BackendDataSourceFileSystem.newDataSource(
					PoiTreeDataSource.class, poiTreesDir);

			poiTrees.getByUid("ile_de_france"); // check for "poitree"

			pois = BackendDataSourceFileSystem.newDataSource(
					PoiDataSource.class, poisDir);

			uploadManager = new UploadManagerImpl(uploadsDir);

			comments = BackendDataSourceFileSystem.newDataSource(
					CommentDataSource.class, commentsDir);

			final InitialContext context = new InitialContext();

			ds = (DataSource) context.lookup("java:/comp/env/jdbc/univmobile");

			checkDataSource(ds);

			final SearchManager searchManager = new SearchManagerImpl(MYSQL, ds);

			commentManager = new CommentManagerImpl(comments, searchManager,
					MYSQL, ds);

		} catch (final NamingException e) {
			throw new ServletException(e);
		} catch (final SQLException e) {
			throw new ServletException(e);
		} catch (final IOException e) {
			throw new ServletException(e);
		}

		// 2. MAIN CONTROLLERS

		final UsersController usersController = new UsersController(users);

		super.init(new HomeController(users), //
				usersController, new UseraddController(tx, users,
						usersController), //
				new RegionsController(tx, regions), //
				new AdminGeocampusController(regions, pois, poiTrees), //
				new SystemController(ds), //
				new PoisController(regions, pois, poiTrees), //
				new PoiController(comments, commentManager, regions, pois,
						poiTrees), //
				new CommentsController(comments, commentManager, regions, pois,
						poiTrees), //
				new CommentController(comments, commentManager), //
				new HelpController(), //
				new LogsController() //
		);

		// 3. JSON CONTROLLERS

		final String baseURL = getBaseURL();

		final RegionClient regionClient = new RegionClientFromLocal(baseURL,
				regions, poiTrees);

		final RegionJSONClient regionJSONClient = new RegionJSONClientImpl(
				regionClient);

		final PoiClient poiClient = new PoiClientFromLocal(baseURL, pois,
				poiTrees, regions);

		final PoiJSONClient poiJSONClient = new PoiJSONClientImpl(poiClient);

		final CommentClient commentClient = new CommentClientFromLocal(baseURL,
				comments, commentManager);

		final CommentJSONClient commentJSONClient = new CommentJSONClientImpl(
				commentClient);

		this.jsonControllers = new AbstractJSONController[] {
				new EndpointsJSONController(), //
				new RegionsJSONController(regionJSONClient), //
				new UniversitiesJSONController(regions, regionJSONClient), //
				new PoisJSONController(poiJSONClient), //
				new CommentsJSONController(pois, commentJSONClient) //
		};

		for (final AbstractJSONController jsonController : jsonControllers) {

			jsonController.init(this);
		}
	}

	private static final Log log = LogFactory.getLog(BackendServlet.class);

	@Override
	public void service(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException,
			ServletException {

		// 1. REQUEST

		request.setCharacterEncoding(UTF_8);

		// final String host = request.getHeader("Host");
		final String remoteAddr = request.getRemoteAddr();
		final String userAgent = request.getHeader("User-Agent");
		final String remoteUser = request.getRemoteUser();
		final String requestURI = request.getRequestURI();

		if (log.isInfoEnabled()) {
			log.info("remoteAddr: " + remoteAddr//
					+ ", requestURI: " + requestURI //
					+ ", userAgent: \"" + userAgent + "\"" //
					+ ", remoteUser: " + remoteUser);
		}

		if (log.isDebugEnabled()) {
			log.debug("  request.authType: " + request.getAuthType());
			log.debug("  request.contentType: " + request.getContentType());
			log.debug("  request.contextPath: " + request.getContextPath());
			log.debug("  request.localAddr: " + request.getLocalAddr());
			log.debug("  request.localName: " + request.getLocalName());
			log.debug("  request.localPort: " + request.getLocalPort());
			log.debug("  request.method: " + request.getMethod());
			log.debug("  request.pathInfo: " + request.getPathInfo());
			log.debug("  request.protocol: " + request.getProtocol());
			log.debug("  request.queryString: " + request.getQueryString());
			log.debug("  request.remoteAddr: " + request.getRemoteAddr());
			log.debug("  request.remoteHost: " + request.getRemoteHost());
			log.debug("  request.remoteUser: " + request.getRemoteUser());
			log.debug("  request.requestURI: " + request.getRequestURI());
			log.debug("  request.scheme: " + request.getScheme());
			log.debug("  request.serverName: " + request.getServerName());
			log.debug("  request.serverPort: " + request.getServerPort());
			for (final Enumeration<?> e = request.getHeaderNames(); e
					.hasMoreElements();) {
				final String headerName = e.nextElement().toString();
				final String headerValue = request.getHeader(headerName);
				log.debug("  request.header." + headerName + ": " + headerValue);
			}
		}

		if (requestURI.contains("/json/") || requestURI.endsWith("/json")) {

			serveJSON(request, response);

			return;
		}

		if (requestURI.contains("/uploads/")) {

			serveUpload(request, response);

			return;
		}

		final Object displayName = request.getAttribute("displayName");
		final Object uid = request.getAttribute("uid");

		if (log.isInfoEnabled()) {
			log.info("Shibboleth: uid=" + uid //
					+ ", remoteUser=" + remoteUser //
					+ ", displayName=" + displayName);
		}

		// 2. VALIDATE: SHIBBOLETH MUST BE HERE

		if (remoteUser == null) {

			log.fatal("remoteAddr: "
					+ remoteAddr
					+ ", 403 Cannot find REMOTE_USER."
					+ " Shibboleth seems to be missing and filters not applied.");

			UnivMobileHttpUtils.sendError403(request, response,
					"Cannot find REMOTE_USER");

			return;
		}

		// 3. DATA

		regions.reload();

		users.reload();

		// 4. USER

		if (users.isNullByRemoteUser(remoteUser)) {

			log.fatal("remoteAddr: " + remoteAddr
					+ ", 403 Unknown REMOTE_USER in the Database: "
					+ remoteUser);

			UnivMobileHttpUtils.sendError403(request, response,
					"Unknown REMOTE_USER in the Database: " + remoteUser);

			return;
		}

		final User user = users.getByRemoteUser(remoteUser);

		request.getSession().setAttribute("user", user);

		// 8. BUILD INFO

		request.setAttribute("buildInfo",
				BuildInfoUtils.loadBuildInfo(getServletContext()));

		// 9. CHAIN

		super.service(request, response);
	}

	private void serveJSON(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException,
			ServletException {

		log.info("serveJSON()...");

		// 1. CONTEXT

		final String host = request.getHeader("host");

		final boolean beautify = request.getParameter("html") != null;

		final String requestURI = request.getRequestURI();

		if (log.isDebugEnabled()) {
			log.debug("requestURI: " + requestURI);
		}

		// 2. BASE URL

		final String baseURL;

		// final boolean rewriteHost;

		if (host == null || optional_jsonBaseURLs == null) {

			baseURL = getBaseURL(); // From configuration

		} else {

			String jsonBaseURL = null;

			for (final String optional_jsonBaseURL : optional_jsonBaseURLs) {

				final String protocol = substringBefore(optional_jsonBaseURL,
						"://");

				final String requestProtocolUpperCase = //
				request.getProtocol().toUpperCase();

				if (requestProtocolUpperCase.startsWith("HTTPS/")) {
					if (!protocol.equals("https")) {
						continue;
					}
				} else if (requestProtocolUpperCase.startsWith("HTTP/")) {
					if (!protocol.equals("http")) {
						continue;
					}
				} else {
					continue;
				}

				final String part2 = substringAfter(optional_jsonBaseURL, "//");

				if (!part2.startsWith(host)) {
					continue;
				}

				final String baseURI = "/" + substringAfter(part2, "/");

				if (!requestURI.startsWith(baseURI)) {
					continue;
				}

				jsonBaseURL = optional_jsonBaseURL;

				break;
			}

			if (jsonBaseURL != null) {

				baseURL = jsonBaseURL;

				AbstractClientFromLocal.setThreadLocalBaseURL(baseURL);

			} else {

				baseURL = getBaseURL(); // From configuration
			}
		}

		// 3. DISPATCH

		final String uriPath = UnivMobileHttpUtils.extractUriPath(request);

		if (log.isDebugEnabled()) {
			log.debug("uriPath: " + uriPath);
		}

		for (final AbstractJSONController jsonController : jsonControllers) {

			if (log.isDebugEnabled()) {
				log.debug("jsonController: "
						+ jsonController.getClass().getName());
			}

			if (jsonController.hasPath(uriPath)) {

				if (log.isDebugEnabled()) {
					log.debug("Found JSONController: "
							+ jsonController.getClass().getName());
				}

				setThreadLocal(jsonController, request);

				final String json;

				try {

					json = jsonController.actionJSON(baseURL);

				} catch (final PageNotFoundException e) {

					UnivMobileHttpUtils
							.sendError404(request, response, uriPath);

					return;

				} catch (final Exception e) {

					UnivMobileHttpUtils.sendError500(request, response, e);

					return;
				}

				serveJSON(json, beautify, response);

				return;
			}
		}

		UnivMobileHttpUtils.sendError404(request, response, uriPath);
	}

	private static void serveJSON(final String json, final boolean beautify,
			final HttpServletResponse response) throws IOException,
			ServletException {

		response.setCharacterEncoding(UTF_8);

		final PrintWriter out = response.getWriter();

		if (beautify) {

			response.setContentType("text/html");

			out.println("<!DOCTYPE html>");
			out.println("<html dir='ltr'>");
			out.println("<head>");
			out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
			out.println("<title>JSON</title>");
			out.println("</head>");
			out.println("<body>");
			out.println(JsonHtmler.jsonToHtml(json));
			out.println("</body>");
			out.println("</html>");

		} else {

			response.setContentType("application/json");

			out.print(json);
		}

		out.flush();

		out.close();
	}

	private void serveUpload(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException,
			ServletException {

		// e.g. "/uploads/poi/d70eeb8c7837b9b1c2edd4a62511a7460b14c82b.jpg"
		//
		final String uploadPath = "/"
				+ UnivMobileHttpUtils.extractUriPath(request);

		final InputStream is;
		final String mimeType;

		try {

			is = uploadManager.getUploadAsStream(uploadPath);
			mimeType = uploadManager.getUploadMimeType(uploadPath);

		} catch (UploadNotFoundException e) {

			UnivMobileHttpUtils.sendError404(request, response, uploadPath);

			return;
		}

		response.setContentType(mimeType);

		final ServletOutputStream os = response.getOutputStream();

		try {

			IOUtils.copy(is, os);

		} finally {
			is.close();
		}

		os.close();
	}

	private static void checkDataSource(final DataSource ds)
			throws SQLException {

		checkNotNull(ds, "dataSource");

		log.info("checkDataSource()...");

		final Connection cxn = ds.getConnection();
		try {

			// do nothing

		} finally {
			cxn.close();
		}

		log.info("checkDataSource() OK.");
	}
}