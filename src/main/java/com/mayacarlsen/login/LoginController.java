package com.mayacarlsen.login;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.mayacarlsen.user.User;
import com.mayacarlsen.user.UserController;
import com.mayacarlsen.user.UserDAO;
import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.RequestUtil;
import com.mayacarlsen.util.ViewUtil;

import spark.Request;
import spark.Response;
import spark.Route;

public class LoginController {

	private static final Logger logger = Logger.getLogger(LoginController.class.getCanonicalName());

    public static Route serveLoginPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        model.put("loggedOut", RequestUtil.removeSessionAttrLoggedOut(request));
        model.put("loginRedirect", RequestUtil.removeSessionAttrLoginRedirect(request));
        return ViewUtil.render(request, model, Path.Template.LOGIN);
    };

    public static Route handleLoginPost = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<String, Object>();
        String username = request.queryParams("username");
        String password = request.queryParams("password");
        
        if (!UserController.authenticate(username, password)) {
            model.put("authenticationFailed", true);
            return ViewUtil.render(request, model, Path.Template.LOGIN);
        }

        model.put("authenticationSucceeded", true);
        User user = UserDAO.getUserByUsername(username);
        request.session().attribute("currentUser", username);
        request.session().attribute("user", user);

        if (RequestUtil.getQueryLoginRedirect(request) != null) {
            response.redirect(RequestUtil.getQueryLoginRedirect(request));
        }

        logger.info("User logged in: " + user.toLogString());

        return ViewUtil.render(request, model, Path.Template.INDEX);
    };

    public static Route handleLogoutPost = (Request request, Response response) -> {
    	User user = (User) request.session().attribute("user");
    	request.session().removeAttribute("currentUser");
    	request.session().removeAttribute("user");
        request.session().attribute("loggedOut", true);

		response.redirect(Path.Web.LOGIN);

		logger.info("User logged out: " + user.toLogString());

        return null;
    };

}
