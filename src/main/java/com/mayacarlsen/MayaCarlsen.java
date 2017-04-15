package com.mayacarlsen;

import org.eclipse.jetty.websocket.api.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mayacarlsen.chat.ChatController;
import com.mayacarlsen.chat.ChatWebSocketHandler;
import com.mayacarlsen.index.IndexController;
import com.mayacarlsen.login.LoginController;
import com.mayacarlsen.registration.RegistrationController;
import com.mayacarlsen.user.User;
import com.mayacarlsen.user.UserController;
import com.mayacarlsen.util.Filters;
import com.mayacarlsen.util.Path;

import java.text.*;
import java.util.*;
import java.util.logging.Logger;

import static spark.debug.DebugScreen.enableDebugScreen;
import static j2html.TagCreator.*;
import static spark.Spark.*;

public class MayaCarlsen {

	private static final Logger logger = Logger.getLogger(MayaCarlsen.class.getCanonicalName());

	public static Map<Session, User> chatuserUsernameMap = new HashMap<>();

    public static void main(String[] args) {
		logger.info("Starting MayaCarlsen.com...");

		port(getHerokuAssignedPort());

    	webSocket("/wschat", ChatWebSocketHandler.class);
        //init();

    	staticFiles.location("/public"); //index.html is served at localhost:4567 (default port)
        staticFiles.expireTime(600);

        enableDebugScreen();  // Somehow enableDebugScreen() will cause websocket to fail on heroku

        // Set up before-filters (called before each get/post)
//        before("*",           Filters.addTrailingSlashes);
        before("*",           Filters.handleLocaleChange);
//        before(((request, response) -> {
//            final String localhostUrl = "http://localhost"; //"https://maya-chat.herokuapp.com/chat";
//            final String httpUrl = "http://"; //"https://maya-chat.herokuapp.com/chat";
//            final String httpsUrl = "https://";
//        	final String url = request.url();
//        	System.out.println("url="+url);
//        	if (!url.startsWith(localhostUrl) && url.startsWith(httpUrl)) {
//                final String[] split = url.split(httpUrl);
//                final String newUrl = httpsUrl + split[1];
//                System.out.println("newUrl="+newUrl);
//                response.redirect(newUrl);
//            }
//        }));
        
        get(Path.Web.ROOT,      IndexController.serveIndexPage);
        get(Path.Web.INDEX,     IndexController.serveIndexPage);
        get(Path.Web.LOGIN,     LoginController.serveLoginPage);
        post(Path.Web.LOGIN,    LoginController.handleLoginPost);
        post(Path.Web.REGISTER, RegistrationController.handleRegistrationPost);
        get(Path.Web.LOGOUT,    LoginController.handleLogoutPost);
        get(Path.Web.CHAT,      ChatController.serveChatPage);
        get(Path.Web.USER_SETTINGS, UserController.serveUserSettingsPage);
        post(Path.Web.SAVE_USER_SETTINGS, UserController.handleUserSettingsPost);
//        get("*",              ViewUtil.notFound);

        //Set up after-filters (called after each get/post)
        after("*",            Filters.addGzipHeader);
	}

    public static Map.Entry<Session, User> getUser(String username) {
        for(Map.Entry<Session, User> entry : MayaCarlsen.chatuserUsernameMap.entrySet()) {
        	if(entry.getValue().getUsername().equals(username)) {
        		return entry;
        	}
        }
    	return null;
    }
    
    private static JSONArray createJSONArray() {
    	JSONArray jsonArray = new JSONArray();
		try {
	    	for(User user: chatuserUsernameMap.values()) {
	    		JSONObject jsonObj = new JSONObject();
	    		jsonObj.put("alias", user.getAlias());
	    		jsonObj.put("firstName", user.getFirstName());
				jsonObj.put("lastName", user.getLastName());
	    		jsonObj.put("username", user.getUsername());
	    		jsonArray.put(jsonObj);
	    	}
	    	return jsonArray;
		} catch (JSONException e) {
			throw new RuntimeException("Create JSON user object failed", e);
		}
    }

    //Sends a message from one user to all users, along with a list of current usernames
    public static void broadcastMessage(String sender, String message) {
        chatuserUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
            	session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(sender, message))
                    .put("userlist", createJSONArray())
                ));
            } catch (Exception e) {
    			throw new RuntimeException("Sending JSON user object failed", e);
            }
        });
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }

    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}