package com.w.srb.base.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * OpenFeign日志
 * @author xin
 * @date 2022-10-14-16:13
 */
@Configuration
public class OpenFeignConfig {

    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}