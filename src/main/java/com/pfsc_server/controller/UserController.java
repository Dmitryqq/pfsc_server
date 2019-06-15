/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.controller;

import com.pfsc_server.domain.Role;
import com.pfsc_server.domain.User;
import com.pfsc_server.repo.RolesRepo;
import com.pfsc_server.repo.UsersRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {
    private final UsersRepo userRepo;
    private final RolesRepo rolesRepo;

    @Autowired
    public UserController(UsersRepo userRepo, RolesRepo rolesRepo) {
        this.userRepo = userRepo;
        this.rolesRepo = rolesRepo;
    }



    @GetMapping
    public  ResponseEntity<List<User>> list() {
        List<User> users = userRepo.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //@JsonView(Views.FullUser.class)
    @GetMapping("{id}")
    public ResponseEntity<User> getOne(@PathVariable("id") Long userId) {
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        user.setPassword(getMd5(user.getPassword())); //password to md5
        Role role = rolesRepo.findById(user.getRole_id()).orElse(null);
        user.setRole(role);
        user.setCreate_date(LocalDateTime.now());
        userRepo.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<User> update(
            @PathVariable("id") Long userId,
            @RequestBody User user
    ) {
        User userFromDb = userRepo.findById(userId).orElse(null);
        if (userFromDb == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(user, userFromDb, "id");
        userFromDb.setPassword(getMd5(user.getPassword()));
        Role role = rolesRepo.findById(user.getRole_id()).orElse(null);
        userFromDb.setRole(role);
        userFromDb.setUpdate_date(LocalDateTime.now());
        userRepo.save(userFromDb);
        return new ResponseEntity<>(userFromDb, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<User> delete(@PathVariable("id") Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userRepo.delete(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    public static String getMd5(String input)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
