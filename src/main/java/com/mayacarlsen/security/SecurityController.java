package com.mayacarlsen.security;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.mayacarlsen.user.User;
import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.RequestUtil;
import com.mayacarlsen.util.ViewUtil;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class SecurityController {

	private static final Logger logger = Logger.getLogger(SecurityController.class.getCanonicalName());

	/**
	 * Authenticate user
	 */
	public static Filter authenticate = (Request request, Response response) -> {
		if (!ACL.isPublic(request.pathInfo())) {
			ensureUserIsLoggedIn(request, response);
		}
	};

	/**
	 * Authorize user
	 */
	public static Filter authorize = (Request request, Response response) -> {
		String path = request.pathInfo();
		User currentUser = RequestUtil.getSessionUser(request);

		Boolean isPublic = ACL.isPublic(path);
		Boolean isAuthorized = ACL.isAuthorized(currentUser, path, request);
		
		logger.info("isPublic="+isPublic + ", isAuthorized="+isAuthorized);
		
		if (!isPublic && !isAuthorized) {
			response.redirect(Path.Web.UNAUTHORIZED, 401);
			Spark.halt();
		}
	};

	public static Route serveUnauthorizedPage = (Request request, Response response) -> {
		Map<String, Object> model = new HashMap<>();
		model.put("errorMessage", "Access Denied");
		return ViewUtil.render(request, model, Path.Template.UNAUTHORIZED);
	};

	/**
	 * Ensure user is logged into the application.
	 * The origin of the request (request.pathInfo()) is saved in the session so
	 * the user can be redirected back after login
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 */
	private static void ensureUserIsLoggedIn(Request request, Response response) {
		if (request.session().attribute("currentUser") == null) {
			request.session().attribute("loginRedirect", request.pathInfo());
			response.redirect(Path.Web.LOGIN);
			Spark.halt();
		}
	};

}
