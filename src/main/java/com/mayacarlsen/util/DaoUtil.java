package com.mayacarlsen.util;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.sql2o.Sql2o;

public class DaoUtil {
	
	private static final Logger logger = Logger.getLogger(DaoUtil.class.getCanonicalName());

	// Heroku PostgreSQL database connection from system environment variable
	private static final String HEROKU_DATABASE_URL_ENV = System.getenv("JDBC_DATABASE_URL");
	
	// Heroku PostgreSQL database connection URL
	private static final String HEROKU_DATABASE_URL = "jdbc:postgresql://ec2-54-225-71-119.compute-1.amazonaws.com:5432/df1ee1153d9ulv?user=lnvfbhpyxbknmy&password=201decc8aa33924cfd161680d73336add505fe894ccd84cf37d1eeddf347c6fa&sslmode=require";
	
	private final static Sql2o sql2o = new Sql2o(HEROKU_DATABASE_URL, null, null);

	private static final String USER_TABLE_DROP =
			"DROP TABLE IF EXISTS users";
	
	private static final String USER_TABLE_CREATE = 
			"CREATE TABLE users ("
			+ "user_id SERIAL,"
			+ "username VARCHAR(50),"
			+ "last_name VARCHAR(50),"
			+ "first_name VARCHAR(50),"
			+ "alias VARCHAR(50),"
			+ "email VARCHAR(100),"
			+ "salt VARCHAR(50),"
			+ "password VARCHAR(100),"
			+ "create_dttm timestamp,"
			+ "update_dttm timestamp)";
	
	private static final String USER_TABLE_INSERT =
			"INSERT INTO users (username, last_name, first_name, alias, email, salt, password, create_dttm, update_dttm) "
			+ "VALUES ("
			+ "'admin', "
			+ "'Admin Last Name', "
			+ "'Admin First Name', "
			+ "'Administrator', "
			+ "'admin@admin', "
			+ "'$2a$10$ZdCS2LELbU3BvWNibQbJre', "
			+ "'$2a$10$ZdCS2LELbU3BvWNibQbJreMqywjR4MMWU.NiZBg1g4ndw61W8GL3.', "
			+ "current_timestamp, "
			+ "current_timestamp)";
	
	public static Sql2o getSql2o() {
		return sql2o;
	}

	/**
	 * Gets a database connection.
	 * 
	 * @return Database connection
	 * @throws URISyntaxException
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	public static Connection getConnection() throws URISyntaxException, SQLException {
		return DriverManager.getConnection(HEROKU_DATABASE_URL);
	}
	
	private void createTables() {
		try (Connection conn = getConnection()) {
			createPreparedStatement(conn, USER_TABLE_DROP);
			createPreparedStatement(conn, USER_TABLE_CREATE);
			createPreparedStatement(conn, USER_TABLE_INSERT);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void createPreparedStatement(Connection conn, String sql) throws SQLException {
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.executeUpdate();
	}

	public static void main(String[] args) throws Exception {
		logger.info("Creating database...");
		new DaoUtil().createTables();
		logger.info("Creating database completed");
	}
	
}
