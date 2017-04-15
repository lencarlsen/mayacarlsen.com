package com.mayacarlsen.user;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.*;

import com.mayacarlsen.login.LoginController;
import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.RequestUtil;
import com.mayacarlsen.util.ViewUtil;

import spark.Request;
import spark.Response;
import spark.Route;

public class UserController {
	
    // Authenticate the user by hashing the inputed password using the stored salt,
    // then comparing the generated hashed password to the stored hashed password
    public static boolean authenticate(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }
        User user = UserDao.getUserByUsername(username);
        if (user == null) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, user.getSalt());

        return hashedPassword.equals(user.getHashedPassword());
    }

    public static Route serveUserSettingsPage = (Request request, Response response) -> {
    	LoginController.ensureUserIsLoggedIn(request, response);
        
    	User user = request.session().attribute("user");

        Map<String, Object> model = new HashMap<>();
        model.put("username", user.getUsername());
        model.put("firstname", user.getFirstName());
        model.put("lastname", user.getLastName());
        model.put("alias", user.getAlias());
        model.put("avitar", "");
        model.put("email", (user.getEmailAddress() == null ? "" : user.getEmailAddress()));
        return ViewUtil.render(request, model, Path.Template.USER_SETTINGS);
    };

    public static Route handleUserSettingsPost = (Request request, Response response) -> {
    	LoginController.ensureUserIsLoggedIn(request, response);

    	Map<String, Object> model = new HashMap<>();
        String username = RequestUtil.getQueryUsername(request);
        String firstName = request.queryParams("firstname");
        String lastName = request.queryParams("lastname");
        String alias = request.queryParams("alias");
        String email = request.queryParams("email");
        String newPassword = request.queryParams("password");
        String confirmNewPassword = request.queryParams("confirmpassword");
        
        if (!newPassword.equals(confirmNewPassword)) {
            model.put("registrationFailed", true);
            return ViewUtil.render(request, model, Path.Template.LOGIN);
        }

    	User loggedInUser = request.session().attribute("user");
        
    	String salt = getSalt();
    	String hashedPassword = createHashedPassword(newPassword, salt);
        User user = new User(loggedInUser.getUsername(), firstName, lastName, alias, email, salt, hashedPassword);
        User newUser = UserDao.updateUser(user);
        
        request.session().attribute("user", newUser);
        
        return ViewUtil.render(request, model, Path.Template.INDEX);
    };
    
    // This method doesn't do anything, it's just included as an example
    public static void updateNewUserPassword(String username, String oldPassword, String newPassword) {
        if (authenticate(username, oldPassword)) {
            String newSalt = BCrypt.gensalt();
            String newHashedPassword = BCrypt.hashpw(newSalt, newPassword);
            // Update the user salt and password
            User user = UserDao.getUserByUsername(username);
            user.setSalt(newSalt);
            user.setHashedPassword(newHashedPassword);
        }
    }
    
    public static String createHashedPassword(String password, String salt) {
        return BCrypt.hashpw(password, salt);
    }
    
    public static String getSalt() {
    	return BCrypt.gensalt();
    }
}
