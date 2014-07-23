package fr.univmobile.backend.noshib;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This filter mocks the presence of Shibboleth. It is intended to be used only
 * in integration tests. See the <code>unm-backend-app-noshib</code> webapp for
 * a sample usage.
 * 
 * <ul>
 * <li>If Shibboleth is present, the filter will purposingly crash the
 * application.
 * <li>If HTTPS is used, the filter will purposingly crash the application.
 * <li>If the local HOSTNAME is none of the hosts listed in the
 * <code>web.xml</code> init properties (expectingly Continuous integration
 * hosts), the filter will crash during initialization.
 * <li>If the host in the HTTP request is not "<code>localhost</code>", the
 * filter will purposingly crash the application.
 * </ul>
 * 
 * To mock the presence of Shibboleth, you must pass parameters within the HTTP
 * request:
 * <ul>
 * <li>NOSHIB_uid: will be transformed into a "<code>uid</code>" Shibboleth
 * request attribute.
 * <li>NOSHIB_eppn: will be transformed into a "<code>eppn</code>" Shibboleth
 * request attribute.
 * <li>NOSHIB_REMOTE_USER: will be transformed into a "<code>REMOTE_USER</code>"
 * Shibboleth request attribute.
 * </ul>
 */
public class NoShibbolethFilter implements Filter {

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {

		final String HOSTNAME;

		try {

			// Not quite the best way to pretend calling hostname(1) on Unix,
			// but this class will presumably only be used within tests.

			HOSTNAME = InetAddress.getLocalHost().getHostName();

		} catch (final UnknownHostException e) {
			throw new ServletException("Cannot find HOSTNAME.");
		}

		if (HOSTNAME == null) {
			throw new UnavailableException("Cannot find HOSTNAME.");
		}

		final String hostsDeny = filterConfig.getInitParameter("hosts.deny");

		if (hostsDeny != null) {

			for (final String host : split(hostsDeny)) {
				if (HOSTNAME.equals(host)) {
					throw new UnavailableException(
							"Host is denied access to this test app: "
									+ HOSTNAME);
				}
			}
		}

		final String hostsAllow = filterConfig.getInitParameter("hosts.allow");

		if (hostsAllow == null) {
			throw new UnavailableException(
					"Servlet filter should be initialized with a \"hosts.allow\" parameter.");
		}

		for (final String host : split(hostsAllow)) {
			if (HOSTNAME.equals(host)) {
				return;
			}
		}

		throw new UnavailableException(
				"Host is denied access to this test app: " + HOSTNAME);
	}

	@Override
	public void doFilter(final ServletRequest request,
			final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {

		final ServletRequest validRequest = validate(request, response);

		if (validRequest == null) {

			return; // The error message has already been sent.
		}

		chain.doFilter(validRequest, response);
	}

	/**
	 * send an error message to the response, and return null
	 */
	private ServletRequest error(final ServletResponse response,
			final String text) throws IOException, ServletException {

		response.setCharacterEncoding(UTF_8);
		response.setContentType("text/plain");

		((HttpServletResponse) response)
				.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		final PrintWriter out = response.getWriter();

		out.println(this.getClass().getName() + ": " + text);

		out.flush();

		out.close();

		return null;
	}

	private ServletRequest validate(final ServletRequest request,
			final ServletResponse response) throws IOException,
			ServletException {

		if (!HttpServletRequest.class.isInstance(request)) {

			return error(response,
					"Request should be an instance of HttpServletRequest: "
							+ request);
		}

		final HttpServletRequest httpRequest = (HttpServletRequest) request;

		final String httpHost = httpRequest.getHeader("host");

		if (httpHost == null || !httpHost.startsWith("localhost")) {

			return error(response, "HTTP host must be localhost (test env): "
					+ httpHost);
		}

		final String remoteUser = httpRequest.getRemoteUser();

		if (remoteUser != null) {

			return error(response, "REMOTE_USER exists: " + remoteUser);
		}

		final Object shibIdentityProvider = request
				.getAttribute("Shib-Identity-Provider");

		if (shibIdentityProvider != null) {

			return error(response, "Shib_Identity_Provider exists: "
					+ shibIdentityProvider);
		}

		request.setCharacterEncoding(UTF_8);

		final String uidParam = request.getParameter("uid");
		final String eppnParam = request.getParameter("eppn");
		final String remoteUserParam = request.getParameter("remoteUser");

		final HttpSession httpSession = httpRequest.getSession();

		final String HOLDER = NoShibbolethHolder.class.getName();

		if (uidParam != null && eppnParam != null && remoteUserParam != null) {

			if (isBlank(uidParam) || isBlank(eppnParam)
					|| isBlank(remoteUserParam)) {

				httpSession.removeAttribute(HOLDER);

			} else {

				httpSession.setAttribute(HOLDER, new NoShibbolethHolder(
						uidParam, eppnParam, remoteUserParam));
			}
		}

		final NoShibbolethHolder holder = (NoShibbolethHolder) httpSession
				.getAttribute(HOLDER);

		if (holder == null) {

			return error(response,
					"The following HTTP parameters must be passed along with the request,"
							+ " in order to mock the Shibboleth presence:" //
							+ "\r\n           uid: " + uidParam //
							+ "\r\n          eppn: " + eppnParam //
							+ "\r\n    remoteUser: " + remoteUserParam);
		}

		request.setAttribute("uid", holder.uid);
		request.setAttribute("eppn", holder.eppn);

		return new HttpServletRequestWrapper(httpRequest) {
			@Override
			public String getRemoteUser() {
				return holder.remoteUser;
			}
		};
	}

	@Override
	public void destroy() {

		// do nothing
	}

	private static class NoShibbolethHolder implements Serializable {

		/**
		 * for serialization.
		 */
		private static final long serialVersionUID = 1232416941724016097L;

		public final String uid;
		public final String eppn;
		public final String remoteUser;

		public NoShibbolethHolder(final String uid, final String eppn,
				final String remoteUser) {

			this.uid = checkNotNull(uid, "uid");
			this.eppn = checkNotNull(eppn, "eppn");
			this.remoteUser = checkNotNull(remoteUser, "remoteUser");
		}
	}
}
