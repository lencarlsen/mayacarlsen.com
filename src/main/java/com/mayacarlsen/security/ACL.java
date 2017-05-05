package com.mayacarlsen.security;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mayacarlsen.user.User;
import com.mayacarlsen.user.UserRoleEnum;
import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.RequestUtil;

import spark.Request;

/**
 * ACL relies on UserDAO to read the User.getAccess_control().
 * 
 * View Only Request Method: GET Update Request Methods: GET, POST, DELETE
 * 
 */
public class ACL {

    private static final Logger logger = Logger.getLogger(ACL.class.getCanonicalName());

    private Request request;

    public ACL(final Request request) {
	this.request = request;
    }

    // List of public paths that do not require authorization
    private final static List<String> PUBLIC = ImmutableList.of(Path.Web.UNAUTHORIZED, Path.Web.PUBLIC, Path.Web.ROOT,
	    Path.Web.INDEX, Path.Web.ABOUT, Path.Web.LOGIN, Path.Web.VIEW_IMAGES);

    // List of public paths that do not require authorization
    private final static List<String> PUBLIC_STARTS_WITH = ImmutableList.of("/article/", "/get_image/");

    private final static Map<UserRoleEnum, List<HttpMethodEnum>> priviledgeMap = ImmutableMap.of(UserRoleEnum.ADMIN,
	    ImmutableList.of(HttpMethodEnum.GET, HttpMethodEnum.POST, HttpMethodEnum.DELETE), UserRoleEnum.USER,
	    ImmutableList.of(HttpMethodEnum.GET));

    private final static Map<UserRoleEnum, List<String>> ATTRIBUTE_LIST = ImmutableMap.of(UserRoleEnum.ADMIN,
	    ImmutableList.of("userAdmin", "createUser", "editUser", "articleAdmin", "createArticle", "editArticle",
		    "fileAdmin", "createFile", "editFile"),
	    UserRoleEnum.USER,
	    ImmutableList.of("articleAdmin", "createArticle", "editArticle", "fileAdmin", "createFile", "editFile"));

    /**
     * Determines if a <code>path</code> is public.
     * 
     * @param path Request path
     * @return True if public; otherwise false
     */
    public static Boolean isPublic(final String path) {
	return PUBLIC.contains(path) || startsWith(path);
    }

    private static Boolean startsWith(String path) {
	for (String p : PUBLIC_STARTS_WITH) {
	    if (path.startsWith(p)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Determine if user is authorized to access <code>path</code>.
     * 
     * @param user User accessing request path
     * @param path Request path
     * @param request HTTP Request
     * @return True if user is allowed to access path; otherwise false
     */
    public static Boolean isAuthorized(final User user, final String path, final Request request) {
	if (user == null || user.getRole() == null || user.getRole().trim().isEmpty()) {
	    return false;
	}

	UserRoleEnum userRole = UserRoleEnum.valueOf(user.getRole());
	List<HttpMethodEnum> authorizedMethods = priviledgeMap.get(userRole);

	HttpMethodEnum requestMethod = HttpMethodEnum.valueOf(request.requestMethod().toUpperCase());
	Boolean isAuthorized = authorizedMethods.contains(requestMethod);

	logger.info("authorizedMethods=" + authorizedMethods + ", requestMethod=" + requestMethod);
	return isAuthorized;
    }

    public Boolean isAuthorized(final String attributeId) {
	User user = RequestUtil.getSessionUser(request);
	UserRoleEnum role = UserRoleEnum.valueOf(user.getRole());
	// String path = request.pathInfo();
	String id = attributeId;
	if (ATTRIBUTE_LIST.get(role).contains(id)) {
	    return true;
	}
	return false;
    }
}
