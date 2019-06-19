/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.controller;

import com.pfsc_server.domain.*;
import com.pfsc_server.service.CommitServiceImpl;
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
      
    @GetMapping
    public  ResponseEntity<List<Commit>> list() {
        return new ResponseEntity<>(commitService.getAll(),HttpStatus.OK);
    }
    
    @GetMapping("{id}")
    public ResponseEntity<Commit> getCommit(@PathVariable("id") Long commitId){
        Commit commit = commitService.getById(commitId);
        if(commit==null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(commit,HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Commit> create(@RequestBody Commit commit) {
        if (commit == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try{
            commit = commitService.create(commit);
            if(commit == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<>(commit,HttpStatus.CREATED);
        }
        catch(IOException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }          
    }
    
    @PostMapping("search")
    public ResponseEntity<List<Commit>> search(@RequestBody Map<String, String> param) {
        String sParam = param.get("param");
        if(sParam == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(commitService.find(sParam),HttpStatus.OK);
    }
    
    @PutMapping("{id}")
    public ResponseEntity<Commit> update(@PathVariable("id") Long commitId, @RequestBody Commit commit){
        Commit dbCommit = commitService.getById(commitId);
        if(dbCommit == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(commitService.update(commitId, commit),HttpStatus.OK);
    }
    
}
