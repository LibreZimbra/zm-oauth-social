package com.zimbra.oauth.resources;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.extension.ExtensionHttpHandler;
import com.zimbra.oauth.exceptions.GenericOAuthException;
import com.zimbra.oauth.utilities.OAuth2Constants;
import com.zimbra.oauth.utilities.OAuth2ResourceUtilities;

/**
 * The ZOAuth2Servlet class.<br>
 * Request entry point for the project.
 *
 * @author Zimbra API Team
 * @package com.zimbra.oauth.resources
 * @copyright Copyright © 2018
 */
public class ZOAuth2Servlet extends ExtensionHttpHandler {

	@Override
	public String getPath() {
		return "/oauth2";
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		final String path = StringUtils.removeEndIgnoreCase(req.getPathInfo(), "/");
		if (!isValidPath(path)) {
			// invalid location - not part of this service
			resp.sendError(Status.BAD_REQUEST.getStatusCode());
			return;
		}
		final Map<String, String> pathParams = parseRequestPath(path);
		final String client = pathParams.get("client");
		String location = OAuth2Constants.DEFAULT_SUCCESS_REDIRECT;

		try {
			switch (pathParams.get("action")) {
				case "authorize":
					location = OAuth2ResourceUtilities.authorize(client, req.getParameter("relay"));
					break;
				case "authenticate":
					location = OAuth2ResourceUtilities.authenticate(client, req.getParameterMap(), getAuthToken(req.getCookies()));
					break;
				default:
					// missing valid action - bad request
					resp.sendError(Status.BAD_REQUEST.getStatusCode());
					return;
			}
		} catch (final GenericOAuthException e) {
			ZimbraLog.extensions.error(e);
			resp.setStatus(e.getStatus().getStatusCode());
			return;
		}

		// set response redirect location
		resp.sendRedirect(location);
	}

	/**
	 * Determines if the path is one serviced by this extension.
	 *
	 * @param path The path to check
	 * @return True if the op is serviceable
	 */
	protected boolean isValidPath(String path) {
		return StringUtils.containsIgnoreCase(path, "authenticate/")
			|| StringUtils.containsIgnoreCase(path, "authorize/");
	}

	/**
	 * Retrieves the zm auth token from the request.
	 *
	 * @param cookies The request cookies
	 * @return The zm auth token
	 */
	protected String getAuthToken(Cookie[] cookies) {
		for (final Cookie cookie : cookies) {
			if (StringUtils.equals(cookie.getName(), OAuth2Constants.COOKIE_AUTH_TOKEN)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	/**
	 * Parses the path for the request path parameters.
	 *
	 * @param path The path to parse
	 * @return Path parameters
	 */
	protected Map<String, String> parseRequestPath(String path) {
		final Map<String, String> pathParams = new HashMap<String, String>();
		final String[] parts = path.split("/");
		// action
		if (StringUtils.equalsIgnoreCase(parts[2], "authorize")
			|| StringUtils.equalsIgnoreCase(parts[2], "authenticate")) {
			pathParams.put("action", parts[2]);
		}
		// client
		if (StringUtils.isNotEmpty(parts[3])) {
			pathParams.put("client", parts[3]);
		}
		return pathParams;
	}

}