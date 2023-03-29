package com.w.srb.core.controller.admin;


import com.alibaba.excel.EasyExcel;
import com.w.common.exception.BusinessException;
import com.w.common.result.R;
import com.w.common.result.ResponseEnum;
import com.w.srb.core.pojo.dto.ExcelDictDTO;
import com.w.srb.core.pojo.entity.Dict;
import com.w.srb.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Api(tags = "数据字典管理")
@RestController
@RequestMapping("/admin/core/dict")
@Slf4j
//@CrossOrigin
public class AdminDictController {

    @Resource
    DictService dictService;

    @ApiOperation("Excel数据的批量导入")
    @PostMapping("/import")
    public R batchImport(
            @ApiParam(value = "Excel数据字典文件",required = true)
            @RequestParam("file") MultipartFile file){
        try {
            InputStream stream = file.getInputStream();
            dictService.importData(stream);
            return R.ok().message("数组字典数据批量导入成功");
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
    }

    @ApiOperation("Excel数据的导出")
    @GetMapping("/export")
    public R export(HttpServletResponse response) throws IOException{
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("mydict", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.listDictData());
        return R.ok().message("数组字典数据批量导出成功");
    }

    @ApiOperation("根据上级id获取子节点数据列表")
    @GetMapping("/listByParentId/{parentId}")
    public R listByParentId(
            @ApiParam(value = "上节点id",required = true)
            @PathVariable("parentId") Long parentId){
        List<Dict> dictyList = dictService.listByParentId(parentId);
        return R.ok().data("list",dictyList);
    }
}

