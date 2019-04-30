package org.ananas.runner.model.api.pipeline;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.LinkedList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PipelineInfo {
	public String id;
	public String projectId;
	public String name;
	public String description;
	public LinkedList<String> steps;
}
