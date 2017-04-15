package com.mayacarlsen.user;

import java.util.*;
import java.util.stream.*;

public class UserDao {

    private final static Map<String,User> USERS = new HashMap<String,User>();

    static {
        //                          Username FirstName LastName Alias            Email Salt for hash                    Hashed password (password='nimda')
        USERS.put("admin", new User("admin", "",       "",      "Administrator", null, "$2a$10$d8TjHuDb9ozYV4siKlUDTu", "$2a$10$d8TjHuDb9ozYV4siKlUDTuYeOIfWpuKJOQmSNHcdAokSSGtxixoZK"));
    }

    public static Boolean usernameExist(String username) {
    	return USERS.containsKey(username);
    }
    
    public static Boolean userExist(User user) {
    	return USERS.containsKey(user.getUsername());
    }
    
    public static User createUser(User user) {
    	USERS.put(user.getUsername(), user);
    	return USERS.get(user.getUsername());
    }
    
    public static User updateUser(User user) {
    	USERS.put(user.getUsername(), user);
    	return USERS.get(user.getUsername());
    }
    
//    public static int[] getIndicesByUsername(String username) {
//    	return IntStream.range(0, USERS.size()).filter(i -> USERS.get(i).getUsername().equals(username)).toArray();
//    }
    
    public static User getUserByUsername(String username) {
//        return USERS.stream().filter(b -> b.getUsername().equals(username)).findFirst().orElse(null);
    	return USERS.get(username);
    }

    public static Iterable<String> getAllUserNames() {
        return USERS.values().stream().map(User::getUsername).collect(Collectors.toList());
    }

}
