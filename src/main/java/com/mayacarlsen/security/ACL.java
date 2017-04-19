package com.mayacarlsen.security;

import java.util.Arrays;
import java.util.List;

import com.mayacarlsen.util.Path;

/**
 * ACL relies on UserDAO to read the User.getAccess_control().
 * 
 * View Only Request Method: GET
 * Update Request Methods: GET, POST, DELETE
 * 
 */
public class ACL {

	// List of public paths that do not require authorization
	private final static List<String> PUBLIC = Arrays.asList(Path.Web.UNAUTHORIZED, 
			Path.Web.PUBLIC, 
			Path.Web.ROOT, 
			Path.Web.INDEX, 
			Path.Web.ABOUT, 
			Path.Web.LOGIN);

	// List of public paths that do not require authorization
	private final static List<String> PUBLIC_STARTS_WITH = Arrays.asList("/article/");
	
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
		for(String p : PUBLIC_STARTS_WITH) {
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
	/*public static Boolean isAuthorized(final User user, final String path, final Request request) {
		if (user == null || user.getAccess_control() == null || user.getAccess_control().trim().isEmpty()) {
			return false;
		}
		String[] paths = user.getAccess_control().toUpperCase().split(",");
		List<String> accessControl = Arrays.asList(paths);
		return accessControl.contains(request.requestMethod().toUpperCase());
	}*/

}
