/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.repo;

import com.pfscServer.domain.CommitHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitHistoryRepo extends JpaRepository<CommitHistory, Long>{ 
 
    List<CommitHistory> findByCommitId(Long commitId);
    
    List<CommitHistory> findByCommitIdAndActivity(Long commitId, String activity);
}
