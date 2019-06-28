/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.controller;

import com.pfscServer.domain.*;
import com.pfscServer.exception.ServiceException;
import com.pfscServer.service.CommitServiceImpl;
import com.pfscServer.service.UserDetailsServiceImpl;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
public class CommitController {
    
    @Autowired
    CommitServiceImpl commitService;
    @Autowired
    UserDetailsServiceImpl userService;
      
    @GetMapping
    public  ResponseEntity<List<CommitDto>> list() {
        return new ResponseEntity<>(commitService.getDtoAll(),HttpStatus.OK);
    }
    
    @GetMapping("{id}")
    public ResponseEntity<CommitDto> getCommit(@PathVariable("id") Long commitId){
        CommitDto commit = commitService.getDtoById(commitId);
        if(commit==null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        ApplicationUser user = userService.getCurrentUser();
        if(user.getRole().getRoleName().equals("User") && user.getId() != commit.getUserId())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(commit,HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<CommitDto> create(@RequestBody Commit commit) throws IOException{
        if (commit == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CommitDto commitDto = commitService.create(commit);
        if(commitDto == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(commitDto,HttpStatus.CREATED);         
    }
    
    @PostMapping("search")
    public ResponseEntity<List<CommitDto>> search(@RequestBody Map<String, String> param) {
        String sParam = param.get("param");
        if(sParam == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(commitService.find(sParam),HttpStatus.OK);
    }
    
    @PutMapping("{id}")
    public ResponseEntity<CommitDto> update(@PathVariable("id") Long commitId, @RequestBody Commit commit) throws ServiceException{
        Commit dbCommit = commitService.getById(commitId);
        if(dbCommit == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        ApplicationUser user = userService.getCurrentUser();
        if(user.getId() != dbCommit.getUserId())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(commitService.update(commitId,commit),HttpStatus.OK);
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<CommitDto> delete(@PathVariable("id") Long commitId) throws ServiceException {
        Commit commit = commitService.getById(commitId);
        if(commit == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        ApplicationUser user = userService.getCurrentUser();
        if(user.getId() != commit.getUserId() && !user.getRole().getRoleName().equals("Admin"))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        commitService.delete(commitId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
