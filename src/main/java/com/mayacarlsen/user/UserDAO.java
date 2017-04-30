package com.mayacarlsen.user;

import java.util.List;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.mayacarlsen.user.User;
import com.mayacarlsen.util.DAOUtil;

public class UserDAO {

	private static final Logger logger = Logger.getLogger(UserDAO.class.getCanonicalName());

	private final static Sql2o sql2o = DAOUtil.getSql2o();

	private static final String SELECT_ALL_USERS_SQL =
			"SELECT USER_ID, USERNAME, FIRST_NAME, LAST_NAME, ALIAS, EMAIL, ROLE, /*SALT, PASSWORD,*/ CREATE_DTTM, UPDATE_DTTM "
					+ "FROM USERS";
						
	private static final String SELECT_USER_BY_USERNAME_SQL =
			"SELECT USER_ID, USERNAME, FIRST_NAME, LAST_NAME, ALIAS, EMAIL, ROLE, SALT, PASSWORD, CREATE_DTTM, UPDATE_DTTM "
					+ "FROM USERS "
					+ "WHERE USERNAME = :username";
    
	private static final String SELECT_USER_BY_USERID_SQL =
			"SELECT USER_ID, USERNAME, FIRST_NAME, LAST_NAME, ALIAS, EMAIL, ROLE, /*SALT, PASSWORD,*/ CREATE_DTTM, UPDATE_DTTM "
					+ "FROM USERS "
					+ "WHERE USER_ID = :user_id";
    
	private static final String UPDATE_USER_SQL =
			"UPDATE USERS SET "
					+ "USERNAME = :username, "
					+ "FIRST_NAME = :first_name, "
					+ "LAST_NAME = :last_name, "
					+ "ALIAS = :alias, "
					+ "EMAIL = :email, "
					+ "ROLE = :role,"
					+ "SALT = :salt, "
					+ "PASSWORD = :password,"
					+ "UPDATE_DTTM = CURRENT_TIMESTAMP "
					+ "WHERE USER_ID = :user_id";

	private static final String INSERT_USER_SQL =
			"INSERT INTO USERS ( "
					+ "USERNAME, "
					+ "FIRST_NAME, "
					+ "LAST_NAME, "
					+ "ALIAS, "
					+ "EMAIL, "
					+ "ROLE, "
					+ "SALT, "
					+ "PASSWORD,"
					+ "CREATE_DTTM "
					+ ") VALUES ( "
					+ ":username, "
					+ ":first_name, "
					+ ":last_name, "
					+ ":alias, "
					+ ":email, "
					+ ":role, "
					+ ":salt, "
					+ ":password,"
					+ "CURRENT_TIMESTAMP ) ";

	private static final String DELETE_USER_SQL =
			"DELETE FROM USERS WHERE user_id = :user_id ";

	public static Boolean usernameExist(String username) {
		return (getUserByUsername(username) != null);
	}

	public static Boolean userExist(User user) {
		return usernameExist(user.getUsername());
	}

	public static Boolean userExist(Integer userId) {
		return (getUser(userId) == null ? false : true);
	}
	
	public static User getUserByUsername(String username) {
		try (Connection conn = sql2o.open()) {
			return conn.createQuery(SELECT_USER_BY_USERNAME_SQL)
					.addParameter("username", username)
					.executeAndFetchFirst(User.class);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static User getUser(Integer userId) {
		try (Connection conn = sql2o.open()) {
			return conn.createQuery(SELECT_USER_BY_USERID_SQL)
					.addParameter("user_id", userId)
					.executeAndFetchFirst(User.class);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<User> getAllUsers() {
		try (Connection conn = sql2o.open()) {
			return conn.createQuery(SELECT_ALL_USERS_SQL)
					.executeAndFetch(User.class);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static User updateUser(User user) {
		try (Connection conn = sql2o.beginTransaction()) {
			int count = conn.createQuery(UPDATE_USER_SQL)
					.addParameter("username", user.getUsername())
					.addParameter("first_name", user.getFirst_name())
					.addParameter("last_name", user.getLast_name())
					.addParameter("alias", user.getAlias())
					.addParameter("email", user.getEmail())
					.addParameter("role", user.getRole())
					.addParameter("salt", user.getSalt())
					.addParameter("password", user.getPassword())
					.addParameter("user_id", user.getUser_id())
					.executeUpdate().getResult();

			conn.commit();
			logger.info("Update User Count: "+count);
		}
		return getUserByUsername(user.getUsername());
	}

	public static User createUser(User user) {
		try (Connection conn = sql2o.beginTransaction()) {
			Integer count = conn.createQuery(INSERT_USER_SQL)
					.addParameter("username", user.getUsername())
					.addParameter("first_name", user.getFirst_name())
					.addParameter("last_name", user.getLast_name())
					.addParameter("alias", user.getAlias())
					.addParameter("email", user.getEmail())
					.addParameter("role", user.getRole())
					.addParameter("salt", user.getSalt())
					.addParameter("password", user.getPassword())
					.executeUpdate().getResult();

			conn.commit();
			logger.info("Create User Count: "+count);
		}
		return getUserByUsername(user.getUsername());
	}

	public static Integer deleteUser(Integer userId) {
		try (Connection conn = sql2o.beginTransaction()) {
			int count = conn.createQuery(DELETE_USER_SQL)
					.addParameter("user_id", userId)
					.executeUpdate().getResult();

			conn.commit();
			logger.info("Delete User Count: "+count);
			return count;
		}
	}

	public static void main(String[] args) {
		String username = "admin";
		String pwd = "nimda4now";
		String newSalt = "$2a$10$ZdCS2LELbU3BvWNibQbJre"; //BCrypt.gensalt();
		String newHashedPassword = BCrypt.hashpw(pwd, newSalt);
		logger.info("username="+username + ", newSalt="+newSalt + ", newHashedPassword="+newHashedPassword);
		
		/*for(User user : getAllUsers()) {
			logger.info(user.getDetails());
		}*/
	}
}
