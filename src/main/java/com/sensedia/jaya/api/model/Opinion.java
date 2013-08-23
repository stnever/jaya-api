package com.sensedia.jaya.api.model;

public class Opinion {

	private Long id;
	private String userId;
	private Long customerId;
	private String painId; // string pois é a key do JIRA
	private Integer value;
	private String comment;

	private String userLogin;

	public Long getId() {
		return id;
	}

	public Opinion setId(Long id) {
		this.id = id;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public Opinion setUserId(String userId) {
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

	public String getUserLogin() {
		return userLogin;
	}

	public Opinion setUserLogin(String userLogin) {
		this.userLogin = userLogin;
		return this;
	}

	@Override
	public String toString() {
		return "Opinion [id=" + id + ", userId=" + userId + ", customerId=" + customerId + ", painId=" + painId
				+ ", value=" + value + ", comment=" + comment + "]";
	}

}
