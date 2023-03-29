package com.w.srb.oss.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.w.srb.oss.service.FileService;
import com.w.srb.oss.util.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

/**
 * @author xin
 * @date 2022-10-11-16:16
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {


    @Override
    public String upload(InputStream inputStream, String module, String fileName) {

        OSS oss = new OSSClientBuilder().build(
                OssProperties.ENDPOINT,
                OssProperties.KEY_ID,
                OssProperties.KEY_SECRET
        );
        log.info(OssProperties.BUCKET_NAME);
        boolean b = oss.doesBucketExist(OssProperties.BUCKET_NAME);

        log.info(String.valueOf(b));
        //判断oss实例是否存在，如果不存在，创造实例
        try {
            if (!b){
                log.info("1");
                //创建bucket
                oss.createBucket(OssProperties.BUCKET_NAME);
                log.info("2");
                //设置oss的访问权限，公共读
                oss.setBucketAcl(OssProperties.BUCKET_NAME, CannedAccessControlList.PublicRead);
                log.info("3");
            }
        } catch (OSSException e) {
            oss.shutdown();
            log.info("OSSException");
            e.printStackTrace();
        } catch (ClientException e) {
            oss.shutdown();
            log.info("ClientException");
        }

        //构建日期目录xxx/2000/1/1/文件名
        String folder = new DateTime().toString("yyyy/MM/dd");

        //文件名:uuid.扩展名
        fileName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));

        //文件根路径
        String key=module+"/"+folder+"/"+fileName;
        log.info(key);
        log.info("开始上传文件");
        //文件上传至阿里服务器
        try {
            oss.putObject(OssProperties.BUCKET_NAME,key,inputStream);
        } catch (OSSException e) {
            oss.shutdown();
            log.info("OSSException");
            e.printStackTrace();
        } catch (ClientException e) {
            oss.shutdown();
            log.info("ClientException");
        }

        oss.shutdown();

        //阿里云文件绝对路径
        return "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/" + key;

    }

    @Override
    public void removeFile(String url) {
        OSS oss = new OSSClientBuilder().build(
                OssProperties.ENDPOINT,
                OssProperties.KEY_ID,
                OssProperties.KEY_SECRET
        );
        //文件名（服务器上的文件路径）
        String host = "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/";
        String objectName=url.substring(host.length());
        //删除文件
        oss.deleteObject(OssProperties.BUCKET_NAME,objectName);
        //关闭
        oss.shutdown();
    }
}
