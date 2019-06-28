/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.service;

import com.pfscServer.domain.*;
import com.pfscServer.exception.ServiceException;
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
import org.springframework.http.HttpStatus;
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
    UserDetailsServiceImpl userService;
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
        List<CommitDto> commits;
        ApplicationUser user = userService.getCurrentUser();
        if(user.getRole().getRoleName().equals("User"))
            commits = commitRepo.findByUserIdDto(user.getId());
        else
            commits = commitRepo.findAllDto();
        return commits;
    }

    @Override
    public Commit getById(Long id) {
        Commit commit = commitRepo.findById(id).orElse(null);  
        return commit;   
    }
    
    @Override
    public CommitDto getDtoById(Long id) {
        CommitDto commit = commitRepo.findByIdDto(id); 
        if(commit == null)
            return null;
        commit.setFileTypes(fileTypeRepo.findAllDto());
        commit.getFileTypes().forEach((ft) -> {
            ft.setFiles(fileRepo.findByFileTypeIdAndCommitId(ft.getId(),id));
        });
        return commit;   
    }
    
    @Override
    public CommitDto create(Commit t) throws IOException{
        Config rootDir = configRepo.findFirstByName("rootDir");
        ApplicationUser user = userService.getCurrentUser();
        Mark mark = markRepo.findById(t.getMarkId()).orElse(null);
        if(rootDir == null || user == null || mark == null) 
            return null;
        t.setMark(mark);
        t.setUserId(user.getId());
        t.setUser(user);
        t.setCreateDate(LocalDateTime.now());
        String dateString = DateUtil.getDateString(LocalDateTime.now(), "-");
        LocalDateTime startDate = DateUtil.convertToDate(dateString + " 00:00","dd-MM-yyyy HH:mm");
        LocalDateTime endDate = DateUtil.convertToDate(dateString + " 23:59","dd-MM-yyyy HH:mm");
        int n = commitRepo.CountUserCommits(t.getUserId(), startDate, endDate);      
        t.setNumber(n+1);
        for(FileType tof : fileTypeRepo.findAll())
            FileUtil.createDir(t.getDir(rootDir.getValue())+"\\"+tof.getName());       
        t = commitRepo.save(t);
        return new CommitDto(t,null);
    }

    @Override
    public List<CommitDto> find(String description) {
        ApplicationUser user = userService.getCurrentUser();
        List<CommitDto> commits;
        LocalDateTime startDate = DateUtil.convertToDate(description+" 00:00","dd-MM-yyyy HH:mm");
        if(startDate != null)
        {
            LocalDateTime endDate = DateUtil.convertToDate(description+" 23:59","dd-MM-yyyy HH:mm");
            if(user.getRole().getRoleName().equals("User"))
                commits = commitRepo.findByUserIdAndDescriptionOrCreateDate(description,startDate,endDate,user.getId());
            else
                commits = commitRepo.findByDescriptionOrCreateDate(description,startDate,endDate);
        }
        else 
            if(user.getRole().getRoleName().equals("User"))
                commits = commitRepo.findByUserIdAndDescriptionContaining(description,user.getId());
            else
                commits = commitRepo.findByDescriptionContaining(description);
        return commits;
    }
    
    @Override
    public CommitDto update(Long id,Commit t) throws ServiceException{  
        Mark mark = markRepo.findById(t.getMarkId()).orElse(null);    
        if(mark == null)
            throw new ServiceException("Отсутствует метка с id "+t.getMarkId(), HttpStatus.BAD_REQUEST);
        if(historyRepo.findByCommitIdAndActivity(id,Activity.REJECT.getTitle()).size()>0 || historyRepo.findByCommitIdAndActivity(id,Activity.ACCEPT.getTitle()).size()>0)
            throw new ServiceException("Данное действие заблокировано", HttpStatus.LOCKED);
        Commit commit = commitRepo.getOne(id);
        commit.setDescription(t.getDescription());
        commit.setMark(mark);
        commit.setMarkId(mark.getId());
        commit.setUpdateDate(LocalDateTime.now());
        commit = commitRepo.save(commit);
        return new CommitDto(commit,null);
    }

    @Override
    public Commit save(Commit t) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    @Override
    public void delete(Long id) throws ServiceException{
        if(fileRepo.findByCommitId(id).isEmpty())
            commitRepo.deleteById(id);
        else
            throw new ServiceException("Накат не может быть удален, так как содержит файлы", HttpStatus.BAD_REQUEST);
    }
    
}
