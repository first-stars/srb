package com.w.srb.sms.receiver;

import com.w.srb.base.dto.SmsDTO;
import com.w.srb.rabbitutil.constant.MQConst;
import com.w.srb.sms.service.SmsService;
import com.w.srb.sms.util.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xin
 * @date 2022-10-27-17:32
 */
@Component
@Slf4j
public class SmsReceiver {

    @Resource
    private SmsService smsService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_SMS_ITEM, durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_TOPIC_SMS),
            key = {MQConst.ROUTING_SMS_ITEM}
    ))
    public void send(SmsDTO smsDTO) throws IOException {
        log.info("SmsReceiver 消息监听");
        Map<String,Object> param = new HashMap<>();
        param.put("code", smsDTO.getMessage());
//        smsService.send(smsDTO.getMobile(), SmsProperties.TEMPLATE_CODE, param);
        log.info(param.toString());
    }
}
