package com.sensedia.jaya.api.model;

import java.util.Date;

public class Comment {

	private Long id;
	private Long userId;
	private String userName;
	private String text;
	private Date date;

	public Long getUserId() {
		return userId;
	}

	public Comment setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public String getUserName() {
		return userName;
	}

	public Comment setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public String getText() {
		return text;
	}

	public Comment setText(String text) {
		this.text = text;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public Comment setDate(Date date) {
		this.date = date;
		return this;
	}

	public Long getId() {
		return id;
	}

	public Comment setId(Long id) {
		this.id = id;
		return this;
	}

}
