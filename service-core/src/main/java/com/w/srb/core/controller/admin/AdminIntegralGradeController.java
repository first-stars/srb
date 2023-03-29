package com.w.srb.core.controller.admin;


import com.w.common.exception.Assert;
import com.w.common.exception.BusinessException;
import com.w.common.result.R;
import com.w.common.result.ResponseEnum;
import com.w.srb.core.pojo.entity.IntegralGrade;
import com.w.srb.core.service.IntegralGradeService;
import com.w.srb.core.service.impl.IntegralGradeServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Api(tags = "积分等级管理")
//@CrossOrigin    //跨域
@RestController
@RequestMapping("/admin/core/integralGrade")
@Slf4j
public class AdminIntegralGradeController {

    @Resource
    private IntegralGradeService integralGradeService;

    @ApiOperation("积分等级列表")
    @GetMapping("/list")
    public R listAll(){

        log.info("hi is this log info");
        log.warn("hi is this log warn");
        log.error("hi is this log error");

        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list",list).message("获取列表成功");
    }

    @ApiOperation("根据id删除积分等级")
    @DeleteMapping("/remove/{id}")
    public R removeById(
            @ApiParam(value = "数据id" , required = true ,example = "1")
            @PathVariable("id") Long id){
        boolean result = integralGradeService.removeById(id);
        if (result){
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }
    }

    @ApiOperation("新增积分等级")
    @PostMapping("/save")
    public R save(
            @ApiParam(value = "积分等级对象",required = true)
            @RequestBody IntegralGrade integralGrade){

        //如果借款额度为空就手动抛出一个自定义的异常！
//        if(integralGrade.getBorrowAmount() == null){
//            //BORROW_AMOUNT_NULL_ERROR(-201, "借款额度不能为空"),
//            throw new BusinessException(ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
//        }
        //采用断言的方式解决异常
        Assert.notNull(integralGrade.getBorrowAmount(),ResponseEnum.BORROW_AMOUNT_NULL_ERROR);


        boolean result = integralGradeService.save(integralGrade);
        if (result){
            return R.ok().message("保存成功");
        }else {
            return R.error().message("保存失败");
        }
    }

    @ApiOperation("根据id查询")
    @GetMapping("/get/{id}")
    public R getById(
            @ApiParam(value = "数据id",required = true,example = "1")
            @PathVariable("id") Long id){
        IntegralGrade integralGrade = integralGradeService.getById(id);
        if (integralGrade!=null) {
            return R.ok().data("record",integralGrade);
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("更新积分等级")
    @PutMapping("/update")
    public R updateById(
            @ApiParam(value = "积分等级对象",required = true)
            @RequestBody IntegralGrade integralGrade){
        boolean result = integralGradeService.updateById(integralGrade);
        if (result){
            return R.ok().message("更新成功");
        }else {
            return R.error().message("更新失败");
        }
    }



}

