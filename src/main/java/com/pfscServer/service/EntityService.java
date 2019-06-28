/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.service;

import com.pfscServer.exception.ServiceException;
import java.io.IOException;
import java.util.List;
/**
 *
 * @author User
 * @param <T>
 * @param <S>
 */
public interface EntityService<T,S> {
    
    List<T> getAll();
    
    T getById(S id);
    
    T save(T t);

    void delete(S id) throws IOException,ServiceException;
    
}
