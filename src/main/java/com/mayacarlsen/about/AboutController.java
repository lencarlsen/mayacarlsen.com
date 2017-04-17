package com.mayacarlsen.about;

import java.util.HashMap;
import java.util.Map;

import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.ViewUtil;

import spark.Request;
import spark.Response;
import spark.Route;

public class AboutController {
    public static Route serveAboutPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, Path.Template.ABOUT);
    };
}
