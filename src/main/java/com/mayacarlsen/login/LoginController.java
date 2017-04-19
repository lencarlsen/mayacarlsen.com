package com.mayacarlsen.login;

import java.util.HashMap;
import java.util.Map;

import com.mayacarlsen.user.UserController;
import com.mayacarlsen.user.UserDAO;
import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.RequestUtil;
import com.mayacarlsen.util.ViewUtil;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginController {

    public static Route serveLoginPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        model.put("loggedOut", RequestUtil.removeSessionAttrLoggedOut(request));
        model.put("loginRedirect", RequestUtil.removeSessionAttrLoginRedirect(request));
        return ViewUtil.render(request, model, Path.Template.LOGIN);
    };

    public static Route handleLoginPost = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        String username = RequestUtil.getQueryUsername(request);
        String password = RequestUtil.getQueryPassword(request);
        
        if (!UserController.authenticate(username, password)) {
            model.put("authenticationFailed", true);
            return ViewUtil.render(request, model, Path.Template.LOGIN);
        }
        model.put("authenticationSucceeded", true);
        request.session().attribute("currentUser", username);
        request.session().attribute("user", UserDAO.getUserByUsername(username));

        if (RequestUtil.getQueryLoginRedirect(request) != null) {
            response.redirect(RequestUtil.getQueryLoginRedirect(request));
        }
        return ViewUtil.render(request, model, Path.Template.INDEX);
    };

    public static Route handleLogoutPost = (Request request, Response response) -> {
    	request.session().removeAttribute("currentUser");
        request.session().attribute("loggedOut", true);

		response.redirect(Path.Web.LOGIN);
        return null;
    };

    // The origin of the request (request.pathInfo()) is saved in the session so
    // the user can be redirected back after login
	public static Filter ensureUserIsLoggedIn = (Request request, Response response) -> {
        if (!com.mayacarlsen.security.ACL.isPublic(request.pathInfo()) && request.session().attribute("currentUser") == null) {
            request.session().attribute("loginRedirect", request.pathInfo());
            response.redirect(Path.Web.LOGIN);
        }
    };

}
