/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.controller;

import com.pfscServer.domain.Commit;
import com.pfscServer.domain.CommitHistory;
import com.pfscServer.service.CommitHistoryServiceImpl;

import java.io.IOException;

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

    @GetMapping("{id}/accept")
    public ResponseEntity<CommitHistory> acceptCommit(@PathVariable("id") Long commitId) {
        try {
            CommitHistory history = historyService.acceptCommit(commitId);
            if (history == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(history, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
    }

    @GetMapping("{id}/reject")
    public ResponseEntity<CommitHistory> rejectCommit(@PathVariable("id") Long commitId, @RequestBody String text) {
        try {
            CommitHistory history = historyService.rejectCommit(commitId, text);
            if (history == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(history, HttpStatus.OK);
        } catch (IOException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessagingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception ex){
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
    }
}
