package com.mayacarlsen.util;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessageBundle {

    private ResourceBundle messages;

    public MessageBundle(String languageTag) {
        Locale locale = languageTag != null ? new Locale(languageTag) : Locale.ENGLISH;
        this.messages = ResourceBundle.getBundle("localization/messages", locale);
    }

    public String get(String message) {
        try {
        	// Hack to get Japanese character encoding to work in UTF-8 encoded files
			return new String(messages.getString(message).getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
    }

    public final String get(final String key, final Object... args) {
        return MessageFormat.format(get(key), args);
    }

}
