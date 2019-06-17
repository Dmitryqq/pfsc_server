/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    
    @Column(updatable = false, insertable = false, nullable = false)
    private Long user_id;  
    
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    
    @Column(updatable = false, insertable = false, nullable = false)
    private Long mark_id;  
    
    @ManyToOne
    @JoinColumn(name="mark_id")
    private Mark mark;
    
    @Column(updatable = false, nullable = false)
    private int number;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime accept_date;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime reject_date;
    
    @Column(updatable = false, nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime create_date;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime update_date;
    
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

    public LocalDateTime getAccept_date() {
        return accept_date;
    }

    public void setAccept_date(LocalDateTime accept_date) {
        this.accept_date = accept_date;
    }

    public LocalDateTime getReject_date() {
        return reject_date;
    }

    public void setReject_date(LocalDateTime reject_date) {
        this.reject_date = reject_date;
    }

    public LocalDateTime getCreate_date() {
        return create_date;
    }

    public void setCreate_date(LocalDateTime create_date) {
        this.create_date = create_date;
    }

    public LocalDateTime getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(LocalDateTime update_date) {
        this.update_date = update_date;
    }
    
    public Long getUser_id() {
        return user_id;
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public Long getMark_id() {
        return mark_id;
    }
    
    public void setMark_id(Long mark_id) {
        this.mark_id = mark_id;
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }
    
    public String getDir(String rootDir) {
        return rootDir + "\\" + getDateString(create_date) + "\\" + user.getName() + "\\" + number;
    } 
    
    private String getDateString(LocalDateTime locDate) {
        String day = (locDate.getDayOfMonth() < 10 ? "0" : "") + locDate.getDayOfMonth(); 
        String month = (locDate.getMonthValue() < 10 ? "0" : "") + locDate.getMonthValue(); 
        return  day + "." + month + "." + locDate.getYear();
    }
}
