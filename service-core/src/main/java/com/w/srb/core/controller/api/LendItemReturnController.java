package com.w.srb.core.controller.api;


import com.w.common.result.R;
import com.w.srb.base.util.JwtUtils;
import com.w.srb.core.pojo.entity.LendItemReturn;
import com.w.srb.core.service.LendItemReturnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 标的出借回款记录表 前端控制器
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Slf4j
@Api(tags = "回款计划")
@RestController
@RequestMapping("/api/core/lendItemReturn")
public class LendItemReturnController {

    @Resource
    private LendItemReturnService lendItemReturnService;

    @GetMapping("/auth/list/{lendId}")
    public R list(
            @ApiParam(value = "标的id",required = true)
            @PathVariable("lendId") Long lendId,

            HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        List<LendItemReturn> list = lendItemReturnService.selectByLendId(lendId,userId);
        return R.ok().data("list",list);
    }
}

