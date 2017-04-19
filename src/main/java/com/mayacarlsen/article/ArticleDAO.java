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
			"SELECT ARTICLE_ID, ARTICLE_TYPE, ARTICLE_TITLE, ARTICLE_DESCRIPTION, CREATE_DTTM, UPDATE_DTTM "
					+ "FROM ARTICLES ";
	
	private static final String SELECT_ARTICLE_SQL =
			"SELECT ARTICLE_ID, ARTICLE_TYPE, ARTICLE_TITLE, ARTICLE_DESCRIPTION, ARTICLE, CREATE_DTTM, UPDATE_DTTM "
					+ "FROM ARTICLES "
					+ "WHERE article_id = :article_id";

	private static final String INSERT_ARTICLE_SQL =
			"INSERT INTO ARTICLES (ARTICLE_TYPE, ARTICLE_TITLE, ARTICLE_DESCRIPTION, ARTICLE, CREATE_DTTM) "
					+ "VALUES (:article_type, :article_title, :article_description, :article, CURRENT_TIMESTAMP) ";

	private static final String UPDATE_ARTICLE_SQL =
			"UPDATE ARTICLES SET "
					+ "ARTICLE_TYPE = :article_type, "
					+ "ARTICLE_TITLE = :article_title, "
					+ "ARTICLE_DESCRIPTION = :article_description, "
					+ "ARTICLE = :article, "
					+ "UPDATE_DTTM = CURRENT_TIMESTAMP "
					+ "WHERE article_id = :article_id";

	public static List<Article> getAllArticles() {
		try (Connection conn = sql2o.beginTransaction()) {
			return conn.createQuery(SELECT_ALL_ARTICLES_SQL)
					.executeAndFetch(Article.class);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
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
					.addParameter("article_type", article.getArticle_type())
					.addParameter("article_title", article.getArticle_title())
					.addParameter("article_description", article.getArticle_description())
					.addParameter("article", article.getArticle())
					.addParameter("article_id", article.getArticle_id())
					.executeUpdate().getResult();

			logger.info("Update Article Count: "+count);
			conn.commit();
		}
		return getArticle(article.getArticle_id());
	}

	public static void createArticle(Article article) {
		try (Connection conn = sql2o.beginTransaction()) {
			Integer count = conn.createQuery(INSERT_ARTICLE_SQL)
					.addParameter("article_type", article.getArticle_type())
					.addParameter("article_title", article.getArticle_title())
					.addParameter("article_description", article.getArticle_description())
					.addParameter("article", article.getArticle())
					.executeUpdate().getResult();

			logger.info("Insert Article Count: "+count);
			conn.commit();
		}
	}

}
