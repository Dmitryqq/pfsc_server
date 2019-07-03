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

import java.util.Map;

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
            fileService.fileValidation(commitId);
            CommitHistory history = historyService.acceptCommit(commitId);
            if (history == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(history, HttpStatus.OK);

        } catch (MessagingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("{id}/reject")
    public ResponseEntity<CommitHistory> rejectCommit(@PathVariable("id") Long commitId, @RequestBody Map<String, String> text) throws ServiceException, IOException {
        String message = text.get("text");
        if (message == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            CommitHistory history = historyService.rejectCommit(commitId, message);
            if (history == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(history, HttpStatus.OK);
        } catch (MessagingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
