package org.ananas.runner.model.api.job;

import org.ananas.runner.model.core.DagRequest;
import org.ananas.runner.model.core.Job;
import org.ananas.runner.model.steps.commons.jobs.LocalJobManager;

import java.io.IOException;
import java.util.UUID;

public class LocalJobApiClient implements JobClient {

	@Override
	public String createJob(String projectId, String token, DagRequest dagRequest) throws IOException {
		// simply create a uuid as job id
		return UUID.randomUUID().toString();
	}

	@Override
	public void updateJobState(String jobId) throws IOException {
		// do't need to implement this method at all
	}
}
