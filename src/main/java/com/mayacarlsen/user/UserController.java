package com.mayacarlsen.user;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.*;

import com.mayacarlsen.login.LoginController;
import com.mayacarlsen.util.DaoUtil;
import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.RequestUtil;
import com.mayacarlsen.util.ViewUtil;

import spark.Request;
import spark.Response;
import spark.Route;

public class UserController {
	
	private static final Logger logger = Logger.getLogger(UserController.class.getCanonicalName());

	// Authenticate the user by hashing the inputed password using the stored salt,
    // then comparing the generated hashed password to the stored hashed password
    public static boolean authenticate(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }
        User user = UserDAO.getUserByUsername(username);
        if (user == null) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, user.getSalt());

		logger.info("username="+username + ", dbsalt="+user.getSalt() + ", hashedPassword="+hashedPassword);
		logger.info("username="+username + ", dbsalt="+user.getSalt() + ", dbPassword    ="+user.getPassword());

		return hashedPassword.equals(user.getPassword());
    }

    public static Route serveUserSettingsPage = (Request request, Response response) -> {
        return ViewUtil.render(request, new HashMap<String, Object>(), Path.Template.USER_SETTINGS);
    };

    public static Route handleUserSettingsPost = (Request request, Response response) -> {
    	Map<String, Object> model = new HashMap<>();
        String username = RequestUtil.getQueryUsername(request);
        String firstName = request.queryParams("firstname");
        String lastName = request.queryParams("lastname");
        String alias = request.queryParams("alias");
        String avitar = request.queryParams("avitar");
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
    	
    	if (newPassword.trim().length() == 0) {
    		salt = loggedInUser.getSalt();
    		hashedPassword = loggedInUser.getPassword();
    	}
    	
        User user = new User(loggedInUser.getUser_id(), username, firstName, lastName, alias, avitar, email, salt, hashedPassword, null, null);
        User newUser = UserDAO.updateUser(user);
        
        request.session().attribute("user", newUser);
        
        return ViewUtil.render(request, model, Path.Template.INDEX);
    };
    
    // This method doesn't do anything, it's just included as an example
    public static void updateNewUserPassword(String username, String oldPassword, String newPassword) {
        if (authenticate(username, oldPassword)) {
            String newSalt = BCrypt.gensalt();
            String newHashedPassword = BCrypt.hashpw(newSalt, newPassword);
            // Update the user salt and password
            User user = UserDAO.getUserByUsername(username);
            user.setSalt(newSalt);
            user.setPassword(newHashedPassword);
        }
    }
    
    public static String createHashedPassword(String password, String salt) {
        return BCrypt.hashpw(password, salt);
    }
    
    public static String getSalt() {
    	return BCrypt.gensalt();
    }
}
