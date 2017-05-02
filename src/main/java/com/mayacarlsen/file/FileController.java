package com.mayacarlsen.file;

import java.io.OutputStream;
import java.io.IOException;

import spark.Request;
import spark.Response;
import spark.Route;

public class FileController {
	
    public static Route getFile = (Request request, Response response) -> {
		String fileId = request.params("fileId");

		File file = FileDAO.getFile(Integer.valueOf(fileId));
		byte[] bytes = file.file;
		
		try (OutputStream outputStream = response.raw().getOutputStream()) {
			outputStream.write(bytes);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
        return null;
    };


}
