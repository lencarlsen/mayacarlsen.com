package com.mayacarlsen.file;

import java.util.Arrays;
import java.util.Date;

import com.mayacarlsen.security.AuthObject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor // All fields are private and final. Getters (but not setters) are generated
		    // (https://projectlombok.org/features/Value.html)
public class File extends AuthObject {
    final @Getter Integer file_id;
    final @Getter Integer user_id;
    final @Getter String file_name;
    final @Getter String file_type;
    final @Getter String file_title;
    final @Getter byte[] thumbnail;
    final @Getter byte[] file;
    final @Getter Boolean publish_file;
    final @Getter Date create_dttm;
    final @Getter Date update_dttm;
    final @Getter String first_name;
    final @Getter String last_name;

    public String getPublish_file_string() {
	return (publish_file == null ? "false" : publish_file.toString());
    }

    @Override public String toString() {
	return "File [file_id=" + file_id + ", user_id=" + user_id + ", file_name=" + file_name + ", file_type="
		+ file_type + ", file_title=" + file_title + ", thumbnail=" + Arrays.toString(thumbnail) + ", file="
		+ Arrays.toString(file) + ", publish_file=" + publish_file + ", create_dttm=" + create_dttm
		+ ", update_dttm=" + update_dttm + ", first_name=" + first_name + ", last_name=" + last_name + "]";
    }
}
