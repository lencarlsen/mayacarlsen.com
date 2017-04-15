package com.mayacarlsen.index;

import spark.*;
import java.util.*;

public class IndexController {
    public static Route serveIndexPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
//        model.put("users", userDao.getAllUserNames());
//        model.put("book", bookDao.getRandomBook());
        return ViewUtil.render(request, model, Path.Template.INDEX);
    };
}
