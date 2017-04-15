package com.mayacarlsen.user;

import lombok.*;

@AllArgsConstructor // All fields are private and final. Getters (but not setters) are generated (https://projectlombok.org/features/Value.html)
public class User {
    final @Getter String username;
    final @Getter String firstName;
    final @Getter String lastName;
    final @Getter String alias;
    final @Getter String emailAddress;
    @Getter @Setter String salt;
    @Getter @Setter String hashedPassword;

    public String getName() {
    	return toString();
    }
    
    public String toString() {
    	return (this.alias == null || this.alias.trim().isEmpty() 
    			? this.firstName + " " + this.lastName : this.alias);
    }
}
