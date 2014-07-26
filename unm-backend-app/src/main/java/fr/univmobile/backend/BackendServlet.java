package fr.univmobile.backend;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.web.commons.AbstractUnivMobileServlet;
import fr.univmobile.web.commons.UnivMobileHttpUtils;

public class BackendServlet extends AbstractUnivMobileServlet {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = -4796360020211862333L;

	@Override
	public void init() throws ServletException {

		super.init( //
		HomeController.class //
		);
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

		// 2. VALIDATE: SHIBBOLETH MUST BE HERE

		if (remoteUser == null) {

			log.fatal("host: "
					+ host
					+ ", 403 Cannot Find REMOTE_USER."
					+ " Shibboleth seems to be missing and filters not applied.");

			UnivMobileHttpUtils.sendError403(request, response,
					"Cannot find REMOTE_USER");

			return;
		}

		if (log.isInfoEnabled()) {
			log.info("host: " + host //
					+ ", requestURI: " + requestURI //
					+ ", userAgent: \"" + userAgent + "\"" //
					+ ", remoteUser: " + remoteUser);
		}

		// 3. OUTPUT

		super.service(request, response);
	}
}