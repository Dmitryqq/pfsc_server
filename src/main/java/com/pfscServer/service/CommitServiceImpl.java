/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.service;

import com.pfscServer.domain.*;
import com.pfscServer.repo.ApplicationUserRepository;
import com.pfscServer.repo.CommitHistoryRepo;
import com.pfscServer.repo.CommitsRepo;
import com.pfscServer.repo.ConfigsRepo;
import com.pfscServer.repo.MarksRepo;
import com.pfscServer.util.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pfscServer.repo.FileTypesRepo;
import com.pfscServer.repo.FilesRepo;
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
    ApplicationUserRepository userRepo;
    @Autowired
    FileTypesRepo fileTypeRepo;
    @Autowired
    MarksRepo markRepo;
    @Autowired
    CommitHistoryRepo historyRepo;
    @Autowired
    FilesRepo fileRepo;
    
    @Override
    public List<Commit> getAll() {
        return commitRepo.findAll();
    }
    
    @Override
    public List<CommitDto> getDtoAll() {
        List<CommitDto> commits = commitRepo.findWithHistory();
        return commits;
    }

    @Override
    public Commit getById(Long id) {
        Commit commit = commitRepo.findById(id).orElse(null);  
        return commit;   
    }
    
    @Override
    public CommitDto getDtoById(Long id) {
        CommitDto commit = commitRepo.findByIdWithHistory(id); 
        if(commit == null)
            return null;
        commit.setFileTypes(fileTypeRepo.findAllDto());
        commit.getFileTypes().forEach((ft) -> {
            ft.setFiles(fileRepo.findByFileTypeId(ft.getId()));
        });
        return commit;   
    }
    
    @Override
    public Commit create(Commit t) throws IOException{
        Config rootDir = configRepo.findFirstByName("rootDir");
        ApplicationUser user = userRepo.findById(t.getUserId()).orElse(null);
        Mark mark = markRepo.findById(t.getMarkId()).orElse(null);
        if(rootDir == null || user == null || mark == null) 
            return null;
        t.setMark(mark);
        t.setUser(user);
        t.setCreateDate(LocalDateTime.now());
        String dateString = DateUtil.getDateString(LocalDateTime.now(), "-");
        LocalDateTime startDate = DateUtil.convertToDate(dateString + " 00:00","dd-MM-yyyy HH:mm");
        LocalDateTime endDate = DateUtil.convertToDate(dateString + " 23:59","dd-MM-yyyy HH:mm");
        int n = commitRepo.CountUserCommits(t.getUserId(), startDate, endDate);      
        t.setNumber(n+1);
        for(FileType tof : fileTypeRepo.findAll())
            FileUtil.createDir(t.getDir(rootDir.getValue())+"\\"+tof.getName());       
        return commitRepo.save(t);
    }

    @Override
    public List<CommitDto> find(String description) {
        LocalDateTime startDate = DateUtil.convertToDate(description+" 00:00","dd-MM-yyyy HH:mm");
        if(startDate != null)
        {
            LocalDateTime endDate = DateUtil.convertToDate(description+" 23:59","dd-MM-yyyy HH:mm");
            return commitRepo.findByDescriptionOrCreateDate(description,startDate,endDate);
        }
        else 
            return commitRepo.findByDescriptionContaining(description);
    }
    
    @Override
    public Commit update(Long id, Commit t) throws Exception{
        Commit commit = commitRepo.findById(id).orElse(null);
        Mark mark = markRepo.findById(t.getMarkId()).orElse(null);
        if( commit == null || mark == null)
            throw new Exception("Bad request");
        if(historyRepo.findByCommitId(id).isEmpty()){
            commit.setDescription(t.getDescription());
            commit.setMark(mark);
            commit.setMarkId(mark.getId());
            commit.setUpdateDate(LocalDateTime.now());
            return commitRepo.save(commit);   
        }
        else
            return null;
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
