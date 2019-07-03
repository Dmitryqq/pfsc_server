/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.controller;

import com.pfscServer.domain.Role;
import com.pfscServer.repo.RolesRepo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "Операции по взаимодействию с ролями (роли статичны и операции над ролями не проводятся)")
@RestController
@RequestMapping("role")
public class RoleController {
    private final RolesRepo roleRepo;

    @Autowired
    public RoleController(RolesRepo roleRepo) {
        this.roleRepo = roleRepo;
    }


    @ApiOperation(value = "Получение списка всех ролей")
    @GetMapping
    public  ResponseEntity<List<Role>> list() {
        List<Role> roles = roleRepo.findAll();
        if (roles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @ApiOperation(value = "Получение названия роли по id")
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


    @ApiOperation(value = "Создание роли (не используется)")
    @PostMapping
    public ResponseEntity<Role> create(@RequestBody Role role) {
        if (role == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        roleRepo.save(role);
        return new ResponseEntity<>(role, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Обновление роли (не используется)")
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

    @ApiOperation(value = "Удаление роли (не используется)")
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
