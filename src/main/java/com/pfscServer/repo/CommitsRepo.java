/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.repo;

import com.pfsc_server.domain.Commit;
import com.pfsc_server.domain.CommitDto;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommitsRepo extends JpaRepository<Commit, Long>{ 
    
    static final String joinQuery = "select new com.pfsc_server.domain.CommitDto(a,b.accepted) from Commit a left join CommitHistory b on b.commitId = a.id and b.createDate = (select max(ch.createDate) from CommitHistory ch where ch.commitId = a.id)";
    
    @Query("select count(a) from Commit a where (a.createDate between ?2 and ?3) and (a.userId = ?1)")
    int CountUserCommits(Long user_id, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query(joinQuery + " where a.description like %?1%")
    List<CommitDto> findByDescriptionContaining(String description);
    
    @Query(joinQuery + " where a.description like %?1% or a.createDate between ?2 and ?3")
    List<CommitDto> findByDescriptionOrCreateDate(String param, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query(joinQuery)
    List<CommitDto> findWithHistory();
    
    @Query(joinQuery +" where a.id=?1")
    CommitDto findByIdWithHistory(Long id);
}
