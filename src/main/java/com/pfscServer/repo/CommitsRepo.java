/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.repo;

import com.pfscServer.domain.Commit;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommitsRepo extends JpaRepository<Commit, Long>{ 
    
    @Query(value = "SELECT COUNT(*) FROM commits a WHERE (DATE_PART('day',?1 - a.create_date)+DATE_PART('hour',?1 - a.create_date)/24+0.9)<1 and a.user_id = ?2",  nativeQuery = true)
    int CountUserCommits(LocalDateTime create_date,Long user_id);
    
    List<Commit> findByDescriptionContainingIgnoreCase(String description);
    
    @Query(value = "SELECT * FROM commits a WHERE (DATE_PART('day',?2 - a.create_date)+DATE_PART('hour',?2 - a.create_date)/24+0.9)<1 or a.description LIKE %?1%",  nativeQuery = true)
    List<Commit> findByDescriptionOrCreateDate(String param, LocalDateTime create_date);
}
