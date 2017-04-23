package com.mayacarlsen.article;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mayacarlsen.security.AuthorizedList;
import com.mayacarlsen.user.User;
import com.mayacarlsen.util.JSONUtil;
import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.RequestUtil;
import com.mayacarlsen.util.ViewUtil;

import spark.Request;
import spark.Response;
import spark.Route;

public class ArticleController {

    public static Route serveStoryPage = (Request request, Response response) -> {
		String articleId = request.params("articleId");
        
    	Map<String, Object> model = new HashMap<>();

		Article article = ArticleDAO.getArticle(Integer.valueOf(articleId));
		if (article != null) {
			model.put("article", article);
		}
		
        return ViewUtil.render(request, model, Path.Template.VIEW_ARTICLES);
    };

    public static Route serveArticlePage = (Request request, Response response) -> {
    	Map<String, Object> model = new HashMap<>();
    	model.put("article", new Article(null, null, null, null, null, null, null, null, null, null, null ));
		
        return ViewUtil.render(request, model, Path.Template.ADMIN);
    };

    public static Route saveArticlePage = (Request request, Response response) -> {
        String articleId = request.queryParams("article_id");
        String articleType = request.queryParams("article_type");
        String articleTitle = request.queryParams("article_title");
        String publishArticle = request.queryParams("publish_article");
        String articleDescription = request.queryParams("article_description");
        String articleText = request.queryParams("article_text");
        
        Integer articlaeIdInt = (articleId == null || articleId.trim().isEmpty() ? null : Integer.valueOf(articleId));
        		 
    	User loggedInUser = request.session().attribute("user");

    	Article article = new Article(
    			articlaeIdInt, 
    			loggedInUser.getUser_id(), 
    			articleType, 
    			articleTitle, 
    			Boolean.valueOf(publishArticle), 
    			articleDescription, 
    			articleText, 
    			null, null, null, null);

    	if (ArticleDAO.articleExist(articlaeIdInt)) {
    		ArticleDAO.updateArticle(article);
    	} else {
        	ArticleDAO.createArticle(article);
    	}
        
    	Map<String, Object> model = new HashMap<>();
    	model.put("article", new Article(null, null, null, null, null, null, null, null, null, null, null ));
		
        return ViewUtil.render(request, model, Path.Template.ADMIN);
    };

    public static Route getAllArticlesAsJSON = (Request request, Response response) -> {
    	List<Article> articleList = ArticleDAO.getAllArticles();
    	AuthorizedList<Article> list = new AuthorizedList<>(RequestUtil.getSessionUser(request), articleList);
    	String json = JSONUtil.dataToJson(list.getList());

		response.status(200);
		response.type("application/json");

		return json;
    };

    public static Route getArticleAsJSON = (Request request, Response response) -> {
		String articleId = request.params("articleId");
		Article article = ArticleDAO.getArticle(Integer.valueOf(articleId));
    	String json = JSONUtil.dataToJson(article);

		response.status(200);
		response.type("application/json");

		return json;
    };

}
