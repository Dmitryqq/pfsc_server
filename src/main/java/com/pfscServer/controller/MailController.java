package com.pfscServer.controller;

import com.pfscServer.domain.File;
import com.pfscServer.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("mail")
public class MailController {

    @Autowired
    MailSender mailSender;


    @GetMapping
    public String list() {
        mailSender.send("atai.seitbekuulu@mail.ru", "Check message", "Успешно!");
        return "Main send, check";
    }
}
