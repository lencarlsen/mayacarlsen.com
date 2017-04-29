package com.mayacarlsen.admin;

import java.util.HashMap;
import java.util.Map;

import com.mayacarlsen.article.Article;
import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.ViewUtil;

import spark.Request;
import spark.Response;
import spark.Route;

public class AdminController {
    public static Route serveAdminPage = (Request request, Response response) -> {
    	Map<String, Object> model = new HashMap<>();
    	model.put("article", new Article(null, null, null, null, null, null, null, null, null, null, null ));
		
        return ViewUtil.render(request, model, Path.Template.ADMIN);
    };
}
