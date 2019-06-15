/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.controller;

import com.pfsc_server.repo.*;
import com.pfsc_server.domain.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
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
    private final CommitsRepo commitRepo;
    private final ConfigsRepo configRepo;
    private final UsersRepo userRepo;
    
    @Autowired
    public CommitController(CommitsRepo commitRepo, ConfigsRepo configRepo,UsersRepo userRepo) {
        this.commitRepo = commitRepo;
        this.configRepo = configRepo;
        this.userRepo = userRepo;
    }
    
    String getDateString(LocalDateTime locDate) {
        String day = (locDate.getDayOfMonth() < 10 ? "0" : "") + locDate.getDayOfMonth(); 
        String month = (locDate.getMonthValue() < 10 ? "0" : "") + locDate.getMonthValue(); 
        return  day + "." + month + "." + locDate.getYear();
    }   
    
    @GetMapping
    public  ResponseEntity<List<Commit>> list() {
        List<Commit> commits = commitRepo.findAll();
        if (commits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(commits, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Commit> create(@RequestBody Commit commit) {
        if (commit == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Config rootDir = configRepo.findById(1L).orElse(null);
        if(rootDir == null) {
            return null;
        }
        
        User user = userRepo.findById(commit.getUser_id()).orElse(null);
        commit.setCreate_date(LocalDateTime.now());
        
        int n = commitRepo.CountUserCommits(commit.getCreate_date(), commit.getUser_id());      
        commit.setNumber(n+1);
        
        File folders = new File(rootDir.getValue()+"\\"+ getDateString(commit.getCreate_date())+"\\"+ user.getName() + "\\" + commit.getNumber());       
        if (folders.mkdirs()) {
            commitRepo.save(commit);
            return new ResponseEntity<>(commit,HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
