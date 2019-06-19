/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.service;

import com.pfsc_server.domain.*;
import com.pfsc_server.repo.CommitsRepo;
import com.pfsc_server.repo.ConfigsRepo;
import com.pfsc_server.repo.MarksRepo;
import com.pfsc_server.repo.TypeOfFileRepo;
import com.pfsc_server.repo.UsersRepo;
import com.pfsc_server.util.DateUtil;
import com.pfsc_server.util.FileUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author User
 */
@Service
public class CommitServiceImpl implements EntityService<Commit,Long>, CommitService {

    @Autowired
    CommitsRepo commitRepo;
    @Autowired
    ConfigsRepo configRepo;
    @Autowired
    UsersRepo userRepo;
    @Autowired
    TypeOfFileRepo typeOfFileRepo;
    @Autowired
    MarksRepo markRepo;
    
    @Override
    public List<Commit> getAll() {
        return commitRepo.findAll();
    }

    @Override
    public Commit getById(Long id) {
        return commitRepo.findById(id).orElse(null);  
    }
    
    public Commit create(Commit t) throws IOException{
        Config rootDir = configRepo.findById(1L).orElse(null);
        User user = userRepo.findById(t.getUser_id()).orElse(null);
        Mark mark = markRepo.findById(t.getMark_id()).orElse(null);
        if(rootDir == null || user == null || mark == null) 
            return null;
        t.setMark(mark);
        t.setUser(user);
        t.setCreate_date(LocalDateTime.now());
        int n = commitRepo.CountUserCommits(t.getCreate_date(), t.getUser_id());      
        t.setNumber(n+1);
        for(TypeOfFile tof : typeOfFileRepo.findAll())
            FileUtil.createDir(t.getDir(rootDir.getValue())+"\\"+tof.getName());       
        return commitRepo.save(t);
    }

    @Override
    public List<Commit> find(String description) {
        LocalDateTime date = DateUtil.convertToDate(description,"dd-MM-yyyy");
        if(date != null)
            return commitRepo.findByDescriptionOrCreateDate(description,date);
        else 
            return commitRepo.findByDescriptionContainingIgnoreCase(description);
    }
    
    @Override
    public Commit update(Long id, Commit t) {
        Commit commit = commitRepo.findById(id).orElse(null);
        Mark mark = markRepo.findById(t.getMark_id()).orElse(null);
        if( commit == null || mark == null)
            return null;
        commit.setDescription(t.getDescription());
        commit.setMark(mark);
        commit.setMark_id(mark.getId());
        commit.setUpdate_date(LocalDateTime.now());
        return commitRepo.save(commit);    
    }

    @Override
    public Commit save(Commit t) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
}
