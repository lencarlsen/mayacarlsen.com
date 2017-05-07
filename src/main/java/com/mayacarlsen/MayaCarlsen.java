package com.mayacarlsen;

import static j2html.TagCreator.article;
import static j2html.TagCreator.b;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;
import static spark.debug.DebugScreen.enableDebugScreen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mayacarlsen.about.AboutController;
import com.mayacarlsen.admin.AdminController;
import com.mayacarlsen.article.ArticleController;
import com.mayacarlsen.chat.ChatController;
import com.mayacarlsen.chat.ChatWebSocketHandler;
import com.mayacarlsen.file.FileController;
import com.mayacarlsen.index.IndexController;
import com.mayacarlsen.login.LoginController;
import com.mayacarlsen.registration.RegistrationController;
import com.mayacarlsen.security.SecurityController;
import com.mayacarlsen.user.User;
import com.mayacarlsen.user.UserController;
import com.mayacarlsen.util.DAOUtil;
import com.mayacarlsen.util.Filters;
import com.mayacarlsen.util.Path;

public class MayaCarlsen {

    private static final Logger logger = Logger.getLogger(MayaCarlsen.class.getCanonicalName());

    public static Map<Session, User> chatuserUsernameMap = new HashMap<>();

    public static void main(String[] args) {
	logger.info("Starting MayaCarlsen.com...");

	addShutdownHook();

	port(getHerokuAssignedPort());

	webSocket("/wschat", ChatWebSocketHandler.class);
	// init();

	staticFiles.location("/public"); // index.html is served at localhost:4567 (default port)
	staticFiles.expireTime(600);

	enableDebugScreen(); // Somehow enableDebugScreen() will cause websocket to fail on heroku

	// Set up before-filters (called before each get/post)
	// before("*", Filters.addTrailingSlashes);
	// Handle local change
	before("*", Filters.handleLocaleChange);
	// Authenticate user
	before("/*", SecurityController.authenticate);
	// Authorize user
	// before("/*", SecurityController.authorize);

	// Catch global exceptions
	exception(Exception.class, IndexController.serveErrorPage);

	get(Path.Web.ROOT, IndexController.serveIndexPage);
	get(Path.Web.ABOUT, AboutController.serveAboutPage);
	get(Path.Web.INDEX, IndexController.serveIndexPage);
	get(Path.Web.LOGIN, LoginController.serveLoginPage);
	post(Path.Web.LOGIN, LoginController.handleLoginPost);
	post(Path.Web.REGISTER, RegistrationController.handleRegistrationPost);
	get(Path.Web.LOGOUT, LoginController.handleLogoutPost);
	get(Path.Web.CHAT, ChatController.serveChatPage);

	get(Path.Web.USER_SETTINGS, UserController.serveUserSettingsPage);
	post(Path.Web.SAVE_USER_SETTINGS, UserController.handleUserSettingsPost);
	// get("*", ViewUtil.notFound);

	get(Path.Web.ADMIN, AdminController.serveAdminPage);

	get(Path.Web.VIEW_ARTICLE, ArticleController.serveStoryPage);
	post(Path.Web.SAVE_ARTICLE, ArticleController.saveArticlePage);
	get(Path.Web.GET_ALL_ARTICLES, ArticleController.getAllArticlesAsJSON);
	get(Path.Web.GET_ARTICLE, ArticleController.getArticleAsJSON);
	delete(Path.Web.DELETE_ARTICLE, ArticleController.deleteArticleAsJSON);

	get(Path.Web.GET_ALL_USERS, UserController.getAllUsersAsJSON);
	get(Path.Web.GET_USER, UserController.getUserAsJSON);
	post(Path.Web.SAVE_USER, UserController.saveUserPage);
	delete(Path.Web.DELETE_USER, UserController.deleteUserAsJSON);

	get(Path.Web.GET_FILE_INFO, FileController.getFileInfo);
	get(Path.Web.GET_ALL_FILES, FileController.getAllFilesAsJSON);
	get(Path.Web.GET_FILE, FileController.getFile);
	post(Path.Web.SAVE_FILE, FileController.uploadFile);
	delete(Path.Web.DELETE_FILE, FileController.deleteFileAsJSON);

        get(Path.Web.GET_THUMBNAIL_IMAGE, FileController.getThumbnailImage);
	get(Path.Web.GET_PUBLISHED_THUMBNAIL_IMAGE, FileController.getPublishedThumbnailImage);
	get(Path.Web.GET_PUBLISHED_IMAGE, FileController.getPublishedImage);
	get(Path.Web.VIEW_IMAGES, FileController.serveImagesPage);

	// Set up after-filters (called after each get/post)
	after("*", Filters.addGzipHeader);
    }

    public static Map.Entry<Session, User> getUser(String username) {
	for (Map.Entry<Session, User> entry : MayaCarlsen.chatuserUsernameMap.entrySet()) {
	    if (entry.getValue().getUsername().equals(username)) {
		return entry;
	    }
	}
	return null;
    }

    private static JSONArray createJSONArray() {
	JSONArray jsonArray = new JSONArray();
	try {
	    for (User user : chatuserUsernameMap.values()) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("alias", user.getAlias());
		jsonObj.put("firstName", user.getFirst_name());
		jsonObj.put("lastName", user.getLast_name());
		jsonObj.put("username", user.getUsername());
		jsonArray.put(jsonObj);
	    }
	    return jsonArray;
	} catch (JSONException e) {
	    throw new RuntimeException("Create JSON user object failed", e);
	}
    }

    // Sends a message from one user to all users, along with a list of current usernames
    public static void broadcastMessage(String sender, String message) {
	chatuserUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
	    try {
		session.getRemote().sendString(String.valueOf(new JSONObject().put("userMessage",
			createHtmlMessageFromSender(sender, message)).put("userlist", createJSONArray())));
	    } catch (Exception e) {
		throw new RuntimeException("Sending JSON user object failed", e);
	    }
	});
    }

    // Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(String sender, String message) {
	return article().with(b(sender + " says:"), p(message), span().withClass("timestamp").withText(
		new SimpleDateFormat("HH:mm:ss").format(new Date()))).render();
    }

    private static int getHerokuAssignedPort() {
	ProcessBuilder processBuilder = new ProcessBuilder();
	if (processBuilder.environment().get("PORT") != null) {
	    return Integer.parseInt(processBuilder.environment().get("PORT"));
	}
	return 4567; // return default port if heroku-port isn't set (i.e. on localhost)
    }

    private static final Thread shutdownThread = new Thread() {
	public void run() {
	    DAOUtil.closeDatasource();
	}
    };

    private static void addShutdownHook() {
	Runtime.getRuntime().addShutdownHook(shutdownThread);
	logger.info("Shudown hook added.");
    }
}