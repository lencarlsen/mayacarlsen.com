package com.mayacarlsen.util;

import javax.activation.MimetypesFileTypeMap;

public class IOUtil {

    public static String getFileType(String filename) {
        final MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
        return fileTypeMap.getContentType(filename);
    }        

}
