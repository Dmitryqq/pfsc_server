/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.repo;

import com.pfscServer.domain.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigsRepo extends JpaRepository<Config, Long> {
    Config findFirstByName(String name);
}
