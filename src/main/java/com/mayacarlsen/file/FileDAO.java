package com.mayacarlsen.file;

import java.util.List;
import java.util.logging.Logger;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.mayacarlsen.util.DAOUtil;

public class FileDAO {

    private static final Logger logger = Logger.getLogger(FileDAO.class.getCanonicalName());

    private final static Sql2o sql2o = DAOUtil.getSql2o();

    public static final String SELECT_ALL_FILES_SQL = "SELECT F.FILE_ID, F.USER_ID, F.FILE_NAME, F.FILE_TYPE, F.FILE_TITLE, F.PUBLISH_FILE, F.CREATE_DTTM, F.UPDATE_DTTM, "
	    + "U.FIRST_NAME, U.LAST_NAME " + "FROM FILES F, USERS U " + "WHERE F.user_id = U.user_id";

    private static final String SELECT_ALL_PUBLISH_FILES_SQL = "SELECT F.FILE_ID, F.USER_ID, F.FILE_NAME, F.FILE_TYPE, F.FILE_TITLE, /*F.FILE,*/ F.PUBLISH_FILE, F.CREATE_DTTM, F.UPDATE_DTTM, "
	    + "U.FIRST_NAME, U.LAST_NAME " + "FROM FILES F, USERS U "
	    + "WHERE F.user_id = U.user_id AND F.publish_file = TRUE";

    public static final String SELECT_FILE_SQL = "SELECT F.FILE_ID, F.USER_ID, F.FILE_NAME, F.FILE_TYPE, F.FILE_TITLE, F.FILE, F.PUBLISH_FILE, F.CREATE_DTTM, F.UPDATE_DTTM, "
	    + "U.FIRST_NAME, U.LAST_NAME " + "FROM FILES F, USERS U "
	    + "WHERE F.user_id = U.user_id AND F.file_id = :file_id";

    public static final String SELECT_PUBLISHED_FILE_SQL = "SELECT F.FILE_ID, F.USER_ID, F.FILE_NAME, F.FILE_TYPE, F.FILE_TITLE, F.FILE, F.PUBLISH_FILE, F.CREATE_DTTM, F.UPDATE_DTTM, "
	    + "U.FIRST_NAME, U.LAST_NAME " + "FROM FILES F, USERS U "
	    + "WHERE F.user_id = U.user_id AND F.file_id = :file_id and F.publish_file = TRUE";

    public static final String SELECT_PUBLISHED_THUMBNAIL_SQL = "SELECT F.FILE_ID, F.USER_ID, F.FILE_NAME, F.FILE_TYPE, F.FILE_TITLE, F.THUMBNAIL, F.PUBLISH_FILE, F.CREATE_DTTM, F.UPDATE_DTTM, "
	    + "U.FIRST_NAME, U.LAST_NAME " + "FROM FILES F, USERS U "
	    + "WHERE F.user_id = U.user_id AND F.file_id = :file_id and F.publish_file = TRUE";

    public static final String SELECT_FILE_INFO_SQL = "SELECT F.FILE_ID, F.USER_ID, F.FILE_NAME, F.FILE_TYPE, F.FILE_TITLE, F.PUBLISH_FILE, F.CREATE_DTTM, F.UPDATE_DTTM, "
	    + "U.FIRST_NAME, U.LAST_NAME " + "FROM FILES F, USERS U "
	    + "WHERE F.user_id = U.user_id AND F.file_id = :file_id";

    private static final String INSERT_FILE_SQL = "INSERT INTO files (user_id, file_name, file_type, file_title, thumbnail, file, publish_file, create_dttm) "
	    + "VALUES (:user_id, :file_name, :file_type, :file_title, :thumbnail, :file, :publish_file, CURRENT_TIMESTAMP)";

    private static final String UPDATE_FILE_SQL = "UPDATE files SET " + "user_id = :user_id, "
	    + "file_name = :file_name, " + "file_type = :file_type, " + "file_title = :file_title, "
	    + "thumbnail = :thumbnail, " + "file = :file, " + "publish_file = :publish_file, "
	    + "update_dttm = CURRENT_TIMESTAMP " + "WHERE file_id = :file_id";

    private static final String DELETE_FILE_SQL = "DELETE FROM FILES WHERE file_id = :file_id ";

    public static List<File> getAllFiles() {
	try (Connection conn = sql2o.open()) {
	    return conn.createQuery(SELECT_ALL_FILES_SQL).executeAndFetch(File.class);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static List<File> getAllPublishFiles() {
	try (Connection conn = sql2o.open()) {
	    return conn.createQuery(SELECT_ALL_PUBLISH_FILES_SQL).executeAndFetch(File.class);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static File getFile(Integer fileId) {
	try (Connection conn = sql2o.open()) {
	    return conn.createQuery(SELECT_FILE_SQL).addParameter("file_id", fileId).executeAndFetchFirst(File.class);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static File getPublishedFile(Integer fileId) {
	try (Connection conn = sql2o.open()) {
	    return conn.createQuery(SELECT_PUBLISHED_FILE_SQL).addParameter("file_id", fileId).executeAndFetchFirst(
		    File.class);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static File getPublishedThumbnail(Integer fileId) {
	try (Connection conn = sql2o.open()) {
	    return conn.createQuery(SELECT_PUBLISHED_THUMBNAIL_SQL).addParameter("file_id", fileId)
		    .executeAndFetchFirst(File.class);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static File getFileInfo(Integer fileId) {
	try (Connection conn = sql2o.open()) {
	    return conn.createQuery(SELECT_FILE_INFO_SQL).addParameter("file_id", fileId).executeAndFetchFirst(
		    File.class);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static Boolean fileExist(Integer fileId) {
	return (getFileInfo(fileId) == null ? false : true);
    }

    public static Integer createFile(File file) {
	try (Connection conn = sql2o.beginTransaction()) {
	    Integer fileId = (Integer) conn.createQuery(INSERT_FILE_SQL, true).addParameter("user_id", file
		    .getUser_id()).addParameter("file_name", file.getFile_name()).addParameter("file_type", file
			    .getFile_type()).addParameter("file_title", file.getFile_title()).addParameter("thumbnail",
				    file.getThumbnail()).addParameter("file", file.getFile()).addParameter(
					    "publish_file", file.getPublish_file()).executeUpdate().getKey();

	    conn.commit();
	    logger.info("Create File fileId = " + fileId);
	    return fileId;
	}
    }

    public static Integer updateFile(File file) {
	try (Connection conn = sql2o.beginTransaction()) {
	    int count = conn.createQuery(UPDATE_FILE_SQL).addParameter("user_id", file.getUser_id()).addParameter(
		    "file_name", file.getFile_name()).addParameter("file_type", file.getFile_type()).addParameter(
			    "file_title", file.getFile_title()).addParameter("thumbnail", file.getThumbnail())
		    .addParameter("file", file.getFile()).addParameter("publish_file", file.getPublish_file())
		    .addParameter("file_id", file.getFile_id()).executeUpdate().getResult();

	    conn.commit();
	    logger.info("Update File Count: " + count);
	    return count;
	}
    }

    public static Integer deleteFile(Integer fileId) {
	try (Connection conn = sql2o.beginTransaction()) {
	    int count = conn.createQuery(DELETE_FILE_SQL).addParameter("file_id", fileId).executeUpdate().getResult();

	    conn.commit();
	    logger.info("Delete File Count: " + count);
	    return count;
	}
    }

}
