/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author User
 */
public class CommitDto implements Serializable{
  
    private Long id;
    private String description;   
    private Long userId;  
    private User user;
    private Long markId;  
    private Mark mark;
    private int number;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updateDate;
    private Boolean accepted;
    
    public CommitDto(Commit commit, Boolean accepted) {
        this.id = commit.getId();
        this.description = commit.getDescription();
        this.userId = commit.getUserId();
        this.user = commit.getUser();
        this.markId = commit.getMarkId();
        this.mark = commit.getMark();
        this.number = commit.getNumber();
        this.createDate = commit.getCreateDate();
        this.updateDate = commit.getUpdateDate();
        this.accepted = accepted;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getMarkId() {
        return markId;
    }

    public void setMarkId(Long markId) {
        this.markId = markId;
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean status) {
        this.accepted = status;
    }
}
