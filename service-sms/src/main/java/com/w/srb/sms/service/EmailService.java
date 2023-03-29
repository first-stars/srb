package com.w.srb.sms.service;

/**
 * @author xin
 * @date 2022-10-11-10:15
 */
public interface EmailService {
    /**
     * QQ邮箱验证
     */
    boolean sendQQEmail(String recevices,String code);
}
