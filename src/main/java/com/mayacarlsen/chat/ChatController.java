package com.mayacarlsen.chat;


import spark.*;
import java.util.*;

import com.mayacarlsen.login.LoginController;
import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.ViewUtil;

public class ChatController {
    public static Route serveChatPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
//        model.put("users", userDao.getAllUserNames());
        return ViewUtil.render(request, model, Path.Template.CHAT);
    };
}
