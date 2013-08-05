package com.sensedia.jaya.api.model;

public class Opinion {

	private Long id;
	private Long userId;
	private Long customerId;
	private String painId; // string pois é a key do JIRA
	private Integer value;
	private String comment;

	public Long getId() {
		return id;
	}

	public Opinion setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public Opinion setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public Opinion setCustomerId(Long customerId) {
		this.customerId = customerId;
		return this;
	}

	public String getPainId() {
		return painId;
	}

	public Opinion setPainId(String painId) {
		this.painId = painId;
		return this;
	}

	public Integer getValue() {
		return value;
	}

	public Opinion setValue(Integer value) {
		this.value = value;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public Opinion setComment(String comment) {
		this.comment = comment;
		return this;
	}

}
