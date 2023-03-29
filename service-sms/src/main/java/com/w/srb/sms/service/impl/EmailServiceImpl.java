package com.w.srb.sms.service.impl;

import com.w.srb.sms.service.EmailService;
import com.w.srb.sms.util.EmailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author xin
 * @date 2022-10-11-10:16
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Override
    public boolean sendQQEmail(String recevices,String code){


        String from_email= EmailProperties.FROM_EMAIL;
        String pwd=EmailProperties.PWD;
//        log.info(from_email);
//        log.info(pwd);
//        String recevices="609703596@qq.com";
//        String code="123456";
        String name="用户";
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");		//使用smpt的邮件传输协议
        props.setProperty("mail.smtp.host", "smtp.qq.com");		//主机地址
        props.setProperty("mail.smtp.auth", "true");		//授权通过

        Session session = Session.getInstance(props);		//通过我们的这些配置，得到一个会话程序

        try {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from_email));		//设置发件人
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recevices,"用户","utf-8"));		//设置收件人
            message.setSubject("演示发邮件验证码网","utf-8");		//设置主题
            message.setSentDate(new Date());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            String str = "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body><p style='font-size: 20px;font-weight:bold;'>尊敬的："+name+"，您好！</p>"
                    + "<p style='text-indent:2em; font-size: 20px;'>欢迎注册xxx网，您本次的注册码是 "
                    + "<span style='font-size:30px;font-weight:bold;color:red'>" + code + "</span>，3分钟之内有效，请尽快使用！</p>"
                    + "<p style='text-align:right; padding-right: 20px;'"
                    + "<a href='http://www.hyycinfo.com' style='font-size: 18px'>xxxx有限公司</a></p>"
                    + "<span style='font-size: 18px; float:right; margin-right: 60px;'>" + sdf.format(new Date()) + "</span></body></html>";

            Multipart mul=new MimeMultipart();  //新建一个MimeMultipart对象来存放多个BodyPart对象
            BodyPart mdp=new MimeBodyPart();  //新建一个存放信件内容的BodyPart对象
            mdp.setContent(str, "text/html;charset=utf-8");
            mul.addBodyPart(mdp);  //将含有信件内容的BodyPart加入到MimeMultipart对象中
            message.setContent(mul); //把mul作为消息内容


            message.saveChanges();

            //创建一个传输对象
            Transport transport=session.getTransport("smtp");

            //建立与服务器的链接  465端口是 SSL传输
            transport.connect("smtp.qq.com", 587, from_email, pwd);

            //发送邮件
            transport.sendMessage(message,message.getAllRecipients());

            //关闭邮件传输
            transport.close();

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return true;
    }
}
