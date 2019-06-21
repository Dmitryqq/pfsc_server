/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.service;

import com.pfsc_server.domain.Commit;
import com.pfsc_server.domain.CommitDto;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author User
 */
public interface CommitService {
    
    List<CommitDto> find(String description);
    
    Commit update(Long id,Commit t) throws Exception;
    
    List<CommitDto> getDtoAll();
    
    CommitDto getDtoById(Long id);
    
    Commit create(Commit t) throws IOException;
}
