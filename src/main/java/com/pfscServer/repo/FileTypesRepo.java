package com.pfscServer.repo;

import com.pfscServer.domain.FileType;
import com.pfscServer.domain.FileTypeDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FileTypesRepo extends JpaRepository<FileType, Long> {
    
    @Query("select new com.pfscServer.domain.FileTypeDto(a) from FileType a")
    List<FileTypeDto> findAllDto();
}
