package com.w.srb.sms.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xin
 * @date 2022-10-11-8:27
 */

@Setter
@Getter //idea2020.2.3版配置文件自动提示需要这个
@Component
//注意prefix要写到最后一个 "." 符号之前
//调用setter为成员赋值
@ConfigurationProperties(prefix = "qq.email")
public class EmailProperties implements InitializingBean {

    private String fromEmail;
    private String pwd;


    public static String FROM_EMAIL;
    public static String PWD;


    //当私有成员被赋值后，此方法自动被调用，从而初始化常量
    @Override
    public void afterPropertiesSet() throws Exception {
        FROM_EMAIL = fromEmail;
        PWD = pwd;
    }
}
