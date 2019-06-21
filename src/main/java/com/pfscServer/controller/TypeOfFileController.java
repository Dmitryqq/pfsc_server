package com.pfscServer.controller;

import com.pfscServer.domain.TypeOfFile;
import com.pfscServer.repo.TypeOfFileRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("typeOfFile")
public class TypeOfFileController {
    private final TypeOfFileRepo typeOfFileRepo;

    @Autowired
    public TypeOfFileController(TypeOfFileRepo typeOfFileRepo) {
        this.typeOfFileRepo = typeOfFileRepo;
    }



    @GetMapping
    public  ResponseEntity<List<TypeOfFile>> list() {
        List<TypeOfFile> typeOfFiles = typeOfFileRepo.findAll();
        if (typeOfFiles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(typeOfFiles, HttpStatus.OK);
    }

    //@JsonView(Views.FullTypeOfFile.class)
    @GetMapping("{id}")
    public ResponseEntity<TypeOfFile> getOne(@PathVariable("id") Long typeOfFileId) {
        if (typeOfFileId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TypeOfFile typeOfFile = typeOfFileRepo.findById(typeOfFileId).orElse(null);
        if (typeOfFile == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(typeOfFile, HttpStatus.OK);
    }



    @PostMapping
    public ResponseEntity<TypeOfFile> create(@RequestBody TypeOfFile typeOfFile) {
        if (typeOfFile == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        typeOfFileRepo.save(typeOfFile);
        return new ResponseEntity<>(typeOfFile, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<TypeOfFile> update(
            @PathVariable("id") Long typeOfFileId,
            @RequestBody TypeOfFile typeOfFile
    ) {
        TypeOfFile typeOfFileFromDb = typeOfFileRepo.findById(typeOfFileId).orElse(null);
        if (typeOfFileFromDb == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(typeOfFile, typeOfFileFromDb, "id");
        typeOfFileRepo.save(typeOfFileFromDb);
        return new ResponseEntity<>(typeOfFileFromDb, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<TypeOfFile> delete(@PathVariable("id") Long typeOfFileId) {
        TypeOfFile typeOfFile = typeOfFileRepo.findById(typeOfFileId).orElse(null);
        if (typeOfFile == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        typeOfFileRepo.delete(typeOfFile);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
