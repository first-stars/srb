package com.w.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.w.common.result.R;
import com.w.srb.base.util.JwtUtils;
import com.w.srb.core.huf.RequestHelper;
import com.w.srb.core.pojo.vo.UserBindVO;
import com.w.srb.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Slf4j
@Api(tags = "会员账号绑定")
@RestController
@RequestMapping("/api/core/userBind")
public class UserBindController {

    @Resource
    private UserBindService userBindService;

    @ApiOperation("账户绑定提交数据")
    @PostMapping("/auth/bind")
    public R bind(@RequestBody UserBindVO userBindVO, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        String formStr = userBindService.commitBindUser(userBindVO, userId);

        return R.ok().data("formStr",formStr);
    }

    @ApiOperation("账户绑定异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request){

        //汇付宝向尚融宝发起回调函数
        Map<String, Object> map = RequestHelper.switchMap(request.getParameterMap());
        log.info("账户绑定回调函数参数: "+ JSON.toJSONString(map));

        //校验签名
        if (!RequestHelper.isSignEquals(map)){
            log.error("用户绑定异步回调签名验证错误: "+JSON.toJSONString(map));
            return "fail";
        }
        log.info("验签成功，开始绑定");

        //修改绑定装态
        userBindService.notify(map);
        return "success";
    }
}

