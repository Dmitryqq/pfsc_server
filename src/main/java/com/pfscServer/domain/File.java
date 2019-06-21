package com.pfscServer.domain;

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


    @Column(updatable = false, insertable = false, nullable = false, name = "commit_id")
    private Long commitId;


    @ManyToOne
    @JoinColumn(name = "commit_id")
    private Commit commit;


    @Column(updatable = false, insertable = false, nullable = false, name = "file_id")
    private Long fileId;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private TypeOfFile file;

    @Column(updatable = false, nullable = false)
    private String path;

    //Getter and Setters
    public Long getFileId() {
        return fileId;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public Long getcommit_id() {
        return commitId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCommitId(Long commitId) {
        this.commitId = commitId;
    }


    public void setFileId(Long fileId) {
        this.fileId = fileId;
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