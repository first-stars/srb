package com.w.srb.oss.service;

import java.io.InputStream;

/**
 * @author xin
 * @date 2022-10-11-16:15
 */

public interface FileService {

    /**
     * 上传文件到阿里云
     * @param inputStream
     * @param module
     * @param fileName
     * @return
     */
    String upload(InputStream inputStream,String module,String fileName);

    void removeFile(String url);
}
