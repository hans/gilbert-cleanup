package com.dvcs.gilbertcleanup.models;

public class User {

	private int key;
	private int status;
	private String username;
	private String email;
	
	public User(int key, int status, String username, String email) {
		this.key = key;
		this.status = status;
		this.username = username;
		this.email = email;
	}

	public int getKey() {
		return key;
	}

	public int getStatus() {
		return status;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

}
