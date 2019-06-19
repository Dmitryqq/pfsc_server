/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.service;

import java.util.List;
/**
 *
 * @author User
 * @param <T>
 * @param <S>
 */
public interface Service<T,S> {
    
    List<T> getAll();
    
    T getById(S id);
    
    void save(T t);

    void delete(S id);
    
}
