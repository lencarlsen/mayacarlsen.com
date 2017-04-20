package com.mayacarlsen.article;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor // All fields are private and final. Getters (but not setters) are generated (https://projectlombok.org/features/Value.html)
public class Article {
    final @Getter Integer article_id;
    final @Getter Integer user_id;
    final @Getter String article_type;
    final @Getter String article_title;
    final @Getter Boolean publish_article;
    final @Getter String article_description;
    final @Getter String article;
    final @Getter Date create_dttm;
    final @Getter Date update_dttm;

	private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public String getCreated_dttm_string() {
		return (create_dttm == null ? null : format.format(create_dttm));
	}

	public String getUpdated_dttm_string() {
		return (update_dttm == null ? null : format.format(update_dttm));
	}
}
