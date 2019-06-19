package com.pfsc_server.controller;


import com.pfsc_server.Pfsc_serverApllication;
import com.pfsc_server.domain.*;
import com.pfsc_server.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {


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

    /*@GetMapping("{id}")
    public int fileCount(@PathVariable("id") Long fileId, @RequestParam("file") MultipartFile[] files) {
        int count = fileRepo.countFiles(fileId) + files.length;
        return count;
    }*/


    @PostMapping
    public ResponseEntity<List<File>> create(@RequestParam Long fileId, @RequestParam Long commitId, @RequestParam("file") MultipartFile[] files) throws IOException {
        if (fileId == null || commitId == null || files == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            List<File> file = new ArrayList<>();
            Commit commit = commitsRepo.findById(commitId).orElse(null);
            TypeOfFile typeOfFile = typeOfFileRepo.findById(fileId).orElse(null);
            Config rootDir = configRepo.findById(1L).orElse(null);

            File temp = new File();

            int fileLength = files.length;
            int count = fileRepo.countFiles(commitId) + fileLength;

            if (count > typeOfFile.getMax_amount()) {
                //превышен лимит кол-ва файлов
                temp.setPath("Превышен лимит");
                file.add(temp);
                return new ResponseEntity<>(file, HttpStatus.NOT_FOUND);
            } else {

                if (rootDir == null || commit == null || typeOfFile == null) {
                    temp.setPath("одно из полей пустое");
                    file.add(temp);
                    return new ResponseEntity<>(file, HttpStatus.NOT_FOUND);
                } else {
                    java.io.File folders = new java.io.File(commit.getDir(rootDir.getValue()) + "\\" + typeOfFile.getName());
                    if (!folders.exists()) {
                        folders.mkdir();
                    }
                    //необходимо сравнить файлы между собой на равенство
                    //потом необходимо сравнить их на равенство между другими файлами из каталога

                    for (MultipartFile uploadedFile : files) {
                        if (uploadedFile.getSize() > typeOfFile.getMax_size() * 1024) {
                            temp.setPath("размер файла превыщает допустимый");
                            file.add(temp);
                            return new ResponseEntity<>(file, HttpStatus.NOT_FOUND);
                        } else {
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
                    }
                    fileRepo.saveAll(file);
                    return new ResponseEntity<>(file, HttpStatus.CREATED);
                }
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


