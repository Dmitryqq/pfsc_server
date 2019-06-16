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
    private final TypeOfFileRepo typeOfFileRepo;
    
    @Autowired
    public CommitController(CommitsRepo commitRepo, ConfigsRepo configRepo,UsersRepo userRepo,TypeOfFileRepo typeOfFileRepo) {
        this.commitRepo = commitRepo;
        this.configRepo = configRepo;
        this.userRepo = userRepo;
        this.typeOfFileRepo = typeOfFileRepo;
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
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        User user = userRepo.findById(commit.getUser_id()).orElse(null);
        commit.setUser(user);
        commit.setCreate_date(LocalDateTime.now());
        
        int n = commitRepo.CountUserCommits(commit.getCreate_date(), commit.getUser_id());      
        commit.setNumber(n+1);
                     
        if (createDir(commit.getDir(rootDir.getValue()))) {
            commit = commitRepo.save(commit);
            return new ResponseEntity<>(commit,HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("{id}")
    public ResponseEntity<Commit> update(@PathVariable("id") Long commitId, @RequestBody Commit commit){
        Commit dbCommit = commitRepo.findById(commitId).orElse(null);
        if(dbCommit == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        
        if(dbCommit.getAccept_date()!=null || dbCommit.getReject_date()!=null)
            return new ResponseEntity<>(HttpStatus.LOCKED);
        
        dbCommit.setDescription(commit.getDescription());
        dbCommit.setUpdate_date(LocalDateTime.now());
        commitRepo.save(dbCommit);      
        return new ResponseEntity<>(dbCommit,HttpStatus.OK);
    }
    
    @PutMapping("{id}/accept")
    public ResponseEntity<Commit> accept(@PathVariable("id") Long commitId){
        Commit dbCommit = commitRepo.findById(commitId).orElse(null);
        if(dbCommit == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        
        if(dbCommit.getAccept_date()!=null || dbCommit.getReject_date()!=null)
            return new ResponseEntity<>(HttpStatus.LOCKED);
        
        dbCommit.setAccept_date(LocalDateTime.now());
        dbCommit.setUpdate_date(LocalDateTime.now());
        commitRepo.save(dbCommit);      
        return new ResponseEntity<>(dbCommit,HttpStatus.OK);
    }
    
    @PutMapping("{id}/reject")
    public ResponseEntity<Commit> reject(@PathVariable("id") Long commitId){
        Commit dbCommit = commitRepo.findById(commitId).orElse(null);
        if(dbCommit == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        
        if(dbCommit.getAccept_date()!=null || dbCommit.getReject_date()!=null)
            return new ResponseEntity<>(HttpStatus.LOCKED);
        
        Config rootDir = configRepo.findById(1L).orElse(null);
        if(rootDir == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        if(!deleteDir(new File(dbCommit.getDir(rootDir.getValue()))))
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        
        dbCommit.setReject_date(LocalDateTime.now());
        dbCommit.setUpdate_date(LocalDateTime.now());
        commitRepo.save(dbCommit);      
        return new ResponseEntity<>(dbCommit,HttpStatus.OK);
    }
    
    private boolean deleteDir(File f){
        if (f.isDirectory()) {
          for (File c : f.listFiles())
            if(!deleteDir(c))
                return false;
        }
        return f.delete();
    }
    
    private boolean createDir(String dir){
        File file = new File(dir);
        if (file.mkdirs()) {
            for (TypeOfFile tof : typeOfFileRepo.findAll()){
                file = new File(dir + "\\" + tof.getName());
                if(!file.mkdirs())
                    return false;
            }
            return true;
        }
        else 
            return false;
    }
}
