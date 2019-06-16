package com.pfsc_server.controller;


import com.pfsc_server.Pfsc_serverApllication;
import com.pfsc_server.domain.*;
import com.pfsc_server.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {

    private static Logger LOG = LoggerFactory
            .getLogger(FileController.class);

    private final FilesRepo fileRepo;
    private final ConfigsRepo configRepo;
    private final TypeOfFileRepo typeOfFileRepo;
    private final CommitsRepo commitsRepo;

    public FileController(FilesRepo fileRepo, ConfigsRepo configRepo, TypeOfFileRepo typeOfFileRepo, CommitsRepo commitsRepo) {
        this.fileRepo = fileRepo;
        this.configRepo = configRepo;
        this.typeOfFileRepo = typeOfFileRepo;
        this.commitsRepo = commitsRepo;
    }


    @GetMapping
    public ResponseEntity<List<File>> list() {
        List<File> files = fileRepo.findAll();
        if (files.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<File> getOne(@PathVariable("id") Long fileId) {
        File file = fileRepo.findById(fileId).orElse(null);
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(file, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<File > create(@RequestParam Long fileId, @RequestParam Long commitId, @RequestParam("file") MultipartFile[] files) throws IOException {
        if (fileId == null || commitId == null || files == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            List<File> asasfas = new ArrayList<File>();
            List<File> file = new ArrayList<File>();
            Commit commit = commitsRepo.findById(fileId).orElse(null);
            TypeOfFile typeOfFile = typeOfFileRepo.findById(commitId).orElse(null);
            Config rootDir = configRepo.findById(1L).orElse(null);
            if (rootDir == null || commit == null || typeOfFile == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                java.io.File folders = new java.io.File(commit.getDir(rootDir.getValue()) + "\\" + typeOfFile.getName());

                for (MultipartFile uploadedFile : files) {
                    File tempFile = new File();
                    tempFile.setCommit_id(commitId);
                    tempFile.setFile_id(fileId);
                    tempFile.setFile(typeOfFile);
                    tempFile.setCommit(commit);
                    String pathFile = commit.getDir(rootDir.getValue()) + "\\" + typeOfFile.getName() + "\\" + uploadedFile.getOriginalFilename();
                    uploadedFile.transferTo(new java.io.File(pathFile));
                    tempFile.setPath(pathFile);
                    file.add(tempFile);
                }

                fileRepo.saveAll(file);

                return new ResponseEntity<>(file.get(1), HttpStatus.CREATED);
            }

        }
    }



    @PutMapping("{id}")
    public ResponseEntity<File> update(
            @PathVariable("id") Long fileId,
            @RequestBody File file
    ) {
        File fileFromDb = fileRepo.findById(fileId).orElse(null);
        BeanUtils.copyProperties(file, fileFromDb, "id");
        fileRepo.save(fileFromDb);
        return new ResponseEntity<>(fileFromDb, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<File> delete(@PathVariable("id") Long fileId) {
        File file = fileRepo.findById(fileId).orElse(null);
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        fileRepo.delete(file);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


