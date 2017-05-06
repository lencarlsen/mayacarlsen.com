package com.mayacarlsen.file;

import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.mayacarlsen.util.DAOUtil;

/**
 * Unit test for <code>com.mayacarlsen.file.FileDAO</code>
 */
public class FileDAOTest {
    private static final Logger logger = Logger.getLogger(FileDAOTest.class.getCanonicalName());

    @AfterClass 
    public static void afterMethod() throws Exception {
	DAOUtil.closeDatasource();
    }

    @Test 
    public void testCreateFile() throws Exception {
	File file1 = new File(null, 1, "Test1", "Test1", "Test1", null, null, true, null, null, null, null);
	Integer fileId = FileDAO.createFile(file1);

	Assert.assertNotNull(fileId);

	File file2 = FileDAO.getFile(fileId);

	Assert.assertEquals(fileId, file2.getFile_id());
	Assert.assertEquals(file1.getFile_name(), file2.getFile_name());
	Assert.assertEquals(file1.getFile_title(), file2.getFile_title());
	Assert.assertEquals(file1.getFile_type(), file2.getFile_type());
	Assert.assertEquals(file1.getPublish_file(), file2.getPublish_file());
	Assert.assertEquals(file1.getThumbnail(), file2.getThumbnail());
	Assert.assertEquals(file1.getFile(), file2.getFile());
	Assert.assertEquals(file1.getUser_id(), file2.getUser_id());
	Assert.assertNotNull(file2.getCreate_dttm());

	int deleteCount = FileDAO.deleteFile(fileId);
	Assert.assertEquals(1, deleteCount);
    }

    @Test 
    public void testUpdateFile() throws Exception {
	File file1 = new File(null, 1, "Test1", "Test1", "Test1", null, null, true, null, null, null, null);
	Integer fileId = FileDAO.createFile(file1);

	Assert.assertNotNull(fileId);

	File file2 = new File(fileId, 1, "Test2", "Test2", "Test2", null, null, false, null, null, null, null);
	int updateCount = FileDAO.updateFile(file2);

	Assert.assertEquals(1, updateCount);

	File file3 = FileDAO.getFile(fileId);
	Assert.assertEquals(file2.getFile_id(), file3.getFile_id());
	Assert.assertEquals(file2.getFile_name(), file3.getFile_name());
	Assert.assertEquals(file2.getFile_title(), file3.getFile_title());
	Assert.assertEquals(file2.getFile_type(), file3.getFile_type());
	Assert.assertEquals(file2.getPublish_file(), file3.getPublish_file());
	Assert.assertEquals(file2.getThumbnail(), file3.getThumbnail());
	Assert.assertEquals(file2.getFile(), file3.getFile());
	Assert.assertEquals(file2.getUser_id(), file3.getUser_id());
	Assert.assertNotNull(file3.getUpdate_dttm());

	int deleteCount = FileDAO.deleteFile(fileId);
	Assert.assertEquals(1, deleteCount);
    }

    @Test 
    public void testDeleteFile() throws Exception {
	File file1 = new File(null, 1, "Test1", "Test1", "Test1", null, null, true, null, null, null, null);
	Integer fileId = FileDAO.createFile(file1);

	int deleteCount = FileDAO.deleteFile(fileId);
	Assert.assertEquals(1, deleteCount);
    }
}
