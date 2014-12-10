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

import fr.univmobile.backend.admin.GeocampusJSONController;
import fr.univmobile.backend.admin.GeocampusPoiManageJSONController;
import fr.univmobile.backend.admin.GeocampusPoisByRegionAndCategoryJSONController;
import fr.univmobile.backend.client.AbstractClientFromLocal;
import fr.univmobile.backend.client.CommentClient;
import fr.univmobile.backend.client.CommentClientFromLocal;
import fr.univmobile.backend.client.ImageMapClient;
import fr.univmobile.backend.client.ImageMapClientFromLocal;
import fr.univmobile.backend.client.PoiCategoryClient;
import fr.univmobile.backend.client.PoiCategoryClientFromLocal;
import fr.univmobile.backend.client.PoiClient;
import fr.univmobile.backend.client.PoiClientFromLocal;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.RegionClientFromLocal;
import fr.univmobile.backend.client.SessionClient;
import fr.univmobile.backend.client.SessionClientFromLocal;
import fr.univmobile.backend.client.json.CommentJSONClient;
import fr.univmobile.backend.client.json.CommentJSONClientImpl;
import fr.univmobile.backend.client.json.ImageMapJSONClient;
import fr.univmobile.backend.client.json.ImageMapJSONClientImpl;
import fr.univmobile.backend.client.json.PoiCategoryJSONClient;
import fr.univmobile.backend.client.json.PoiCategoryJSONClientImpl;
import fr.univmobile.backend.client.json.PoiJSONClient;
import fr.univmobile.backend.client.json.PoiJSONClientImpl;
import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.backend.client.json.RegionJSONClientImpl;
import fr.univmobile.backend.client.json.SessionJSONClient;
import fr.univmobile.backend.client.json.SessionJSONClientImpl;
import fr.univmobile.backend.core.CommentDataSource;
import fr.univmobile.backend.core.CommentManager;
import fr.univmobile.backend.core.ImageMapDataSource;
import fr.univmobile.backend.core.PoiCategoryDataSource;
//import fr.univmobile.backend.core.CommentThreadDataSource;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.SearchManager;
import fr.univmobile.backend.core.SessionManager;
import fr.univmobile.backend.core.UploadManager;
import fr.univmobile.backend.core.UploadNotFoundException;
import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.backend.core.impl.CommentManagerImpl;
import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.core.impl.SearchManagerImpl;
import fr.univmobile.backend.core.impl.SessionManagerImpl;
import fr.univmobile.backend.core.impl.UploadManagerImpl;
import fr.univmobile.backend.history.LogQueue;
import fr.univmobile.backend.json.AbstractJSONController;
import fr.univmobile.backend.json.CommentsJSONController;
import fr.univmobile.backend.json.CommentsPostJSONController;
import fr.univmobile.backend.json.EndpointsJSONController;
import fr.univmobile.backend.json.JsonHtmler;
import fr.univmobile.backend.json.NearestPoisJSONController;
import fr.univmobile.backend.json.PoisJSONController;
import fr.univmobile.backend.json.RegionsJSONController;
import fr.univmobile.backend.json.SessionJSONController;
import fr.univmobile.backend.json.UniversitiesJSONController;
import fr.univmobile.backend.twitter.ApplicationOnly;
import fr.univmobile.backend.twitter.TwitterAccess;
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

	// Categories
	private PoiCategoryDataSource poiCategories;

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
		final File uploadsDir = new File(dataDir, "uploads");
		final File commentsDir = new File(dataDir, "comments");
		final File imageMapsDir = new File(dataDir, "imagemaps");

		// Categories
		final File poiCategoriesDir = new File(dataDir, "poiscategories");

		// 2. DATASOURCES AND CLIENTS

		final ImageMapDataSource imageMaps;
		final CommentDataSource comments;
		final CommentManager commentManager;
		final SearchManager searchManager;
		final SessionManager sessionManager;
		final DataSource ds;

		try {

			users = BackendDataSourceFileSystem.newDataSource(
					UserDataSource.class, usersDir);

			regions = BackendDataSourceFileSystem.newDataSource(
					RegionDataSource.class, regionsDir);

			pois = BackendDataSourceFileSystem.newDataSource(
					PoiDataSource.class, poisDir);

			// Added by Mauricio
			poiCategories = BackendDataSourceFileSystem.newDataSource(
					PoiCategoryDataSource.class, poiCategoriesDir);

			uploadManager = new UploadManagerImpl(uploadsDir);

			comments = BackendDataSourceFileSystem.newDataSource(
					CommentDataSource.class, commentsDir);
			
			imageMaps = BackendDataSourceFileSystem.newDataSource(ImageMapDataSource.class, imageMapsDir);

			final InitialContext context = new InitialContext();

			ds = (DataSource) context.lookup("java:/comp/env/jdbc/univmobile");

			checkDataSource(ds);

			final LogQueue logQueue = new LogQueueDbImpl(MYSQL, ds);

			searchManager = new SearchManagerImpl(logQueue, MYSQL, ds);

			commentManager = new CommentManagerImpl(logQueue, comments,
					searchManager, MYSQL, ds);

			sessionManager = new SessionManagerImpl(logQueue, users, MYSQL, ds);

		} catch (final NamingException e) {
			throw new ServletException(e);
		} catch (final SQLException e) {
			throw new ServletException(e);
		} catch (final IOException e) {
			throw new ServletException(e);
		}

		// 2. MAIN CONTROLLERS

		final UsersController usersController = new UsersController(users);

		// Added by Mauricio
		final PoiCategoriesController poisCategoriesController = new PoiCategoriesController(
				poiCategories);
		final PoisController poisController = new PoisController(regions, pois);
		final CommentsController commentsController = new CommentsController(comments, commentManager, searchManager,
				regions, pois);

		super.init(new HomeController(users, sessionManager), //
				usersController, new UseraddController(tx, users,
						usersController, regions), //
				new RegionsController(tx, regions), //
				new AdminGeocampusController(regions, pois), //
				new SystemController(ds), //
				poisController, //
				new PoiController(comments, commentManager, searchManager,
						regions, pois), //
				commentsController, //
				new CommentController(comments, commentManager), //
				new HelpController(), //
				new LogsController(), //
				poisCategoriesController, //

				// Added by Mauricio

				new PoiCategoriesAddController(tx, poiCategories,
						poisCategoriesController), //
				new PoiCategoriesModifyController(tx, poiCategories,
						poisCategoriesController), //
				new PoisAddController(tx, pois, poisController, regions, poiCategories), //
				new PoisModifyController(tx, pois, poisController, regions, poiCategories), //
				new UserModifyController(tx, users, usersController, regions),
				new CommentStatusController(tx, comments, commentsController),
				new GeocampusAdminController());

		// 3. JSON CONTROLLERS

		final String baseURL = getBaseURL();

		final RegionClient regionClient = new RegionClientFromLocal(baseURL,
				regions, pois);

		final RegionJSONClient regionJSONClient = new RegionJSONClientImpl(
				regionClient);

		final PoiClient poiClient = new PoiClientFromLocal(baseURL, pois,
				regions);

		final PoiCategoryClient poiCategoryClient = new PoiCategoryClientFromLocal(baseURL, poiCategories, regions);

		final ImageMapClient imageMapClient = new ImageMapClientFromLocal(baseURL, imageMaps, pois);

		final PoiJSONClient poiJSONClient = new PoiJSONClientImpl(poiClient);

		final PoiCategoryJSONClient poiCategoryJSONClient = new PoiCategoryJSONClientImpl(poiCategoryClient);
		

		final ImageMapJSONClient imageMapJSONClient = new ImageMapJSONClientImpl(imageMapClient);

		final CommentClient commentClient = new CommentClientFromLocal(baseURL,
				comments, commentManager, searchManager);

		final CommentJSONClient commentJSONClient = new CommentJSONClientImpl(
				commentClient);

		final String consumerKey = checkedInitParameter("twitter.consumerKey");
		final String consumerSecret = checkedInitParameter("twitter.consumerSecret");

		final TwitterAccess twitter = new ApplicationOnly(consumerKey,
				consumerSecret);

		final String ssoBaseURL = checkedInitParameter("shibboleth.ssoBaseURL");
		final String shibbolethTargetBaseURL = checkedInitParameter("shibboleth.targetBaseURL");
		final String shibbolethCallbackURL = checkedInitParameter("shibboleth.callbackURL");

		Double nearestPoisMaxMetersAway;
		try {
			nearestPoisMaxMetersAway = Double.parseDouble(checkedInitParameter("pois.nearestMaxDistanceInMeters"));
		} catch (Exception e) {
			log.warn("Config parameter 'pois.nearestMaxDistanceInMeters' has a wrong value at web.xml. Please review. Using 0 meters. Current value is: " + checkedInitParameter("pois.nearestMaxDistanceInMeters"));
			nearestPoisMaxMetersAway = 0.0;
		}
		
		final SessionClient sessionClient = new SessionClientFromLocal(baseURL,
				ssoBaseURL, shibbolethTargetBaseURL, shibbolethCallbackURL, //
				sessionManager, twitter);

		final SessionJSONClient sessionJSONClient = new SessionJSONClientImpl(
				sessionClient);

		this.jsonControllers = new AbstractJSONController[] {
				new EndpointsJSONController(), //
				new RegionsJSONController(regionJSONClient), //
				new UniversitiesJSONController(regions, regionJSONClient), //
				/*new ImageMapJSONController(imageMaps, imageMapJSONClient), //*/
				new PoisJSONController(poiJSONClient), //
				new CommentsJSONController(pois, commentJSONClient), //
				new SessionJSONController( //
						sessionManager, sessionJSONClient), //
				new GeocampusPoisByRegionAndCategoryJSONController(poiJSONClient),
				new GeocampusPoiManageJSONController(tx, pois, imageMaps),
				new NearestPoisJSONController(poiJSONClient, nearestPoisMaxMetersAway),
				new CommentsPostJSONController(comments, commentManager), 				
				new GeocampusJSONController(regionJSONClient, poiCategoryJSONClient, imageMapJSONClient, regions, imageMaps)				
		};

		for (final AbstractJSONController jsonController : jsonControllers) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("Loading JSON controller: %s", jsonController.getClass().getName()));
			}
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
		final String remoteUserAttribute = request.getRemoteUser();
		final String requestURI = request.getRequestURI();

		if (log.isInfoEnabled()) {
			log.info("remoteAddr: " + remoteAddr//
					+ ", requestURI: " + requestURI //
					+ ", userAgent: \"" + userAgent + "\"" //
					+ ", remoteUser: " + remoteUserAttribute);
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
			// Note: We should not log any password here
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
		final Object eppn = request.getAttribute("eppn");
		final Object persistentId = request.getAttribute("persistentId");

		if (log.isInfoEnabled()) {
			log.info("Shibboleth: uid=" + uid //
					+ ", remoteUser=" + remoteUserAttribute //
					+ ", eppn=" + eppn //
					+ ", persistentId=" + persistentId //
					+ ", displayName=" + displayName);
		}

		// 2. VALIDATE: SHIBBOLETH MUST BE HERE

		final String remoteUser;

		if (remoteUserAttribute != null) {

			remoteUser = remoteUserAttribute;

		} else if (eppn != null) {

			// We are implementing the same rule as in:
			// /etc/shibboleth/shibboleth2.xml

			remoteUser = eppn.toString();

		} else if (persistentId != null) {

			remoteUser = persistentId.toString();

		} else {

			remoteUser = null;
		}

		if (log.isInfoEnabled()) {
			log.info("Using remoteUser: " + remoteUser);
		}

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

		/* Fine grained need to allow somethings to users/anons
		// Added by Mauricio
		final String uriPath = UnivMobileHttpUtils.extractUriPath(request);
		if (user.getRole() == null || user.getRole().equals("student"))
			UnivMobileHttpUtils
			.sendError404(request, response, uriPath);
		*/

		LogQueueDbImpl.setPrincipal(user.getUid()); // TODO user? delegation?

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

		if (host == null || optional_jsonBaseURLs == null
				|| optional_jsonBaseURLs.length == 0) {

			baseURL = getBaseURL(); // From configuration

			if (log.isDebugEnabled()) {
				log.debug("Using baseURL from configuration: " + baseURL);
			}

		} else {

			String jsonBaseURL = null;

			for (final String optional_jsonBaseURL : optional_jsonBaseURLs) {

				if (log.isDebugEnabled()) {
					log.debug("Checking optional_jsonBaseURL: "
							+ optional_jsonBaseURL);
				}

				final String protocol = substringBefore(optional_jsonBaseURL,
						"://");

				if (log.isDebugEnabled()) {
					log.debug("protocol: " + protocol + ", host: " + host //
							+ ", method: " + request.getMethod() //
							+ ", requestURI: " + requestURI);
				}

				final String requestProtocolUpperCase = //
				request.getProtocol().toUpperCase();

				if (log.isDebugEnabled()) {
					log.debug("requestProtocolUpperCase: "
							+ requestProtocolUpperCase + ", protocol: "
							+ protocol);
				}

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

				if (log.isDebugEnabled()) {
					log.debug("part2: " + part2 + ", host: " + host);
				}

				if (!part2.startsWith(host)) {
					continue;
				}

				final String baseURI = "/" + substringAfter(part2, "/");

				if (log.isDebugEnabled()) {
					log.debug("baseURI: " + baseURI + ", requestURI: "
							+ requestURI);
				}

				if (!requestURI.startsWith(baseURI)) {
					continue;
				}

				jsonBaseURL = optional_jsonBaseURL;

				if (log.isDebugEnabled()) {
					log.debug("Using optional_jsonBaseURL: "
							+ optional_jsonBaseURL);
				}

				break;
			}

			if (log.isDebugEnabled()) {
				log.debug("jsonBaseURL: " + jsonBaseURL);
			}

			if (jsonBaseURL != null) {

				baseURL = jsonBaseURL;

				AbstractClientFromLocal.setThreadLocalBaseURL(baseURL);

			} else {

				// baseURL = getBaseURL(); // From configuration

				baseURL = optional_jsonBaseURLs[0]; // FORCE!

				AbstractClientFromLocal.setThreadLocalBaseURL(baseURL);
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

				setThreadLocal(jsonController, request, response);

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
