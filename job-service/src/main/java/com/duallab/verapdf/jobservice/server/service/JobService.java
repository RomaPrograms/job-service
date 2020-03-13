package com.duallab.verapdf.jobservice.server.service;

import com.duallab.verapdf.jobservice.model.dto.JobDTO;
import com.duallab.verapdf.jobservice.model.dto.StoredFileDTO;
import com.duallab.verapdf.jobservice.model.entity.Job;
import com.duallab.verapdf.jobservice.model.entity.StoredFile;
import com.duallab.verapdf.jobservice.model.entity.enums.JobError;
import com.duallab.verapdf.jobservice.model.entity.enums.JobStatus;
import com.duallab.verapdf.jobservice.model.error.exception.BadRequestException;
import com.duallab.verapdf.jobservice.model.error.exception.NotFoundException;
import com.duallab.verapdf.jobservice.model.error.exception.VeraPDFBackEndException;
import com.duallab.verapdf.jobservice.server.mapper.JobMapper;
import com.duallab.verapdf.jobservice.server.mapper.StoredFileMapper;
import com.duallab.verapdf.jobservice.server.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @author Maksim Bezrukov
 */
@Service
public class JobService {
	private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

	private final long lifetimeDelay;
	private final JobRepository jobRepository;
	private final StoredFileService storedFileService;
	private final LocalFileService localFileService;
	private final JobMapper jobMapper;
	private final StoredFileMapper storedFileMapper;

	public JobService(@Value("${verapdf.cleaning.job.lifetime-delay-sec}") long lifetimeDelay,
	                  JobRepository jobRepository,
	                  StoredFileService storedFileService, LocalFileService localFileService,
	                  JobMapper jobMapper, StoredFileMapper storedFileMapper) {
		this.lifetimeDelay = lifetimeDelay;
		this.jobRepository = jobRepository;
		this.storedFileService = storedFileService;
		this.localFileService = localFileService;
		this.jobMapper = jobMapper;
		this.storedFileMapper = storedFileMapper;
	}

	@Transactional
	@Scheduled(cron = "${verapdf.cleaning.job.cron}")
	public void clearJobs() {
		Instant expiredTime = Instant.now().minusSeconds(lifetimeDelay);
		List<Job> expiredJobs;
		do {
			expiredJobs = jobRepository.findTop100ByLastUsedBefore(expiredTime);
			expiredJobs.forEach(this::prepareJobToDelete);
			jobRepository.deleteAll(expiredJobs);
		} while (!expiredJobs.isEmpty());
		storedFileService.clearFilesWithoutJobReference();
	}

	@Transactional
	public JobDTO createJob(JobDTO jobDTO, List<MultipartFile> files) throws IOException, VeraPDFBackEndException {
		List<StoredFile> sourceFiles = localFileService.saveSourceFiles(files, jobDTO.getSourceFiles());

		Job saved = jobRepository.saveAndFlush(new Job(jobDTO.getProfile()));
		sourceFiles.forEach(saved::addSourceFile);
		saved = jobRepository.saveAndFlush(saved);

		return jobMapper.createDTOFromEntity(saved);
	}

	@Transactional(timeout = 60)
	public JobDTO getJobById(UUID jobId) throws NotFoundException {
		Job job = findJobById(jobId);
		job.updateLastUsed();
		return jobMapper.createDTOFromEntity(job);
	}

	@Transactional
	public Pair<StoredFileDTO, Resource> getResultByJobId(UUID jobId) throws NotFoundException, BadRequestException, FileNotFoundException {
		Job job = findJobById(jobId);
		StoredFile result = job.getResultFile();
		if (result == null) {
			throw new BadRequestException("The specified job has no result");
		}
		Resource resource = localFileService.getFileResource(result);
		StoredFileDTO resultDTO = storedFileMapper.createDTOFromEntity(result);
		return Pair.of(resultDTO, resource);
	}

	@Transactional
	public void deleteJobById(UUID jobId) {
		try {
			Job toDelete = findJobById(jobId);
			prepareJobToDelete(toDelete);
			jobRepository.delete(toDelete);
		} catch (NotFoundException e) {
			// Job already deleted
		}
	}

	@Transactional
	public void jobFinished(UUID jobId, InputStream resultStream,
	                        String resultContentType, StoredFileDTO resultFileDTO) {
		Job job;
		try {
			job = findJobByIdAndStatus(jobId, JobStatus.PROCESSING);
		} catch (NotFoundException e) {
			// ignore missing job
			return;
		}
		job.getSourceFiles().forEach(localFileService::deleteFileFromDisk);
		try {
			StoredFile result = localFileService.saveResultFile(resultStream, resultContentType, resultFileDTO);
			job.finished(result);
		} catch (Throwable e) {
			LOGGER.error("Can not save result file for job " + jobId, e);
			job.error(JobError.INTERNAL_ERROR, e.getMessage());
		}
	}

	@Transactional
	public void jobError(UUID jobId, JobError errorType, String exceptionMessage) {
		LOGGER.warn("Obtained job with error result. Job id: " + jobId + ", exception message: " + exceptionMessage);
		try {
			Job job = findJobByIdAndStatus(jobId, JobStatus.PROCESSING);
			job.getSourceFiles().forEach(localFileService::deleteFileFromDisk);
			job.error(errorType, exceptionMessage);
		} catch (NotFoundException e) {
			// ignore missing job
		}
	}

	// shall be executed inside a transactional method
	private void prepareJobToDelete(Job toDelete) {
		List<StoredFile> sourceFiles = toDelete.getSourceFiles();
		sourceFiles.forEach(localFileService::deleteFileFromDisk);
		localFileService.deleteFileFromDisk(toDelete.getResultFile());
	}

	private Job findJobByIdAndStatus(UUID jobId, JobStatus status) throws NotFoundException {
		return jobRepository.findByIdAndStatus(jobId, status)
		                    .orElseThrow(() -> new NotFoundException("Job with specified id and status does not exist"));
	}

	private Job findJobById(UUID jobId) throws NotFoundException {
		return jobRepository.findById(jobId)
		                    .orElseThrow(() -> new NotFoundException("Job with specified id does not exist"));
	}
}
