package com.mayacarlsen.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.sql2o.Sql2o;

import com.mayacarlsen.file.FileDAO;

public class DatabaseCreator {
	private static final Logger logger = Logger.getLogger(DatabaseCreator.class.getCanonicalName());

	private static final String USER_TABLE_DROP_SQL =
			"DROP TABLE IF EXISTS users";
	
	private static final String USER_TABLE_CREATE_SQL = 
			"CREATE TABLE users ("
			+ "user_id SERIAL,"
			+ "username VARCHAR(50),"
			+ "last_name VARCHAR(50),"
			+ "first_name VARCHAR(50),"
			+ "alias VARCHAR(50),"
			+ "email VARCHAR(100),"
			+ "salt VARCHAR(50),"
			+ "password VARCHAR(100),"
			+ "role VARCHAR(50),"
			+ "create_dttm timestamp,"
			+ "update_dttm timestamp)";
	
	private static final String USER_TABLE_INSERT_SQL =
			"INSERT INTO users (username, last_name, first_name, alias, email, salt, password, role, create_dttm, update_dttm) "
			+ "VALUES ("
			+ "'admin', "
			+ "'Admin Last Name', "
			+ "'Admin First Name', "
			+ "'Administrator', "
			+ "'admin@admin', "
			+ "'$2a$10$ZdCS2LELbU3BvWNibQbJre', "
			+ "'$2a$10$ZdCS2LELbU3BvWNibQbJreMqywjR4MMWU.NiZBg1g4ndw61W8GL3.', "
			+ "'ADMIN', "
			+ "current_timestamp, "
			+ "current_timestamp)";

	private static final String RESET_ADMIN_USER_SQL = 
			"UPDATE USERS SET "
			+ "ROLE = 'ADMIN', "
			+ "SALT = '$2a$10$ZdCS2LELbU3BvWNibQbJre', "
			+ "PASSWORD = '$2a$10$ZdCS2LELbU3BvWNibQbJreMqywjR4MMWU.NiZBg1g4ndw61W8GL3.' "
			+ "WHERE user_id = 1";
	
	private static final String ARTICLE_TABLE_DROP_SQL =
			"DROP TABLE IF EXISTS articles";
	
	private static final String ARTICLE_TABLE_CREATE_SQL = 
			"CREATE TABLE articles ("
			+ "article_id SERIAL,"
			+ "user_id INTEGER,"
			+ "article_type VARCHAR(20),"
			+ "article_title VARCHAR(100),"
			+ "article_description VARCHAR(200),"
			+ "article TEXT,"
			+ "publish_article BOOLEAN,"
			+ "create_dttm timestamp,"
			+ "update_dttm timestamp)";
	
	private static final String ARTICLE_TABLE_INSERT_SQL =
			"INSERT INTO ARTICLES (USER_ID, ARTICLE_TYPE, ARTICLE_TITLE, ARTICLE_DESCRIPTION, ARTICLE, PUBLISH_ARTICLE, CREATE_DTTM) "
					+ "VALUES (1, 'STORY', 'Luna', 'A short story about Luna', "
					+ "'<p>I woke up from my bed. It was a ghastly storm. My cat Luna was right next to me. \"Meow\" Luna says to me. \"Oh, Luna how can you stay so calm in a storm like this?\" I asked Luna petting her soft, silky dark, fur. I look outside.  The storm suddenly stops. Then I see the bright, golden, moon. I yell to my parents: \"Mom? Dad? Look at this beautiful moon. It''s so bright and round.\" But I hear no response. I just assume that they''re fast asleep or outside somewhere.</p>"
					+ "<p>I slowly creep out of my room and into my parent''s room. I see no one in the bed. Luna is next to my leg. I go to the kitchen as my nightgown drags behind me. I see a cupcake on the counter. It looks deliciously made. I smell the warm cupcake swaying in the air. I slowly walk over to the cupcake. That''s strange. It''s warm. I think to myself. Luna jumps onto the counter and grabs the cupcake in her mouth. She jumps down and throws the cupcake into the garbage. \"Luna!\" I yell. \"That could''ve been moms! Maybe she left it there for me to eat or something….\" I look into Luna''s eyes sparkling in the moonlight. I thought; her name is perfect for this night.  I then feel a swish of wind behind me. I turn around. All I see is Luna staring back at me.  I hear a rumble. It came from the front door. I walk over to the front door. I put on my jacket and slowly pull up the zipper. I open the door. I see a ghostly figure floating directly in front of the big, bright, moon.</p>"
					+ "<p>It slowly comes down from the moon. I''m scared but intrigued. I grab Luna and I put her into my arms. The ghostly figure is right in front of me. Her dress sways in the air. Her long hair dripping down to her toes. \"Hello, Child.\" The ghostly figure says, I stay speechless. \"You will be granted one wish, but be careful for what you wish for.\"  I look into her eyes. Her eyes have no soul. \"Um... I wish for my parents to come back soon. I already miss them.\" I said to the ghostly figure. \"Alright then, is that what you really want to wish for dearie?\" the ghostly figure said in her slow, soft, calming voice. Then Luna tugs onto my nightgown, her fur blowing in the wind. Her eyes tell me something- that something''s not right.</p>"
					+ "<p>\"You won''t actually grant my wish. Will you? Luna says she can''t trust you. I can see it in her eyes.\" Then the figure suddenly turns into a horrifying creature. \"YOU SHOULDN''T HAVE DONE THAT, DEARIE.\" The creature shouts as she slowly disappears. It is midnight.</p>"
			        + "<p>It''s been 7 hours, my parents aren''t back. I hear a slow creak as my bedroom door opens.</p>"
			        + "<p><b>Maya Carlsen</b></p>', "
					+ "TRUE, "
					+ "CURRENT_TIMESTAMP) ";

