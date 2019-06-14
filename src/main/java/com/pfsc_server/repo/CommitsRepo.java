/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfsc_server.repo;

import com.pfsc_server.domain.Commit;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommitsRepo extends JpaRepository<Commit, Long>{ 
    
    @Query(value = "SELECT COUNT(*) FROM Commits a WHERE DATE_PART('day', :create_date - a.create_date)<1 and a.user_id = :user_id",  nativeQuery = true)
    int CountUserCommits(@Param("create_date")LocalDateTime create_date,@Param("user_id") Long user_id);
    
}
