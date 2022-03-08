package com.venkate.boot.batch;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.venkate.boot.batch.config.BatchConfiguration;

@SpringBootTest
class SpringBatchApplicationTests {

	@Autowired
	private JobLauncher launcher;
	
	@Autowired
	private BatchConfiguration job;
	
	@Test
	void testCSVtoDB() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		System.out.println("***********Test for CSV****************");
		//JobParametersBuilder builder=new JobParametersBuilder();
//		new JobParametersBuilder().addString("JobId", "CSVToDB_"+System.currentTimeMillis());
		launcher.run(job.job(), new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
	}

}
