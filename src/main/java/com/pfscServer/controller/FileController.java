package com.pfscServer.controller;


import com.pfscServer.domain.*;
import com.pfscServer.exception.ServiceException;
import com.pfscServer.service.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {

    @Autowired
    FileServiceImpl fileService;


    @GetMapping
    public ResponseEntity<List<File>> list() {
        List<File> files = fileService.getAll();
        if (files.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<java.io.File> getOne(@PathVariable("id") Long fileId) {

        File file = fileService.getById(fileId);
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        java.io.File newFile = new java.io.File(file.getPath());
        return new ResponseEntity<>(newFile, HttpStatus.OK);
    }

    /*//Показать есть ли уникальные файлы
    @GetMapping("{id}")
    public String fileCount(@PathVariable("id") Long fileId) throws IOException {
        String message = fileService.comparison(fileId);
        return message;

        return new ResponseEntity<>(file, HttpStatus.OK);


    //Удаление всех записей
    /*@GetMapping("{id}")
    public int fileCount(@PathVariable("id") Long fileId, @RequestParam("file") MultipartFile[] files) {
        int count = fileRepo.countFiles(fileId) + files.length;
        return count;
    }*/

    @PostMapping
    public ResponseEntity<List<File>> create(@RequestParam Long fileTypeId, @RequestParam Long commitId, @RequestParam("file") MultipartFile[] files) throws IOException, ServiceException {
        if (fileTypeId == null || commitId == null || files == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            List<File> file = fileService.create(fileTypeId, commitId, files);
            return new ResponseEntity<>(file, HttpStatus.ACCEPTED);
        }
    }


    @DeleteMapping("{id}")
    public ResponseEntity<File> delete(@PathVariable("id") Long fileId) throws IOException, ServiceException {
        fileService.deleteById(fileId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*private boolean createDir(String dir){
        File file = new File(dir);
        if (file.mkdirs()) {
            for (TypeOfFile tof : typeOfFileRepo.findAll()){
                file = new File(dir + "\\" + tof.getName());
                if(!file.mkdirs())
                    return false;
            }
            return true;
        }
        else
            return false;
    }*/
}


