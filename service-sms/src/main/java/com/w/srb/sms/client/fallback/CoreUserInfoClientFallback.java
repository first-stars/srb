package com.w.srb.sms.client.fallback;

import com.w.srb.sms.client.CoreUserInfoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author xin
 * @date 2022-10-14-17:31
 */
@Service
@Slf4j
public class CoreUserInfoClientFallback implements CoreUserInfoClient {
    @Override
    public boolean checkMobile(String mobile) {
        log.info("远程调用失败，服务熔断");
        return false;//手机号不重复
    }
}
