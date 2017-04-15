package com.mayacarlsen.chat;


import spark.*;
import java.util.*;

import com.mayacarlsen.index.Path;
import com.mayacarlsen.index.ViewUtil;
import com.mayacarlsen.login.LoginController;

public class ChatController {
    public static Route serveChatPage = (Request request, Response response) -> {
    	LoginController.ensureUserIsLoggedIn(request, response);
        Map<String, Object> model = new HashMap<>();
//        model.put("users", userDao.getAllUserNames());
        return ViewUtil.render(request, model, Path.Template.CHAT);
    };
}
