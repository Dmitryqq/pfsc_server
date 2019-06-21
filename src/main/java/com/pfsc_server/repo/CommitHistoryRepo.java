/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.repo;

import com.pfsc_server.domain.CommitHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitHistoryRepo extends JpaRepository<CommitHistory, Long>{ 
 
    List<CommitHistory> findByCommitId(Long commitId);
    
}
