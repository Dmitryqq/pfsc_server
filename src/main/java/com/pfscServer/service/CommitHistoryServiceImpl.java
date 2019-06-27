/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.service;

import com.pfscServer.domain.Commit;
import com.pfscServer.domain.CommitHistory;
import com.pfscServer.domain.ApplicationUser;
import com.pfscServer.domain.Config;
import com.pfscServer.repo.ApplicationUserRepository;
import com.pfscServer.repo.CommitHistoryRepo;
import com.pfscServer.repo.CommitsRepo;
import com.pfscServer.repo.ConfigsRepo;
import com.pfscServer.util.FileUtil;
import java.io.IOException;
import com.pfscServer.domain.Activity;
import com.pfscServer.exception.ServiceException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import org.springframework.http.HttpStatus;

/**
 *
 * @author User
 */
@Service
public class CommitHistoryServiceImpl implements CommitHistoryService{

    @Autowired
    CommitsRepo commitRepo;
    @Autowired
    CommitHistoryRepo historyRepo;
    @Autowired
    ConfigsRepo configRepo;
    @Autowired
    UserDetailsServiceImpl userService;
    @Autowired
    MailSenderServiceImpl mailSenderServiceImpl;
    @Autowired
    ApplicationUserRepository userRepo;


    @Override
    public CommitHistory acceptCommit(Long id) throws IOException,ServiceException, MessagingException{
        Commit commit = commitRepo.findById(id).orElse(null);
        if(commit == null)
            return null;
        ApplicationUser user = userRepo.findById(commit.getUserId()).orElse(null);       
        if(historyRepo.findByCommitIdAndActivity(id,Activity.REJECT.getTitle()).size()>0 || historyRepo.findByCommitIdAndActivity(id,Activity.ACCEPT.getTitle()).size()>0)
            throw new ServiceException("Данное действие заблокировано", HttpStatus.LOCKED);
        mailSenderServiceImpl.send(user,true,commit,"");
        return create(commit, Activity.ACCEPT);
    }

    @Override
    public CommitHistory rejectCommit(Long id, String text)throws IOException, ServiceException, MessagingException{

        Commit commit = commitRepo.findById(id).orElse(null);
        Config rootDir = configRepo.findFirstByName("rootDir");
        if(commit == null || rootDir == null)
            return null;
        ApplicationUser user = userRepo.findById(commit.getUserId()).orElse(null);
        if(historyRepo.findByCommitIdAndActivity(id,Activity.REJECT.getTitle()).size()>0 || historyRepo.findByCommitIdAndActivity(id,Activity.ACCEPT.getTitle()).size()>0)
            throw new ServiceException("Данное действие заблокировано", HttpStatus.LOCKED);
        FileUtil.deleteDir(commit.getDir(rootDir.getValue()));
        mailSenderServiceImpl.send(user,false,commit,text);
        return create(commit,Activity.REJECT);
    }

       
    @Override
    public CommitHistory create(Commit commit, Activity activity){
        CommitHistory history = new CommitHistory();
        history.setCommitId(commit.getId());
        history.setCommit(commit);
        history.setActivity(activity.getTitle());
        ApplicationUser user = userService.getCurrentUser();
        history.setUserId(user.getId());
        history.setCreateDate(LocalDateTime.now());
        return historyRepo.save(history);      
    }
}
