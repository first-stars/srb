package com.w.srb.sms.controller.api;

import com.w.common.exception.Assert;
import com.w.common.result.R;
import com.w.common.result.ResponseEnum;
import com.w.common.util.RandomUtils;
import com.w.common.util.RegexValidateUtils;
import com.w.srb.sms.service.EmailService;
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
@Api(tags = "邮箱验证码管理")
@Slf4j
//@CrossOrigin //跨域
@RequestMapping("/api/email")
public class ApiEmailController {
    @Resource
    private EmailService emailService;

    @Resource
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "获取邮箱验证码")
    @GetMapping("/send/{email}")
    public R sendSms(
            @ApiParam(value = "邮箱号", required = true)
            @PathVariable("email") String email){

        //EMAIL_NULL_ERROR(-702, "邮箱号码不能为空"),
        Assert.notEmpty(email, ResponseEnum.EMAIL_NULL_ERROR);
        log.info(email);
        //EMAIL_ERROR(-703, "邮箱号码不正确"),
//        Assert.isTrue(RegexValidateUtils.checkEmail(email),ResponseEnum.EMAIL_ERROR);
        //生成验证码
        String code = RandomUtils.getFourBitRandom();
        //发送验证码
//        boolean flag = emailService.sendQQEmail(email, code);

//        if (flag!=true){
//            return R.error().message("短信发送失败");
//        }
        //将验证码存入redis
        redisTemplate.opsForValue().set("srb:sms:code",code,3, TimeUnit.MINUTES);


        return R.ok().message("短信发送成功");
    }

}
