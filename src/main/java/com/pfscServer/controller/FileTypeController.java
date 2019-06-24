package com.pfscServer.controller;

import com.pfscServer.domain.FileType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.pfscServer.repo.FileTypesRepo;

@RestController
@RequestMapping("typeOfFile")
public class FileTypeController {
    private final FileTypesRepo typeOfFileRepo;

    @Autowired
    public FileTypeController(FileTypesRepo typeOfFileRepo) {
        this.typeOfFileRepo = typeOfFileRepo;
    }



    @GetMapping
    public  ResponseEntity<List<FileType>> list() {
        List<FileType> typeOfFiles = typeOfFileRepo.findAll();
        if (typeOfFiles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(typeOfFiles, HttpStatus.OK);
    }

    //@JsonView(Views.FullTypeOfFile.class)
    @GetMapping("{id}")
    public ResponseEntity<FileType> getOne(@PathVariable("id") Long typeOfFileId) {
        if (typeOfFileId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        FileType typeOfFile = typeOfFileRepo.findById(typeOfFileId).orElse(null);
        if (typeOfFile == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(typeOfFile, HttpStatus.OK);
    }



    @PostMapping
    public ResponseEntity<FileType> create(@RequestBody FileType typeOfFile) {
        if (typeOfFile == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        typeOfFileRepo.save(typeOfFile);
        return new ResponseEntity<>(typeOfFile, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<FileType> update(
            @PathVariable("id") Long typeOfFileId,
            @RequestBody FileType typeOfFile
    ) {
        FileType typeOfFileFromDb = typeOfFileRepo.findById(typeOfFileId).orElse(null);
        if (typeOfFileFromDb == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(typeOfFile, typeOfFileFromDb, "id");
        typeOfFileRepo.save(typeOfFileFromDb);
        return new ResponseEntity<>(typeOfFileFromDb, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<FileType> delete(@PathVariable("id") Long typeOfFileId) {
        FileType typeOfFile = typeOfFileRepo.findById(typeOfFileId).orElse(null);
        if (typeOfFile == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        typeOfFileRepo.delete(typeOfFile);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
