package com.sensedia.jaya.api.model;

public class Weight {

	private String login;
	private String customer;
	private Integer value;

	public String getLogin() {
		return login;
	}

	public Weight setLogin(String login) {
		this.login = login;
		return this;
	}

	public String getCustomer() {
		return customer;
	}

	public Weight setCustomer(String customer) {
		this.customer = customer;
		return this;
	}

	public Integer getValue() {
		return value;
	}

	public Weight setValue(Integer value) {
		this.value = value;
		return this;
	}

}
