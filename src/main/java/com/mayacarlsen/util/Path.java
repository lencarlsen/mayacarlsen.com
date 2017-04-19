package com.mayacarlsen.util;

import lombok.*;

public class Path {

    // The @Getter methods are needed in order to access
    // the variables from Velocity Templates
    public static class Web {
        @Getter public static final String ROOT = "/";
		@Getter public static final String PUBLIC = "/public";
        @Getter public static final String INDEX = "/index/";
        @Getter public static final String ABOUT = "/about/";
        @Getter public static final String LOGIN = "/login/";
        @Getter public static final String LOGOUT = "/logout/";
        @Getter public static final String CHAT = "/chat/";
        @Getter public static final String REGISTER = "/register/";
        @Getter public static final String USER_SETTINGS = "/user_settings/";
        @Getter public static final String SAVE_USER_SETTINGS = "/save_user_settings/";
        @Getter public static final String STORIES = "/stories/";
        @Getter public static final String ARTICLE = "/article/:articleId";

		@Getter public final static String UNAUTHORIZED = "/unauthorized/";
    }

    public static class Template {
        public final static String INDEX = "/velocity/index/index.vm";
        public final static String ABOUT = "/velocity/about/about.vm";
        public final static String LOGIN = "/velocity/login/login.vm";
        public static final String NOT_FOUND = "/velocity/notFound.vm";
        public final static String CHAT = "/velocity/chat/chat.vm";
        public final static String USER_SETTINGS = "/velocity/user/usersettings.vm";
        public final static String STORIES = "/velocity/stories/stories.vm";

		// Security
		public final static String UNAUTHORIZED = "/velocity/security/unauthorized_access.vm";

		public static final String ERROR = "/velocity/error.vm";
    }

}
