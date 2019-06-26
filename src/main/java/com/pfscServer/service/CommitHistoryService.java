/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.service;

import com.pfscServer.domain.Activity;
import com.pfscServer.domain.Commit;
import com.pfscServer.domain.CommitHistory;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 *
 * @author User
 */
public interface CommitHistoryService {
    
    CommitHistory acceptCommit(Long id) throws Exception;
    

    CommitHistory rejectCommit(Long id) throws IOException,Exception;
    
    CommitHistory create(Commit commit, Activity activity ,String text) throws IOException, MessagingException;

}
