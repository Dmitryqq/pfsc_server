package com.pfscServer.repo;

import com.pfscServer.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FilesRepo extends JpaRepository<File, Long> {

    //Требуется замена в будущем
    @Query(value = "SELECT COUNT(*) FROM files u WHERE u.commit_id = :commit_id and u.file_id = :file_id", nativeQuery = true)
    int countFiles(Long commit_id, Long file_id);

    @Query(value = "SELECT path FROM files u WHERE u.commit_id = :commit_id and u.file_id = :file_id", nativeQuery = true)
    List<String> allFiles(Long commit_id, Long file_id);

    @Query(value = "SELECT * FROM files u WHERE u.commit_id = :commit_id", nativeQuery = true)
    List<File> commits(Long commit_id);

}
