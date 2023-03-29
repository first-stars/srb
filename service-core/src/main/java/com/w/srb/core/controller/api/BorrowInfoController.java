package com.w.srb.core.controller.api;


import com.w.common.result.R;
import com.w.srb.base.util.JwtUtils;
import com.w.srb.core.pojo.entity.BorrowInfo;
import com.w.srb.core.service.BorrowInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Api(tags = "借款信息")
@Slf4j
@RestController
@RequestMapping("/api/core/borrowInfo")
public class BorrowInfoController {

    @Resource
    private BorrowInfoService borrowInfoService;

    @ApiOperation("获取借款人额度")
    @GetMapping("/auth/getBorrowAmount")
    public R getBorrowAmount(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        BigDecimal borrowAmount = borrowInfoService.getBorrowAmount(userId);
        return R.ok().data("borrowAmount",borrowAmount);
    }

    @ApiOperation("提交借款申请")
    @PostMapping("/auth/save")
    public R save(@RequestBody BorrowInfo borrowInfo,HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        borrowInfoService.saveBorrowInfo(borrowInfo,userId);
        return R.ok().message("提交成功");
    }

    @ApiOperation("获取借款申请审批状态")
    @GetMapping("/auth/getBorrowInfoStatus")
    public R getBorrowInfoStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        Integer status = borrowInfoService.getBorrowInfoStatus(userId);
        return R.ok().data("status",status);
    }
}

