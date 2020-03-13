package com.duallab.verapdf.jobservice.server.repository;

import com.duallab.verapdf.jobservice.model.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author Maksim Bezrukov
 */
@Repository
public interface StoredFileRepository extends JpaRepository<StoredFile, UUID> {

	List<StoredFile> findTop100BySourceForIsNullAndResultForIsNull();
}
