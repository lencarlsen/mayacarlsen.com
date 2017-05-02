package com.mayacarlsen.file;

import java.util.Date;

import com.mayacarlsen.security.AuthObject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor // All fields are private and final. Getters (but not setters) are generated (https://projectlombok.org/features/Value.html)
public class File extends AuthObject {
    final @Getter Integer file_id;
    final @Getter Integer user_id;
    final @Getter String file_name;
    final @Getter String file_type;
    final @Getter String file_title;
    final @Getter byte[] file;
    final @Getter Boolean publish_file;
    final @Getter Date create_dttm;
    final @Getter Date update_dttm;
    final @Getter String first_name;
    final @Getter String last_name;
}
