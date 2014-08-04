package fr.univmobile.backend;

import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.RegionClientFromLocal;
import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.backend.client.json.RegionJSONClientImpl;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;
import fr.univmobile.web.commons.AbstractUnivMobileServlet;
import fr.univmobile.web.commons.BuildInfoUtils;
import fr.univmobile.web.commons.UnivMobileHttpUtils;

public class BackendServlet extends AbstractUnivMobileServlet {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = -4796360020211862333L;

	private UserDataSource users;

	private RegionDataSource regions;

	private RegionJSONClient regionJSONClient;

	@Override
	public void init() throws ServletException {

		final String dataDir = getServletConfig().getInitParameter("dataDir");

		if (isBlank(dataDir)) {
			throw new UnavailableException("Cannot find init-param: dataDir");
		}

		final File usersDir = new File(dataDir, "users");
		final File regionsDir = new File(dataDir, "regions");

		try {

			users = BackendDataSourceFileSystem.newDataSource(
					UserDataSource.class, usersDir);

			regions = BackendDataSourceFileSystem.newDataSource(
					RegionDataSource.class, regionsDir);

		} catch (final IOException e) {
			throw new ServletException(e);
		}

		regionJSONClient = new RegionJSONClientImpl(new RegionClientFromLocal(
				regions));

		super.init( //
				new HomeController(users, regions), //
				new UseraddController(users, regions));
	}

	private static final Log log = LogFactory.getLog(BackendServlet.class);

	@Override
	public void service(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException,
			ServletException {

		// 1. REQUEST

		request.setCharacterEncoding(UTF_8);

		final String host = request.getHeader("Host");
		final String userAgent = request.getHeader("User-Agent");
		final String remoteUser = request.getRemoteUser();
		final String requestURI = request.getRequestURI();

		//if (log.isInfoEnabled()) {
		//	log.info("requestURI: " + requestURI);
		//}

		if (log.isInfoEnabled()) {
			log.info("host: " + host //
					+ ", requestURI: " + requestURI //
					+ ", userAgent: \"" + userAgent + "\"" //
					+ ", remoteUser: " + remoteUser);
		}

		if (requestURI.contains("/json/")) {

			serveJSON(request, response);

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

			log.fatal("host: "
					+ host
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

			log.fatal("host: " + host
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

		final String requestURI = request.getRequestURI();

		final String path = substringAfter(requestURI, "/json/");

		// http://univmobile.vswip.com/unm-backend-mock/regions

		// https://univmobile-dev.univ-paris1.fr/json/regions

		if ("regions".equals(path) || "regions.json".equals(path)) {

			serveJSON(regionJSONClient.getRegionsJSON(), response);

			return;
		}

		if (path.startsWith("listUniversities_")) {

			String regionId = substringAfter(path, "listUniversities_");

			if (regionId.endsWith(".json")) {
				regionId = substringBeforeLast(regionId, ".json");
			}

			if (!regions.isNullByUid(regionId)) {

				serveJSON(
						regionJSONClient.getUniversitiesJSONByRegion(regionId),
						response);

				return;
			}
		}

		final String uriPath = UnivMobileHttpUtils.extractUriPath(request);

		UnivMobileHttpUtils.sendError404(request, response, uriPath);
	}

	private static void serveJSON(final String json,
			final HttpServletResponse response) throws IOException,
			ServletException {

		response.setCharacterEncoding(UTF_8);
		response.setContentType("application/json");

		final PrintWriter out = response.getWriter();

		out.print(json);

		out.flush();

		out.close();
	}
}