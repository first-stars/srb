package com.w.srb.rabbitutil.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;

/**
 * @author xin
 * @date 2022-10-27-16:42
 */
public class MQConfig {

    @Bean
    public MessageConverter messageConverter(){
        //json字符串转换器
        return new Jackson2JsonMessageConverter();
    }
}
