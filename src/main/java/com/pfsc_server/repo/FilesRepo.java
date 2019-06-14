package com.pfsc_server.repo;

import com.pfsc_server.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesRepo extends JpaRepository<File, Long> {
}
