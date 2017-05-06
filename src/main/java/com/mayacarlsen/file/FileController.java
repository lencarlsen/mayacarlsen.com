package com.mayacarlsen.file;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import com.mayacarlsen.security.AuthorizedList;
import com.mayacarlsen.user.User;
import com.mayacarlsen.util.ImageUtil;
import com.mayacarlsen.util.JSONUtil;
import com.mayacarlsen.util.Path;
import com.mayacarlsen.util.RequestUtil;
import com.mayacarlsen.util.ViewUtil;

import spark.Request;
import spark.Response;
import spark.Route;

public class FileController {

    private static final Logger logger = Logger.getLogger(FileController.class.getCanonicalName());

    private static final String FILE_DELETED_MSG = "File '%1s' Successfully Deleted";
    private static final String FILE_DELETED_ERROR_MSG = "File '%1s' Could Not be Deleted";
    private static final String FILE_NOT_EXIST_MSG = "File '%1s' Does Not Exist";

    public static Route serveImagesPage = (Request request, Response response) -> {
	Map<String, Object> model = new HashMap<>();

	List<File> fileList = FileDAO.getAllFiles();
	if (fileList != null) {
	    model.put("fileList", fileList);
	}

	return ViewUtil.render(request, model, Path.Template.VIEW_IMAGES);
    };

    public static Route uploadFile = (Request request, Response response) -> {
	User user = RequestUtil.getSessionUser(request);
	try {
	    MultipartConfigElement multipartConfigElement = new MultipartConfigElement(".");
	    request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

	    Part filePart = request.raw().getPart("file");
	    String fileName = filePart.getSubmittedFileName();
	    logger.info("File Upload: Name=" + filePart.getName() + ", size=" + filePart.getSize() + ", filename="
		    + fileName + ", content-type=" + filePart.getContentType());

	    InputStream is = filePart.getInputStream();
	    DataInputStream data = new DataInputStream(is);
	    byte[] fileBytes = new byte[is.available()];
	    data.readFully(fileBytes);

	    String fType = ImageUtil.getFileType(new ByteArrayInputStream(fileBytes));
	    String fileType = (fType == null || fType.trim().isEmpty() ? "UNKNOWN" : fType.toUpperCase());

	    byte[] scaledImageBytes = ImageUtil.scaleImage(fileBytes, 1920, true);
	    byte[] thumbnail = ImageUtil.scaleImage(fileBytes, 200, false);

	    // For some reason request.queryParams must come last otherwise file upload will fail
	    String id = request.queryParams("file_id");
	    Integer fileId = (id == null ? null : Integer.valueOf(id));
	    String fileTitle = request.queryParams("file_title");
	    String publishFile = request.queryParams("publish_file");

	    File file = new File(fileId, user.getUser_id(), fileName, fileType, fileTitle, thumbnail, scaledImageBytes,
		    Boolean.valueOf(publishFile), null, null, null, null);

	    if (fileId != null && FileDAO.fileExist(fileId)) {
		FileDAO.updateFile(file);
	    } else {
		FileDAO.createFile(file);
	    }

	    Map<String, Object> model = new HashMap<>();
	    return ViewUtil.render(request, model, Path.Template.ADMIN);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.log(Level.SEVERE, e.getMessage(), e);
	    throw e;
	}
    };

    public static Route getPublishedThumbnail = (Request request, Response response) -> {
	String fileId = request.params("fileId");

	File file = FileDAO.getPublishedThumbnail(Integer.valueOf(fileId));
	byte[] imageBytes = file.thumbnail;

	try (OutputStream outputStream = response.raw().getOutputStream()) {
	    outputStream.write(imageBytes);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}

	return null;
    };

    public static Route getPublishedImage = (Request request, Response response) -> {
	String fileId = request.params("fileId");

	File file = FileDAO.getPublishedFile(Integer.valueOf(fileId));
	byte[] imageBytes = file.file;

	try (OutputStream outputStream = response.raw().getOutputStream()) {
	    outputStream.write(imageBytes);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}

	return null;
    };

    public static Route getFileInfo = (Request request, Response response) -> {
	String fileId = request.params("fileId");

	File file = FileDAO.getFileInfo(Integer.valueOf(fileId));
	String json = JSONUtil.dataToJson(file);

	response.status(200);
	response.type("application/json");

	return json;
    };

    public static Route getFile = (Request request, Response response) -> {
	String fileId = request.params("fileId");

	File file = FileDAO.getFile(Integer.valueOf(fileId));
	byte[] bytes = file.file;

	try (OutputStream outputStream = response.raw().getOutputStream()) {
	    outputStream.write(bytes);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}

	return null;
    };

    public static Route getAllFilesAsJSON = (Request request, Response response) -> {
	List<File> fileList = FileDAO.getAllFiles();
	AuthorizedList<File> list = new AuthorizedList<>(RequestUtil.getSessionUser(request), fileList);
	String json = JSONUtil.dataToJson(list.getList());

	response.status(200);
	response.type("application/json");

	return json;
    };

    public static Route deleteFileAsJSON = (Request request, Response response) -> {
	Integer fileId = Integer.valueOf(request.params("fileId"));

	String json = JSONUtil.dataToJson("Error");
	if (FileDAO.fileExist(fileId)) {
	    Integer count = FileDAO.deleteFile(fileId);
	    if (count > 0) {
		response.status(200);
		json = JSONUtil.dataToJson(String.format(FILE_DELETED_MSG, fileId));
	    } else {
		response.status(404);
		json = JSONUtil.dataToJson(String.format(FILE_DELETED_ERROR_MSG, fileId));
	    }
	} else {
	    response.status(404);
	    json = JSONUtil.dataToJson(String.format(FILE_NOT_EXIST_MSG, fileId));
	}

	response.type("application/json");

	return json;
    };
}
