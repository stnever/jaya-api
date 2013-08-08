package com.sensedia.jaya.api.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {

	private Long id;
	private String name;
	private String description;
	private List<Comment> comments = new ArrayList<Comment>();
	private List<Opinion> opinions = new ArrayList<Opinion>();

	public Long getId() {
		return id;
	}

	public Customer setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Customer setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Customer setDescription(String description) {
		this.description = description;
		return this;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public Customer setComments(List<Comment> comments) {
		this.comments = comments;
		return this;
	}

	public List<Opinion> getOpinions() {
		return opinions;
	}

	public Customer setOpinions(List<Opinion> opinions) {
		this.opinions = opinions;
		return this;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + ", description=" + description + ", comments=" + comments
				+ ", opinions=" + opinions + "]";
	}

}
