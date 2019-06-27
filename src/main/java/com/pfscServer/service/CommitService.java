/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.service;

import com.pfscServer.domain.Commit;
import com.pfscServer.domain.CommitDto;
import com.pfscServer.exception.ServiceException;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author User
 */
public interface CommitService {
    
    List<CommitDto> find(String description);
    
    CommitDto update(Long id,Commit t) throws ServiceException;
    
    List<CommitDto> getDtoAll();
    
    CommitDto getDtoById(Long id);
    
    CommitDto create(Commit t) throws IOException;
}
