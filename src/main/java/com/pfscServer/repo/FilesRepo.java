package com.pfscServer.repo;

import com.pfscServer.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FilesRepo extends JpaRepository<File, Long> {

    //Требуется замена в будущем
    @Query("select count(u) from File u where u.commitId = ?1 and u.fileId = ?2")
    int countFiles(Long commitId, Long fileId);

    @Query("select u.path from File u WHERE u.commitId = ?1 and u.fileId = ?2")
    List<String> allFiles(Long commitId, Long fileId);

    List<File> findByCommitId(Long commitId);

}
