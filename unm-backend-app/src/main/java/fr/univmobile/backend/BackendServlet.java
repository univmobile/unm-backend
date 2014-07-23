package fr.univmobile.backend;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	@Override
	public void service(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException,
			ServletException {

		// 1. REQUEST

		request.setCharacterEncoding(UTF_8);

		// 2. VALIDATE: SHIBBOLETH MUST BE HERE

		final String remoteUser = request.getRemoteUser();

		if (remoteUser == null) {

			UnivMobileHttpUtils.sendError403(request, response,
					"Cannot find REMOTE_USER");

			return;
		}

		// 3. OUTPUT

		super.service(request, response);
	}
}