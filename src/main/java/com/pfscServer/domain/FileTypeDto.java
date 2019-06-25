
package com.pfscServer.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class FileTypeDto implements Serializable {

    public FileTypeDto(FileType fileType) {
        this.id = fileType.getId();
        this.name = fileType.getName();
        this.maxSize = fileType.getMaxSize();
        this.required = fileType.isRequired();
        this.types = fileType.getTypes();
        this.maxAmount = fileType.getMaxAmount();
        this.roleId = fileType.getRoleId();
        this.enableAfterAccept = fileType.isEnableAfterAccept();
    }

    private Long id;
    private String  name;
    private Long maxSize;
    private boolean required;
    private String types;
    private int maxAmount;
    private Long roleId;
    private boolean enableAfterAccept;
    private List<File>files = new ArrayList();

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Long maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }
    
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
    
    public boolean isEnableAfterAccept() {
        return enableAfterAccept;
    }

    public void setEnableAfterAccept(boolean enableAfterAccept) {
        this.enableAfterAccept = enableAfterAccept;
    }
}