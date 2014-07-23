package fr.univmobile.backend;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BackendServlet extends HttpServlet {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = -4796360020211862333L;

	@Override
	public void service(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException,
			ServletException {

		// 1. REQUEST

		request.setCharacterEncoding(UTF_8);

		// 3. OUTPUT

		response.setContentType("text/plain");
		response.setCharacterEncoding(UTF_8);
		// response.setHeader("Content-Language", "en");
		response.setLocale(Locale.ENGLISH);

		final PrintWriter out = response.getWriter();
		try {

			out.println("### Request Headers:");

			final Enumeration<?> headerNames = request.getHeaderNames();

			while (headerNames.hasMoreElements()) {

				final String headerName = headerNames.nextElement().toString();

				out.print(headerName + ": ");

				out.println(request.getHeader(headerName));
			}

			out.println();

			out.println("### request.getRemoteUser(): "
					+ request.getRemoteUser());

			out.println();

			out.println("### Some Request Attributes, direct access:");

			for (final String name : new String[] { //
			"cn", //
					"uid", //
					"Shib-Identity-Provider", //
					"persistent-id", // NOT: "persistent_id",
					// NOT: "remote-user", "REMOTE_USER", "REMOTE-USER",
					"mail", //
					"displayName", //
					"eppn", //
					"supannCivilite", //
					"orgunit-dn", // NOT: "orgunit_dn",
					"primary-orgunit-dn" // NOT: "primary_orgunit_dn",
			}) {
				out.println(name + ": " + request.getAttribute(name));
			}

			out.println();

			out.println("### Request Attributes, via attributeNames:");

			final Enumeration<?> attributeNames = request.getAttributeNames();

			while (attributeNames.hasMoreElements()) {

				final String attributeName = attributeNames.nextElement()
						.toString();

				out.print(attributeName + ": ");

				out.println(request.getAttribute(attributeName));
			}

			out.println();

			out.println("### System Properties:");

			for (final Map.Entry<?, ?> entry : System.getProperties()
					.entrySet()) {

				out.println(entry.getKey() + ": " + entry.getValue());
			}

			out.println();

		} finally {

			out.flush();

			out.close();
		}
	}
}