/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.service;

import com.pfscServer.domain.Commit;
import java.util.List;

/**
 *
 * @author User
 */
public interface CommitService {
    
    List<Commit> find(String description);
    
    Commit update(Long id,Commit t);
}
