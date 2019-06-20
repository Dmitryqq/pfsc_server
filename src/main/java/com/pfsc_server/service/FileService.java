package com.pfsc_server.service;

import com.pfsc_server.domain.File;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    List<File> create(@RequestParam Long fileId, @RequestParam Long commitId, @RequestParam("file") MultipartFile[] files) throws IOException;

}
