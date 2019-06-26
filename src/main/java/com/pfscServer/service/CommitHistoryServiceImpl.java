/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.service;

import com.pfscServer.domain.ApplicationUser;
import com.pfscServer.domain.Commit;
import com.pfscServer.domain.CommitHistory;
import com.pfscServer.domain.Config;
import com.pfscServer.repo.ApplicationUserRepository;
import com.pfscServer.repo.CommitHistoryRepo;
import com.pfscServer.repo.CommitsRepo;
import com.pfscServer.repo.ConfigsRepo;
import com.pfscServer.util.FileUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

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
    MailSenderServiceImpl mailSenderServiceImpl;
    @Autowired
    ApplicationUserRepository userRepo;



    @Override
    public CommitHistory acceptCommit(Long id) throws Exception{
        Commit commit = commitRepo.findById(id).orElse(null);
        ApplicationUser user = userRepo.findById(commit.getUserId()).orElse(null);
        if(commit == null)
            return null;       
        if(historyRepo.findByCommitId(id).size()>0)
            throw new Exception("Locked");
        CommitHistory history = new CommitHistory();
        history.setCommitId(id);
        history.setCommit(commit);
        history.setAccepted(true);
        history.setCreateDate(LocalDateTime.now());
        mailSenderServiceImpl.send(user,true,commit,"");
        return historyRepo.save(history);
    }

    @Override
    public CommitHistory rejectCommit(Long id, String text) throws IOException, MessagingException {
        Commit commit = commitRepo.findById(id).orElse(null);
        Config rootDir = configRepo.findFirstByName("rootDir");
        ApplicationUser user = userRepo.findById(commit.getUserId()).orElse(null);
        if(commit == null || rootDir == null)
            return null;       
        CommitHistory history = new CommitHistory();
        history.setCommitId(id);
        history.setCommit(commit);
        history.setAccepted(false);
        history.setCreateDate(LocalDateTime.now());
        FileUtil.deleteDir(history.getCommit().getDir(rootDir.getValue()));
        mailSenderServiceImpl.send(user,false,commit,text);
        return historyRepo.save(history);
    }
       
}
