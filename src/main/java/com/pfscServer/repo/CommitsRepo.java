/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pfscServer.repo;

import com.pfscServer.domain.Commit;
import com.pfscServer.domain.CommitDto;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommitsRepo extends JpaRepository<Commit, Long>{ 
    
    static final String joinQuery = "select new com.pfscServer.domain.CommitDto(a,b.activity) from Commit a left join CommitHistory b on b.commitId = a.id and (b.activity='принят' or b.activity='отклонен')";
    
    @Query("select count(a) from Commit a where (a.createDate between ?2 and ?3) and (a.userId = ?1)")
    int CountUserCommits(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query(joinQuery + " where a.description like %?1% order by b.activity desc,a.createDate desc")
    List<CommitDto> findByDescriptionContaining(String description);
    
    @Query(joinQuery + " where a.description like %?1% or a.createDate between ?2 and ?3 order by b.activity desc,a.createDate desc")
    List<CommitDto> findByDescriptionOrCreateDate(String param, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query(joinQuery + " where a.userId = ?2 and a.description like %?1% order by b.activity desc,a.createDate desc")
    List<CommitDto> findByUserIdAndDescriptionContaining(String description, Long userId);
    
    @Query(joinQuery + " where a.userId = ?4 and (a.description like %?1% or a.createDate between ?2 and ?3) order by b.activity desc,a.createDate desc")
    List<CommitDto> findByUserIdAndDescriptionOrCreateDate(String param, LocalDateTime startDate, LocalDateTime endDate, Long userId);
    
    @Query(joinQuery + " order by b.activity desc, a.createDate desc")
    List<CommitDto> findAllDto();
    
    @Query(joinQuery + " where a.userId = ?1 order by b.activity desc, a.createDate desc")
    List<CommitDto> findByUserIdDto(Long userId);
    
    @Query(joinQuery +" where a.id=?1")
    CommitDto findByIdDto(Long id);
    
    boolean existsCommitByMarkId(Long markId);
}
