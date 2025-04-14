package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class MyProjectBackendApplicationTests {

//    @Test
//    void contextLoads() {
//        System.out.println(new BCryptPasswordEncoder().encode("123456"));
//    }

    @Autowired
    JavaMailSender sender;

    @Test
    void contextLoads() {
        //SimpleMailMessage是一个比较简易的邮件封装，支持设置一些比较简单内容
        SimpleMailMessage message = new SimpleMailMessage();
        //设置邮件标题
        message.setSubject("【电子科技大学教务处】关于近期学校对您的处分决定");
        //设置邮件内容
        message.setText("XXX同学您好，经监控和教务巡查发现，您近期存在旷课、迟到、早退、上课刷抖音行为，" +
                "现已通知相关辅导员，请手写5000字书面检讨，并在2022年4月1日17点前交到辅导员办公室。");
        //设置邮件发送给谁，可以多个，这里就发给你的QQ邮箱
        message.setTo("1179954886@qq.com");
        //邮件发送者，这里要与配置文件中的保持一致
        message.setFrom("yubinhsia@163.com");
        //OK，万事俱备只欠发送
        sender.send(message);
    }
}
