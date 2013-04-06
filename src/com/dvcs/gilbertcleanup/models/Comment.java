package com.dvcs.gilbertcleanup.models;


public class Comment {
	
	private User author;
	private String text;
	private long time;
	private int key;
	private int issueKey;
	
	public Comment(User author, String text, long time, int key, int issueKey) {
		this.author = author;
		this.text = text;
		this.time = time;
		this.key = key;
		this.issueKey = issueKey;
	}
	
	public User getAuthor() {
		return author;
	}
	
	public String getText() {
		return text;
	}
	
	public long getTime() {
		return time;
	}
	
	public int getKey() {
		return key;
	}
	
	public int getIssueKey() {
		return issueKey;
	}
	
}
