package com.mayacarlsen.article;

import java.util.List;
import java.util.logging.Logger;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.mayacarlsen.util.DaoUtil;

public class ArticleDAO {

	private static final Logger logger = Logger.getLogger(ArticleDAO.class.getCanonicalName());

	private final static Sql2o sql2o = DaoUtil.getSql2o();

	private static final String SELECT_ALL_ARTICLES_SQL =
			"SELECT A.ARTICLE_ID, A.USER_ID, A.ARTICLE_TYPE, A.ARTICLE_TITLE, A.PUBLISH_ARTICLE, A.ARTICLE_DESCRIPTION, A.CREATE_DTTM, A.UPDATE_DTTM, "
					+ "U.FIRST_NAME, U.LAST_NAME "
					+ "FROM ARTICLES A, USERS U "
					+ "WHERE A.user_id = U.user_id";
	
	private static final String SELECT_ALL_PUBLISH_ARTICLES_SQL =
			"SELECT A.ARTICLE_ID, A.USER_ID, A.ARTICLE_TYPE, A.ARTICLE_TITLE, A.PUBLISH_ARTICLE, A.ARTICLE_DESCRIPTION, A.CREATE_DTTM, A.UPDATE_DTTM, "
					+ "U.FIRST_NAME, U.LAST_NAME "
					+ "FROM ARTICLES A, USERS U "
					+ "WHERE A.user_id = U.user_id AND A.PUBLISH_ARTICLE = TRUE";
	
	private static final String SELECT_ARTICLE_SQL =
			"SELECT A.ARTICLE_ID, A.ARTICLE_TYPE, A.ARTICLE_TITLE, A.PUBLISH_ARTICLE, A.ARTICLE_DESCRIPTION, A.ARTICLE, A.CREATE_DTTM, A.UPDATE_DTTM, "
					+ "U.FIRST_NAME, U.LAST_NAME "
					+ "FROM ARTICLES A, USERS U "
					+ "WHERE A.user_id = U.user_id AND article_id = :article_id";

	private static final String INSERT_ARTICLE_SQL =
			"INSERT INTO ARTICLES (USER_ID, ARTICLE_TYPE, ARTICLE_TITLE, PUBLISH_ARTICLE, ARTICLE_DESCRIPTION, ARTICLE, CREATE_DTTM) "
					+ "VALUES (:user_id, :article_type, :article_title, :publish_article, :article_description, :article, CURRENT_TIMESTAMP) ";

	private static final String UPDATE_ARTICLE_SQL =
			"UPDATE ARTICLES SET "
					+ "USER_ID = :user_id, "
					+ "ARTICLE_TYPE = :article_type, "
					+ "ARTICLE_TITLE = :article_title, "
					+ "ARTICLE_DESCRIPTION = :article_description, "
					+ "PUBLISH_ARTICLE = :publish_article, "
					+ "ARTICLE = :article, "
					+ "UPDATE_DTTM = CURRENT_TIMESTAMP "
					+ "WHERE article_id = :article_id";

	private static final String DELETE_ARTICLE_SQL =
			"DELETE FROM ARTICLES WHERE article_id = :article_id ";

	public static List<Article> getAllArticles() {
		try (Connection conn = sql2o.beginTransaction()) {
			return conn.createQuery(SELECT_ALL_ARTICLES_SQL)
					.executeAndFetch(Article.class);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Article> getAllPublishArticles() {
		try (Connection conn = sql2o.beginTransaction()) {
			return conn.createQuery(SELECT_ALL_PUBLISH_ARTICLES_SQL)
					.executeAndFetch(Article.class);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Boolean articleExist(Integer articleId) {
		return (getArticle(articleId) == null ? false : true);
	}
	
	public static Article getArticle(Integer articleId) {
		try (Connection conn = sql2o.beginTransaction()) {
			return conn.createQuery(SELECT_ARTICLE_SQL)
					.addParameter("article_id", articleId)
					.executeAndFetchFirst(Article.class);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Article updateArticle(Article article) {
		try (Connection conn = sql2o.beginTransaction()) {
			int count = conn.createQuery(UPDATE_ARTICLE_SQL)
					.addParameter("user_id", article.getUser_id())
					.addParameter("article_type", article.getArticle_type())
					.addParameter("article_title", article.getArticle_title())
					.addParameter("publish_article", article.getPublish_article())
					.addParameter("article_description", article.getArticle_description())
					.addParameter("article", article.getArticle())
					.addParameter("article_id", article.getArticle_id())
					.executeUpdate().getResult();

			conn.commit();
			logger.info("Update Article Count: "+count);
		}
		return getArticle(article.getArticle_id());
	}

	public static void createArticle(Article article) {
		try (Connection conn = sql2o.beginTransaction()) {
			Integer count = conn.createQuery(INSERT_ARTICLE_SQL)
					.addParameter("user_id", article.getUser_id())
					.addParameter("article_type", article.getArticle_type())
					.addParameter("article_title", article.getArticle_title())
					.addParameter("publish_article", article.getPublish_article())
					.addParameter("article_description", article.getArticle_description())
					.addParameter("article", article.getArticle())
					.executeUpdate().getResult();

			conn.commit();
			logger.info("Insert Article Count: "+count);
		}
	}

	public static Integer deleteArticle(Integer articleId) {
		try (Connection conn = sql2o.beginTransaction()) {
			int count = conn.createQuery(DELETE_ARTICLE_SQL)
					.addParameter("article_id", articleId)
					.executeUpdate().getResult();

			conn.commit();
			logger.info("Delete Article Count: "+count);
			return count;
		}
	}
}
