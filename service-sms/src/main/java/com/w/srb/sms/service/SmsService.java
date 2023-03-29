package com.w.srb.sms.service;

import java.util.Map;

/**
 * @author xin
 * @date 2022-10-11-9:06
 */
public interface SmsService {
    /**
     * 阿里云短信验证
     * @param mobile
     * @param templateCode
     * @param map
     */
    void send(String mobile, String templateCode, Map<String,Object> map);
}
