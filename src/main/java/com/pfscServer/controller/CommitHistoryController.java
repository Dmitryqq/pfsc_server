/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.controller;

import com.pfscServer.domain.CommitHistory;
import com.pfscServer.domain.Config;
import com.pfscServer.exception.ServiceException;
import com.pfscServer.repo.ConfigsRepo;
import com.pfscServer.service.CommitHistoryServiceImpl;

import java.io.IOException;

import com.pfscServer.service.FileServiceImpl;
import com.pfscServer.service.MailSenderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

/**
 * @author User
 */
@RestController
@RequestMapping("commit")
public class CommitHistoryController {

    @Autowired
    CommitHistoryServiceImpl historyService;
    @Autowired
    MailSenderServiceImpl mailSenderServiceImpl;
    @Autowired
    FileServiceImpl fileService;
    @Autowired
    ConfigsRepo configRepo;

    @GetMapping("{id}/accept")
    public ResponseEntity<CommitHistory> acceptCommit(@PathVariable("id") Long commitId) throws ServiceException, IOException {
        try {
            String message = "";
            Config fileRequired = configRepo.findFirstByName("fileRequired");
            if (fileRequired.getValue().equals("true")) {
                message = fileService.comparison(commitId);
                if (message != null) {
                    throw new ServiceException("Файл " + message + " повторяется ", HttpStatus.BAD_REQUEST);
                }
            }
            message = fileService.fileRequired(commitId);
            System.out.println(message);
            if (message == null) {
                CommitHistory history = historyService.acceptCommit(commitId);
                if (history == null)
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(history, HttpStatus.OK);
            } else {
                throw new ServiceException("Нет файлов в папке " + message , HttpStatus.BAD_REQUEST);// мб стоит вывести более информативно
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}/reject")
    public ResponseEntity<CommitHistory> rejectCommit(@PathVariable("id") Long commitId, @RequestBody String text) throws ServiceException, IOException {
        try {
            CommitHistory history = historyService.rejectCommit(commitId, text);
            if (history == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(history, HttpStatus.OK);
        } catch (MessagingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
