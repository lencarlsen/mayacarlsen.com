package com.mayacarlsen.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONUtil {
	
	public static final String JSON_CONTENT_TYPE = "application/json";
	
	public static String dataToJson(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(data);
        } catch (IOException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

	public static <T> T toObject(String json, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, clazz);
        } catch (IOException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
