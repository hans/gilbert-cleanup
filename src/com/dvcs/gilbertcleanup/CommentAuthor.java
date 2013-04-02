package com.dvcs.gilbertcleanup;

public class CommentAuthor {

	private String username;
	private int key;
	
	public CommentAuthor(String username, int key) {
		this.username = username;
		this.key = key;
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getKey() {
		return key;
	}
	
}
