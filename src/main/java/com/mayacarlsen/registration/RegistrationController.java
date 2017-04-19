package com.mayacarlsen.registration;

import java.util.HashMap;
import java.util.Map;

import com.mayacarlsen.user.User;
import com.mayacarlsen.user.UserController;
import com.mayacarlsen.user.UserDao;
import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.RequestUtil;
import com.mayacarlsen.util.ViewUtil;

import spark.Request;
import spark.Response;
import spark.Route;

public class RegistrationController {

    public static Route handleRegistrationPost = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        String username = RequestUtil.getQueryUsername(request);
        String firstName = request.queryParams("firstname");
        String lastName = request.queryParams("lastname");
        String alias = request.queryParams("alias");
        String avitar = request.queryParams("avitar");
        String email = request.queryParams("email");
        String password = request.queryParams("password");
        String confirmpassword = request.queryParams("confirmpassword");
        String salt = UserController.getSalt();
        
        if (!password.equals(confirmpassword)) {
            model.put("registrationFailed", true);
            return ViewUtil.render(request, model, Path.Template.LOGIN);
        }

        if(UserDao.usernameExist(username)) {
            model.put("registrationFailed", true);
            model.put("usernameExist", true);
            return ViewUtil.render(request, model, Path.Template.LOGIN);
        }
        
        String hashedPassword = UserController.createHashedPassword(password, salt);
        User user = UserDao.createUser(new User(null, username, firstName, lastName, alias, avitar, email, salt, hashedPassword, null, null));
        
        if(!UserController.authenticate(username, password)) {
            model.put("registrationFailed", true);
            return ViewUtil.render(request, model, Path.Template.LOGIN);
        }
        
        model.put("registrationSucceeded", true);
        model.put("authenticationSucceeded", true);
        model.put("user", user);
        request.session().attribute("currentUser", username);
        request.session().attribute("user", user);

        if (RequestUtil.getQueryLoginRedirect(request) != null) {
            response.redirect(RequestUtil.getQueryLoginRedirect(request));
        }
        return ViewUtil.render(request, model, Path.Template.LOGIN);
    };

}
