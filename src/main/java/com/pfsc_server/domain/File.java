package com.pfsc_server.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;


@Entity
@Table(name = "files")
@ToString()
@EqualsAndHashCode(of = {"id"})
public class File {

    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    @Getter
    private Long commitId;

    @Setter
    @Getter
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private TypeOfFile fileId;

    @Setter
    @Getter
    private String path;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommitId() {
        return commitId;
    }

    public void setCommitId(Long commitId) {
        this.commitId = commitId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public TypeOfFile getFileId() {
        return fileId;
    }

    public void setFileId(TypeOfFile fileId) {
        this.fileId = fileId;
    }

}