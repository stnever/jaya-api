package com.sensedia.jaya.api.model;

import java.util.List;

public class Pain {

	private String id;
	private String title;
	private String description;
	private List<Weight> weights;

	public String getId() {
		return id;
	}

	public Pain setId(String id) {
		this.id = id;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Pain setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Pain setDescription(String description) {
		this.description = description;
		return this;
	}

	public List<Weight> getWeights() {
		return weights;
	}

	public Pain setWeights(List<Weight> weights) {
		this.weights = weights;
		return this;
	}

}
