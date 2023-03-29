package com.w.srb.core.controller.api;


import com.w.common.result.R;
import com.w.srb.base.util.JwtUtils;
import com.w.srb.core.pojo.bo.TransFlowBO;
import com.w.srb.core.pojo.entity.TransFlow;
import com.w.srb.core.service.TransFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 交易流水表 前端控制器
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Slf4j
@Api(tags = "资金记录")
@RestController
@RequestMapping("/api/core/transFlow")
public class TransFlowController {

    @Resource
    private TransFlowService transFlowService;

    @ApiOperation("获取列表")
    @GetMapping("/list")
    public R list(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        List<TransFlow> list = transFlowService.selectByUserId(userId);
        return R.ok().data("list",list);

    }
}

