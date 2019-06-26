/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.domain;

/**
 *
 * @author User
 */
public enum Activity {
    ACCEPT("принят"),
    REJECT("отклонен"),
    ADDFILE("добавлен файл");
        
    private String title;

    Activity(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }    
}
