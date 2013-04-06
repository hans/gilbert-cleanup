package com.dvcs.gilbertcleanup.models;

import java.net.URL;

import com.vividsolutions.jts.geom.Coordinate;

public class ExtendedIssue extends Issue {

	private User reporter;
	private Comment[] comments;
	
	public ExtendedIssue(String title, String description, User reporter,
			URL[] pictureUrls, Coordinate location, int key, long time,
			int urgency, Comment[] comments) {
		this.title = title;
		this.description = description;
		this.reporter = reporter;
		this.pictureUrls = pictureUrls;
		this.location = location;
		this.key = key;
		this.time = time;
		this.urgency = urgency;
		this.comments = comments;
	}

}
