package com.w.srb.oss.controller.api;

import com.w.common.exception.BusinessException;
import com.w.common.result.R;
import com.w.common.result.ResponseEnum;
import com.w.srb.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author xin
 * @date 2022-10-11-16:33
 */
@Api(tags = "阿里云文件管理")
@RestController
@RequestMapping("/api/oss/file")
//@CrossOrigin    //跨域
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public R upload(
            @ApiParam(value = "文件", required = true)
            @RequestParam("file") MultipartFile file,

            @ApiParam(value = "模块", required = true)
            @RequestParam("module") String module){

        try {
            InputStream inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();
            log.info(filename);
            String uploadUrl = fileService.upload(inputStream, module, filename);
            return R.ok().message("上传成功").data("url",uploadUrl);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }

    }

    @ApiOperation("文件删除")
    @DeleteMapping("/remove")
    public R removeFile(
            @ApiParam(value = "要删除的文件",required = true)
            @RequestParam("url") String url){
        fileService.removeFile(url);
        return R.ok().message("删除成功");
    }
}
