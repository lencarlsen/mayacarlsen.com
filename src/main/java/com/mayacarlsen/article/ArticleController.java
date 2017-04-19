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
		
        return ViewUtil.render(request, model, Path.Template.STORIES);
    };
}
