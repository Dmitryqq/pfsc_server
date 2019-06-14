package com.pfsc_server.controller;


import com.pfsc_server.domain.File;
import com.pfsc_server.repo.FilesRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {
    private final FilesRepo fileRepo;

    @Autowired
    public FileController(FilesRepo fileRepo) {
        this.fileRepo = fileRepo;
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
    public ResponseEntity<File> create(@RequestBody File file) {
        fileRepo.save(file);
        return new ResponseEntity<>(file, HttpStatus.CREATED);
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


