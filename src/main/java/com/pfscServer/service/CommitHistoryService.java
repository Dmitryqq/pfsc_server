/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.service;

import com.pfsc_server.domain.CommitHistory;

/**
 *
 * @author User
 */
public interface CommitHistoryService {
    
    CommitHistory acceptCommit(Long id);
    
    CommitHistory rejectCommit(Long id);
}
