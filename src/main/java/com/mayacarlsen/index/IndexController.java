package com.mayacarlsen.index;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.ViewMessageConstants;
import com.mayacarlsen.util.ViewUtil;

import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Route;

public class IndexController {
	private static final Logger logger = Logger.getLogger(IndexController.class.getCanonicalName());

    public static Route serveIndexPage = (Request request, Response response) -> {
        return ViewUtil.render(request, new HashMap<String, Object>(), Path.Template.INDEX);
    };

	public static ExceptionHandler serveErrorPage = (Exception exception, Request request, Response response) -> {
		logger.log(Level.SEVERE, exception.getMessage(), exception);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put(ViewMessageConstants.ERROR_MESSAGE, exception);
		model.put(ViewMessageConstants.ERROR_DETAILS, exception.getCause());
		String s = ViewUtil.render(request, model, Path.Template.ERROR);
		response.body(s);
	};
}
