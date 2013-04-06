package com.dvcs.gilbertcleanup.models;

import com.dvcs.gilbertcleanup.CommentAuthor;

public class Comment {
	
	private CommentAuthor author;
	private String text;
	private long time;
	private int key;
	private int issueKey;
	
	public Comment(CommentAuthor author, String text, long time, int key, int issueKey) {
		this.author = author;
		this.text = text;
		this.time = time;
		this.key = key;
		this.issueKey = issueKey;
	}
	
	public CommentAuthor getAuthor() {
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
