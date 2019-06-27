/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.controller;

import com.pfscServer.domain.Role;
import com.pfscServer.domain.ApplicationUser;
import com.pfscServer.repo.RolesRepo;
import com.pfscServer.repo.ApplicationUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {
    private ApplicationUserRepository applicationUserRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private RolesRepo rolesRepo;
    
    public UserController(ApplicationUserRepository applicationUserRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          RolesRepo rolesRepo) {
        this.applicationUserRepository = applicationUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.rolesRepo = rolesRepo;
    }


//    @Autowired
//    public UserController(ApplicationUserRepository applicationUserRepository, RolesRepo rolesRepo) {
//        this.applicationUserRepository = applicationUserRepository;
//    }



    @GetMapping
    public  ResponseEntity<List<ApplicationUser>> list() {
        List<ApplicationUser> users = applicationUserRepository.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //@JsonView(Views.FullUser.class)
    @GetMapping("{id}")
    public ResponseEntity<ApplicationUser> getOne(@PathVariable("id") Long userId) {
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ApplicationUser user = applicationUserRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApplicationUser> create(@RequestBody ApplicationUser user) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(applicationUserRepository.findByUsername(user.getUsername()) != null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Role role = rolesRepo.findById(user.getRoleId()).orElse(null);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole(role);
        user.setCreateDate(LocalDateTime.now());
        applicationUserRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<ApplicationUser> update(
            @PathVariable("id") Long userId,
            @RequestBody ApplicationUser user
    ) {
        ApplicationUser userFromDb = applicationUserRepository.findById(userId).orElse(null);
        if (userFromDb == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(user, userFromDb, "id");
        Role role = rolesRepo.findById(user.getRoleId()).orElse(null);
        userFromDb.setRole(role);
        userFromDb.setUpdateDate(LocalDateTime.now());
        applicationUserRepository.save(userFromDb);
        return new ResponseEntity<>(userFromDb, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApplicationUser> delete(@PathVariable("id") Long userId) {
        ApplicationUser user = applicationUserRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        applicationUserRepository.delete(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping("/sign-up")
    public void signUp(@RequestBody ApplicationUser user) {
        if(applicationUserRepository.findByUsername(user.getUsername()) == null){
            Role role = rolesRepo.findById(user.getRoleId()).orElse(null);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setRole(role);
            user.setCreateDate(LocalDateTime.now());
            applicationUserRepository.save(user);
        }
    }
}
