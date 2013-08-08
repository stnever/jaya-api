package com.sensedia.jaya.api.model;

import java.util.ArrayList;
import java.util.List;

public class Pain {

	private String id;
	private String title;
	private String description;

	private List<Opinion> opinions = new ArrayList<Opinion>();

	private List<Comment> comments = new ArrayList<Comment>();

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

	public List<Opinion> getOpinions() {
		return opinions;
	}

	public Pain setOpinions(List<Opinion> opinions) {
		this.opinions = opinions;
		return this;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public Pain setComments(List<Comment> comments) {
		this.comments = comments;
		return this;
	}

	@Override
	public String toString() {
		return "Pain [id=" + id + ", title=" + title + ", description=" + description + ", opinions=" + opinions
				+ ", comments=" + comments + "]";
	}

}
