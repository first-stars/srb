package com.w.srb.core.controller.admin;


import com.w.common.result.R;
import com.w.srb.core.pojo.entity.UserLoginRecord;
import com.w.srb.core.service.UserLoginRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Slf4j
@Api(tags = "会员登录日志接口")
//@CrossOrigin //跨域
@RestController
@RequestMapping("/admin/core/userLoginRecord")
public class AdminUserLoginRecordController {

    @Resource
    private UserLoginRecordService userLoginRecordService;

    @ApiOperation("获取会员日志登录列表")
    @GetMapping("/listTop50/{userId}")
    public R listTop50(
            @ApiParam(value = "用户id", required = true)
            @PathVariable("userId") Long userId
    ){
        List<UserLoginRecord> userLoginRecords = userLoginRecordService.listTop50(userId);
        return R.ok().data("list",userLoginRecords);
    }

}

