package com.pfsc_server.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "files")
@ToString()
@EqualsAndHashCode(of = {"id"})
public class File implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;



    @Column(updatable = false, insertable = false, nullable = false)
    private Long commit_id;


    @ManyToOne
    @JoinColumn(name = "commit_id")
    private Commit commit;




    @Column(updatable = false, insertable = false, nullable = false)
    private Long file_id;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private TypeOfFile file;

    @Column(updatable = false, nullable = false)
    private String path;

    //Getter and Setters
    public Long getFile_id() {
        return file_id;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public Long getcommit_id() {
        return commit_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCommit_id(Long commit_id) {
        this.commit_id = commit_id;
    }


    public void setFile_id(Long file_id) {
        this.file_id = file_id;
    }

     public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public TypeOfFile getFile() {
        return file;
    }

    public void setFile(TypeOfFile fileId) {
        this.file = fileId;
    }

}