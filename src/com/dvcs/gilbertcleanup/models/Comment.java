package com.dvcs.gilbertcleanup.models;

public class Comment {

	private User author;
	private int key;
	private int time;
	private String text;
	
	public Comment(User author, int key, int time, String text) {
		this.author = author;
		this.key = key;
		this.time = time;
		this.text = text;
	}

	public User getAuthor() {
		return author;
	}

	public int getKey() {
		return key;
	}

	public int getTime() {
		return time;
	}

	public String getText() {
		return text;
	}

}
