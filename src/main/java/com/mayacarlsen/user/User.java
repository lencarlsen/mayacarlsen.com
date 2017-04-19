package com.mayacarlsen.user;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.*;

@AllArgsConstructor // All fields are private and final. Getters (but not setters) are generated (https://projectlombok.org/features/Value.html)
public class User {
    final @Getter Integer user_id;
    final @Getter String username;
    final @Getter String first_name;
    final @Getter String last_name;
    final @Getter String alias;
    final @Getter String avitar;
    final @Getter String email;
    @Getter @Setter String salt;
    @Getter @Setter String password;
    final @Getter Date create_dttm;
    final @Getter Date update_dttm;

	public String getName() {
    	return toString();
    }
    
	public String getDetails() {
    	return "user_id="+user_id+", username="+username+", first_name="+first_name+", last_name="+last_name
    			+", alias="+alias+", email="+email+", salt="+salt+", password="+password+", create_dttm="+create_dttm+", update_dttm="+update_dttm;
    }
    
    public String toString() {
    	return (this.alias == null || this.alias.trim().isEmpty() 
    			? this.first_name + " " + this.last_name : this.alias);
    }

	private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public String getCreated_dttm_string() {
		return (create_dttm == null ? null : format.format(create_dttm));
	}

	public String getUpdated_dttm_string() {
		return (update_dttm == null ? null : format.format(update_dttm));
	}
}
