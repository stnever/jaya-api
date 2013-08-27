package com.sensedia.jaya.api.model;

public class User {

	private String userId;

	private String name;

	private String email;

	private String sessionId;

	private String imageUrl;

	public String getUserId() {
		return userId;
	}

	public User setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getName() {
		return name;
	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getSessionId() {
		return sessionId;
	}

	public User setSessionId(String sessionId) {
		this.sessionId = sessionId;
		return this;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public User setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		return this;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", name=" + name + ", email=" + email + ", sessionId=" + sessionId + "]";
	}

}
