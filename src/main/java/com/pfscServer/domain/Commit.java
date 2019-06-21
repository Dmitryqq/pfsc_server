/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pfscServer.util.DateUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "commits")
@ToString()
@EqualsAndHashCode(of = {"id"})
public class Commit implements Serializable {

    // ----------Columns-------------
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false)
    @Column(nullable = false)
    private String description;

    @Column(updatable = false, insertable = false, nullable = false, name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(updatable = false, insertable = false, nullable = false, name = "mark_id")
    private Long markId;

    @ManyToOne
    @JoinColumn(name = "mark_id")
    private Mark mark;

    @Column(updatable = false, nullable = false)
    private int number;

    @Column(updatable = false, nullable = false, name = "accept_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime acceptDate;

    @Column(updatable = false, nullable = false, name = "reject_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime rejectDate;

    @Column(updatable = false, nullable = false, name = "create_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createDate;

    @Column(updatable = false, nullable = false, name = "update_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updateDate;

    // -------Getters and Setters-------

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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public LocalDateTime getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(LocalDateTime acceptDate) {
        this.acceptDate = acceptDate;
    }

    public LocalDateTime getRejectDate() {
        return rejectDate;
    }

    public void setRejectDate(LocalDateTime rejectDate) {
        this.rejectDate = rejectDate;
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

    public Long getUserId() {
        return userId;
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

    public String getDir(String rootDir) {
        return rootDir + "\\" + DateUtil.getDateString(createDate, ".") + "\\" + user.getName() + "\\" + number;
    }

}
