package com.duallab.verapdf.jobservice.server.repository;

import com.duallab.verapdf.jobservice.model.entity.Job;
import com.duallab.verapdf.jobservice.model.entity.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Maksim Bezrukov
 */
@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<Job> findTop100ByLastUsedBefore(Instant expiredTime);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Job> findByIdAndStatus(UUID uuid, JobStatus status);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Override
	Optional<Job> findById(UUID uuid);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Override
	void delete(Job entity);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Override
	void deleteAll(Iterable<? extends Job> entities);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Override
	<S extends Job> S saveAndFlush(S entity);
}