	private static final String FILE_TABLE_DROP_SQL =
			"DROP TABLE IF EXISTS files";
	
	private static final String FILE_TABLE_CREATE_SQL = 
			"CREATE TABLE files ("
			+ "file_id SERIAL,"
			+ "user_id INTEGER,"
			+ "file_name VARCHAR(100),"
			+ "file_type VARCHAR(20),"
			+ "file_title VARCHAR(100),"
			+ "thumbnail BYTEA,"
			+ "file BYTEA,"
			+ "publish_file BOOLEAN,"
			+ "create_dttm TIMESTAMP,"
			+ "update_dttm TIMESTAMP)";
	
	private static final String FILE_TABLE_INSERT_SQL =
			"INSERT INTO files (user_id, file_name, file_type, file_title, file, publish_file, create_dttm) "
			+ "VALUES (1, ?, 'PNG', 'My Image', ?, TRUE, CURRENT_TIMESTAMP)";

	private void updateUser() {
		try (Connection conn = DAOUtil.getConnection()) {
			createPreparedStatement(conn, RESET_ADMIN_USER_SQL);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void createFile() {
		File file = new File("/Users/lencarlsen/Documents/eclipse/workspace/dadb/src/main/resources/public/img/dadb_home_page.png");

		try (Connection conn = DAOUtil.getConnection()) {
			FileInputStream fis = new FileInputStream(file);

			PreparedStatement ps = conn.prepareStatement(FILE_TABLE_INSERT_SQL);
			ps.setString(1, file.getName());
			ps.setBinaryStream(2, fis, (int)file.length());
			ps.executeUpdate();
			conn.commit();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void createTables() {
		try (Connection conn = DAOUtil.getConnection()) {
//			createPreparedStatement(conn, USER_TABLE_DROP_SQL);
//			createPreparedStatement(conn, USER_TABLE_CREATE_SQL);
//			createPreparedStatement(conn, USER_TABLE_INSERT_SQL);
//			createPreparedStatement(conn, ARTICLE_TABLE_DROP_SQL);
//			createPreparedStatement(conn, ARTICLE_TABLE_CREATE_SQL);
//			createPreparedStatement(conn, ARTICLE_TABLE_INSERT_SQL);
			createPreparedStatement(conn, FILE_TABLE_DROP_SQL);
			createPreparedStatement(conn, FILE_TABLE_CREATE_SQL);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void createPreparedStatement(Connection conn, String sql) throws SQLException {
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.executeUpdate();
		conn.commit();
	}

	private void executePreparedStatement(String sql) throws SQLException {
		try (Connection conn = DAOUtil.getConnection()) {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet rs = preparedStatement.executeQuery();
			
			while(rs.next()) {
				System.out.println("1: " + rs.getString(1));
				System.out.println("2: " + rs.getString(2));
				System.out.println("3: " + rs.getString(3));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		logger.info("Creating database...");
		DatabaseCreator databaseCreator = new DatabaseCreator();
		databaseCreator.createTables();
		databaseCreator.createFile();
		databaseCreator.executePreparedStatement(FileDAO.SELECT_ALL_FILES_SQL);
		logger.info("Creating database completed");
	}

}
