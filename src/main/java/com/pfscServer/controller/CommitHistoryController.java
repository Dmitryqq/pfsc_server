/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.controller;

import com.pfsc_server.domain.CommitHistory;
import com.pfsc_server.service.CommitHistoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author User
 */
@RestController
@RequestMapping("commit")
public class CommitHistoryController {
    
    @Autowired
    CommitHistoryServiceImpl historyService;
    
    @GetMapping("{id}/accept")
    public ResponseEntity<CommitHistory> acceptCommit(@PathVariable("id") Long commitId){
        CommitHistory history = historyService.acceptCommit(commitId);
        if(history == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(history,HttpStatus.OK);
    }
    
    @GetMapping("{id}/reject")
    public ResponseEntity<CommitHistory> rejectCommit(@PathVariable("id") Long commitId){
        CommitHistory history = historyService.rejectCommit(commitId);
        if(history == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(history,HttpStatus.OK);
    }
}
