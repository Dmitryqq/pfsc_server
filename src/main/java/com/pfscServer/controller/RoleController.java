/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.controller;

import com.pfscServer.domain.Role;
import com.pfscServer.repo.RolesRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("role")
public class RoleController {
    private final RolesRepo roleRepo;

    @Autowired
    public RoleController(RolesRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    

    @GetMapping
    public  ResponseEntity<List<Role>> list() {
        List<Role> roles = roleRepo.findAll();
        if (roles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
    
//@JsonView(Views.FullRole.class)
    @GetMapping("{id}")
    public ResponseEntity<Role> getOne(@PathVariable("id") Long roleId) {
        if (roleId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Role role = roleRepo.findById(roleId).orElse(null);
        if (role == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
    


    @PostMapping
    public ResponseEntity<Role> create(@RequestBody Role role) {
        if (role == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        roleRepo.save(role);
        return new ResponseEntity<>(role, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Role> update(
            @PathVariable("id") Long roleId,
            @RequestBody Role role
    ) {
        Role roleFromDb = roleRepo.findById(roleId).orElse(null);
        if (roleFromDb == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BeanUtils.copyProperties(role, roleFromDb, "id");
        roleRepo.save(roleFromDb);
        return new ResponseEntity<>(roleFromDb, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Role> delete(@PathVariable("id") Long roleId) {
        Role role = roleRepo.findById(roleId).orElse(null);
        if (role == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        roleRepo.delete(role);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
