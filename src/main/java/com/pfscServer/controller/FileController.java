package com.pfscServer.controller;


import com.pfscServer.domain.*;
import com.pfscServer.exception.ServiceException;
import com.pfscServer.service.FileServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;

@Api(description = "Операции по взаимодействию с файлами")
@RestController
@RequestMapping("file")
public class FileController {

    @Autowired
    FileServiceImpl fileService;

    @ApiOperation(value = "Получение списка файлов")
    @GetMapping
    public ResponseEntity<List<File>> list() {
        List<File> files = fileService.getAll();
        if (files.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @ApiOperation(value = "Получение файла")
    @GetMapping("{id}")
    public ResponseEntity<byte[]> getOne(@PathVariable("id") Long fileId, HttpServletRequest request) throws IOException {
        File file = fileService.getById(fileId);
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        java.io.File tempFilePath = new java.io.File(file.getPath());
        Tika tika = new Tika();
        String mimeType = tika.detect(tempFilePath);
        MediaType contentType;
        if(mimeType != null){
            contentType = MediaType.parseMediaType(mimeType);
            byte[] resource = FileCopyUtils.copyToByteArray(tempFilePath);
            return ResponseEntity.ok()
                .contentType(contentType)
                .body(resource);
        }
        else
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
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

    @ApiOperation(value = "Создание файла")
    @PostMapping
    public ResponseEntity<List<File>> create(@RequestParam Long fileTypeId, @RequestParam Long commitId, @RequestParam("file") MultipartFile[] files) throws IOException, ServiceException {
        if (fileTypeId == null || commitId == null || files == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            List<File> file = fileService.create(fileTypeId, commitId, files);
            return new ResponseEntity<>(file, HttpStatus.ACCEPTED);
        }
    }

    @ApiOperation(value = "Удаление файла по id")
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


