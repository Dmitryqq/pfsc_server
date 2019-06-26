package com.pfscServer.service;

import com.pfscServer.domain.File;
import com.pfscServer.exception.FileException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    List<File> create(@RequestParam Long fileTypeId, @RequestParam Long commitId, @RequestParam("file") MultipartFile[] files) throws IOException, FileException;
    void deleteByCommit(@RequestParam Long id);
    String comparison(@RequestParam Long id) throws IOException;
}
