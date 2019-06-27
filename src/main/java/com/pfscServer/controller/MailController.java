package com.pfscServer.controller;

import com.pfscServer.service.MailSenderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("mail")
public class MailController {

    @Autowired
    MailSenderServiceImpl mailSenderServiceImpl;


    @GetMapping
    public String list() throws IOException, MessagingException {
        Long id = 87l;
        mailSenderServiceImpl.sendOp("nastyhero20@gmail.com","Тест", "Тест Успешен" );
        return "Main send, check";
    }
}
