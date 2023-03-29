package com.w.srb.sms.controller.api;

import com.w.common.exception.Assert;
import com.w.common.result.R;
import com.w.common.result.ResponseEnum;
import com.w.common.util.RandomUtils;
import com.w.common.util.RegexValidateUtils;
import com.w.srb.sms.client.CoreUserInfoClient;
import com.w.srb.sms.service.SmsService;
import com.w.srb.sms.util.SmsProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author xin
 * @date 2022-10-11-10:22
 */
@RestController
@Api(tags = "短信管理")
@Slf4j
//@CrossOrigin //跨域
@RequestMapping("/api/sms")
public class ApiSmsController {
    @Resource
    private SmsService smsService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CoreUserInfoClient coreUserInfoClient;

    @ApiOperation(value = "获取短信验证码")
    @GetMapping("/send/{mobile}")
    public R sendSms(
            @ApiParam(value = "手机号", required = true)
            @PathVariable("mobile") String mobile){

        //MOBILE_NULL_ERROR(-202, "手机号码不能为空"),
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        //MOBILE_ERROR(-203, "手机号码不正确"),
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile),ResponseEnum.MOBILE_ERROR);
        //手机号是否注册

        boolean b = coreUserInfoClient.checkMobile(mobile);
        System.out.println(b);
        Assert.isTrue(b== false,ResponseEnum.MOBILE_EXIST_ERROR);

        //生成验证码
        String code = RandomUtils.getFourBitRandom();
        log.info(code);
        //组建短信模板
        HashMap<String, Object> map = new HashMap<>();
        map.put("code",code);
        //发送短信
//        smsService.send(mobile, SmsProperties.TEMPLATE_CODE,map);
        //将验证码存入redis
        redisTemplate.opsForValue().set("srb:sms:code:"+mobile,code,3, TimeUnit.MINUTES);


        return R.ok().message("短信发送成功");
    }

}
