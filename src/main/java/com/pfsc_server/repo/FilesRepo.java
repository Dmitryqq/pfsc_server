package com.pfsc_server.repo;

import com.pfsc_server.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface FilesRepo extends JpaRepository<File, Long> {

    //Требуется замена в будущем
    @Query(value = "SELECT COUNT(*) FROM files u WHERE u.commit_id = :commit_id", nativeQuery = true)
    int countFiles(Long commit_id);

}
