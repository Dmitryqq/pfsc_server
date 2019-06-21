/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.service;

import com.pfsc_server.domain.Commit;
import com.pfsc_server.domain.CommitHistory;
import com.pfsc_server.repo.CommitHistoryRepo;
import com.pfsc_server.repo.CommitsRepo;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    @Override
    public CommitHistory acceptCommit(Long id) {
        Commit commit = commitRepo.findById(id).orElse(null);
        if(commit == null)
            return null;       
        CommitHistory history = new CommitHistory();
        history.setCommitId(id);
        history.setCommit(commit);
        history.setAccepted(true);
        history.setCreateDate(LocalDateTime.now());
        return historyRepo.save(history);       
    }

    @Override
    public CommitHistory rejectCommit(Long id) {
        Commit commit = commitRepo.findById(id).orElse(null);
        if(commit == null)
            return null;       
        CommitHistory history = new CommitHistory();
        history.setCommitId(id);
        history.setCommit(commit);
        history.setAccepted(false);
        history.setCreateDate(LocalDateTime.now());
        return historyRepo.save(history);
    }
    
    
}
