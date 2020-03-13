package com.duallab.verapdf.jobservice.server.service;

import com.duallab.verapdf.jobservice.model.dto.JobDTO;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class JobSender {

	private final JmsTemplate jmsTemplate;
	private final JobService jobService;

	public JobSender(JmsTemplate jmsTemplate, JobService jobService) {
		this.jmsTemplate = jmsTemplate;
		this.jobService = jobService;
	}

	public void sendTask(JobDTO jobDTO) {
		try {
			jmsTemplate.convertAndSend("verapdf.jobs", jobDTO.getId().toString());
		} catch (Throwable e) {
			jobService.deleteJobById(jobDTO.getId());
			throw e;
		}
	}
}
