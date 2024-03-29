package com.pfscServer.controller;

import com.pfscServer.domain.Mark;
import com.pfscServer.repo.CommitsRepo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.pfscServer.repo.MarksRepo;

@Api(description = "Операции по взаимодействию с метками")
@RestController
@RequestMapping("mark")
public class MarkController {
    
    @Autowired
    MarksRepo markRepo;
    @Autowired
    CommitsRepo commitRepo;

    @ApiOperation(value = "Получение списка всех меток")
    @GetMapping
    public  ResponseEntity<List<Mark>> list() {
        List<Mark> marks = markRepo.findAll();
        if (marks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(marks, HttpStatus.OK);
    }

    @ApiOperation(value = "Создание метки")
    @PostMapping
    public ResponseEntity<Mark> create(@RequestBody Mark mark) {
        if (mark == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        markRepo.save(mark);
        return new ResponseEntity<>(mark, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Удаление метки")
    @DeleteMapping("{id}")
    public ResponseEntity<Mark> delete(@PathVariable("id") Long markId) {
        Mark mark = markRepo.findById(markId).orElse(null);
        if (mark == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(commitRepo.existsCommitByMarkId(markId))
            return new ResponseEntity<>(HttpStatus.LOCKED);
        markRepo.delete(mark);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
