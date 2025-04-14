package com.example.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {

    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler
    public void sendMailMessage(Map<String, Object> data){
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");
        SimpleMailMessage message = switch (type) {
            case "register" -> createMessage("欢迎注册大型同性交友网站",
                    "您的验证码是：" + code + "有效时间3分钟，请勿向他人泄露验证码信息", email);
            case "reset" -> createMessage("找回密码验证码",
                    "您正在进行密码重置，验证码：" + code + "有效时间3分钟", email);
            default -> null;
        };
        if (message == null)  return;
        sender.send(message);
    }

    private SimpleMailMessage createMessage(String title, String content, String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(email);
        message.setSubject(title);
        message.setText(content);
        return message;
    }
}
