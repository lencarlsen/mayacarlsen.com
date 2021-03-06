package com.mayacarlsen.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

import com.mayacarlsen.article.Article;
import com.mayacarlsen.security.AuthorizedList;
import com.mayacarlsen.util.JSONUtil;
import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.RequestUtil;
import com.mayacarlsen.util.ViewUtil;

import spark.Request;
import spark.Response;
import spark.Route;

public class UserController {
	
	private static final Logger logger = Logger.getLogger(UserController.class.getCanonicalName());

	private static final String USER_DELETED_MSG       = "User '%1s' Successfully Deleted";
	private static final String USER_DELETED_ERROR_MSG = "User '%1s' Could Not be Deleted";
	private static final String USER_NOT_EXIST_MSG     = "User '%1s' Does Not Exist";

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
        String username = request.queryParams("username");
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
    	
        User user = new User(loggedInUser.getUser_id(), username, firstName, lastName, alias, avitar, email, 
        		salt, hashedPassword, loggedInUser.getRole(), null, null);
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

    public static Route saveUserPage = (Request request, Response response) -> {
        String userId = request.queryParams("user_id");
        String username = request.queryParams("username");
        String firstName = request.queryParams("firstname");
        String lastName = request.queryParams("lastname");
        String alias = request.queryParams("alias");
        String email = request.queryParams("email");
        String role = request.queryParams("role");
        String newPassword = request.queryParams("password");
        String confirmNewPassword = request.queryParams("confirmpassword");
        
        Integer userIdInt = (userId == null || userId.trim().isEmpty() ? null : Integer.valueOf(userId));
        		 
    	User loggedInUser = request.session().attribute("user");

    	String salt = getSalt();
    	String hashedPassword = createHashedPassword(newPassword, salt);
    	
    	if (newPassword.trim().length() == 0) {
    		salt = loggedInUser.getSalt();
    		hashedPassword = loggedInUser.getPassword();
    	}

    	User user = new User(
    			userIdInt, 
    			username, 
    			firstName, 
    			lastName, 
    			alias, 
    			null,
    			email, 
    			salt, hashedPassword, UserRoleEnum.valueOf(role).toString(), null, null);

    	if (UserDAO.userExist(userIdInt)) {
    		UserDAO.updateUser(user);
    	} else {
    		UserDAO.createUser(user);
    	}
        
    	Map<String, Object> model = new HashMap<>();
    	model.put("article", new Article(null, null, null, null, null, null, null, null, null, null, null ));
		
        return ViewUtil.render(request, model, Path.Template.ADMIN);
    };
    
    public static Route getAllUsersAsJSON = (Request request, Response response) -> {
    	List<User> userList = UserDAO.getAllUsers();
    	AuthorizedList<User> list = new AuthorizedList<>(RequestUtil.getSessionUser(request), userList);
    	String json = JSONUtil.dataToJson(list.getList());

		response.status(200);
		response.type("application/json");

		return json;
    };

    public static Route getUserAsJSON = (Request request, Response response) -> {
		String userId = request.params("userId");
    	User user = UserDAO.getUser(Integer.valueOf(userId));
    	String json = JSONUtil.dataToJson(user);

		response.status(200);
		response.type("application/json");

		return json;
    };

	public static Route deleteUserAsJSON = (Request request, Response response) -> {
		logger.info("deleteUserAsJSON...");

		Integer userId = Integer.valueOf(request.params("userId"));

		String json = JSONUtil.dataToJson("Error");
		if (UserDAO.userExist(userId)) {
			Integer count = UserDAO.deleteUser(userId);
			if (count > 0) {
				response.status(200);
				json = JSONUtil.dataToJson(String.format(USER_DELETED_MSG, userId));
			} else {
				response.status(404);
				json = JSONUtil.dataToJson(String.format(USER_DELETED_ERROR_MSG, userId));
			}
		} else {
			response.status(404);
			json = JSONUtil.dataToJson(String.format(USER_NOT_EXIST_MSG, userId));
		}

		response.type("application/json");

		return json;
	};
}
