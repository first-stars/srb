package com.w.srb.core.controller.api;


import com.w.common.result.R;
import com.w.srb.base.util.JwtUtils;
import com.w.srb.core.pojo.vo.BorrowerVO;
import com.w.srb.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Slf4j
@Api(tags = "借款人")
@RestController
@RequestMapping("/api/core/borrower")
public class BorrowerController {

    @Resource
    private BorrowerService borrowerService;

    @ApiOperation("保存借款人信息")
    @PostMapping("/auth/save")
    public R save(@RequestBody BorrowerVO borrowerVO, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        borrowerService.saveBorrowerVOByUserId(borrowerVO,userId);
        return R.ok().message("信息提交成功");
    }

    @ApiOperation("获取借款人认证装态")
    @GetMapping("/auth/getBorrowerStatus")
    public R getBorrowerStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        Integer status = borrowerService.getBorrowerStatus(userId);
        return R.ok().data("borrowerStatus",status);
    }
}

