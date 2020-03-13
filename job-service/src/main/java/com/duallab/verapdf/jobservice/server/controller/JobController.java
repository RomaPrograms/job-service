package com.duallab.verapdf.jobservice.server.controller;

import com.duallab.verapdf.jobservice.model.dto.JobDTO;
import com.duallab.verapdf.jobservice.model.dto.StoredFileDTO;
import com.duallab.verapdf.jobservice.model.error.exception.BadRequestException;
import com.duallab.verapdf.jobservice.model.error.exception.NotFoundException;
import com.duallab.verapdf.jobservice.model.error.exception.VeraPDFBackEndException;
import com.duallab.verapdf.jobservice.server.service.JobSender;
import com.duallab.verapdf.jobservice.server.service.JobService;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * @author Maksim Bezrukov
 */
@RestController
@RequestMapping("/job")
@Validated
public class JobController {

	private final JobService jobService;
	private final JobSender jobSender;

	public JobController(JobService jobService, JobSender jobSender) {
		this.jobService = jobService;
		this.jobSender = jobSender;
	}

	@GetMapping("/{jobId}")
	public JobDTO getJob(@PathVariable UUID jobId) throws NotFoundException {
		return jobService.getJobById(jobId);
	}


	@PostMapping
	public ResponseEntity<JobDTO> createJob(@RequestPart @Valid JobDTO job,
                                            @RequestPart List<MultipartFile> file) throws IOException, VeraPDFBackEndException {
		JobDTO result = jobService.createJob(job, file);
		jobSender.sendTask(result);
		URI uri = MvcUriComponentsBuilder.fromMethodName(JobController.class,
		                                                 "getJob",
		                                                 result.getId())
		                                 .build()
		                                 .encode()
		                                 .toUri();
		return ResponseEntity.created(uri).body(result);
	}

	@GetMapping("/{jobId}/result")
	public ResponseEntity<Resource> getJobResult(@PathVariable UUID jobId) throws NotFoundException, IOException, BadRequestException {
		Pair<StoredFileDTO, Resource> result = jobService.getResultByJobId(jobId);
		return ResponseEntity.ok()
		                     .contentType(MediaType.valueOf(result.getFirst().getFileType()))
		                     .body(result.getSecond());
	}

	@DeleteMapping("/{jobId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteJob(@PathVariable UUID jobId) {
		jobService.deleteJobById(jobId);
	}
}
