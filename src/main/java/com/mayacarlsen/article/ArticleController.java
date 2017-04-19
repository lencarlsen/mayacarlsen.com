package com.mayacarlsen.article;

import java.util.HashMap;
import java.util.Map;

import com.mayacarlsen.util.Path;
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
    	model.put("article", new Article(null, null, null, null, null, null, null ));
		
        return ViewUtil.render(request, model, Path.Template.SAVE_ARTICLE);
    };

    public static Route saveArticlePage = (Request request, Response response) -> {
        String articleType = request.queryParams("article_type");
        String articleTitle = request.queryParams("article_title");
        String articleDescription = request.queryParams("article_description");
        String articleText = request.queryParams("article_text");
        
        Article article = new Article(null, articleType, articleTitle, articleDescription, articleText, null, null);
		ArticleDAO.createArticle(article);
        
    	Map<String, Object> model = new HashMap<>();
    	model.put("article", new Article(null, null, null, null, null, null, null ));
		
        return ViewUtil.render(request, model, Path.Template.SAVE_ARTICLE);
    };
}
