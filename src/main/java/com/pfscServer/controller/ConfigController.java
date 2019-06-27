package com.pfscServer.controller;

import com.pfscServer.domain.Config;
import com.pfscServer.repo.ConfigsRepo;
import com.pfscServer.repo.RolesRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("config")
public class ConfigController {
    @Autowired
    private ConfigsRepo configsRepo;





    @GetMapping
    public ResponseEntity<List<Config>> list() {
        List<Config> config = configsRepo.findAll();
        if (config.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(config, HttpStatus.OK);
    }

    //@JsonView(Views.FullRole.class)
    @GetMapping("{id}")
    public ResponseEntity<Config> getOne(@PathVariable("id") Long configId) {
        if (configId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Config config = configsRepo.findById(configId).orElse(null);
        if (config == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(config, HttpStatus.OK);
    }



    @PostMapping
    public ResponseEntity<Config> create(@RequestBody Config config) {
        if (config == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        configsRepo.save(config);
        return new ResponseEntity<>(config, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Config> update(
            @PathVariable("id") Long configId,
            @RequestBody Config config
    ) {
        Config configFromDB = configsRepo.findById(configId).orElse(null);
        if (configFromDB == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(config, configFromDB, "id");
        configsRepo.save(configFromDB);
        return new ResponseEntity<>(configFromDB, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Config> delete(@PathVariable("id") Long configId) {
        Config config = configsRepo.findById(configId).orElse(null);
        if (configId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        configsRepo.delete(config);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}