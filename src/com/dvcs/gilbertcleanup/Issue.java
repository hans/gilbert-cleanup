package com.dvcs.gilbertcleanup;

import java.net.URL;

import com.vividsolutions.jts.geom.Coordinate;

public class Issue {

	private String title;
	private String description;
	private String reporterId;
	private URL[] pictureUrls;
	private Coordinate location;
	private int key;
	private long time;
	private int urgency;
	
	public Issue(String title, String description, String reporterId,
			URL[] pictureUrls, Coordinate location, int key, long time,
			int urgency) {
		this.title = title;
		this.description = description;
		this.reporterId = reporterId;
		this.pictureUrls = pictureUrls;
		this.location = location;
		this.key = key;
		this.time = time;
		this.urgency = urgency;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReporterId() {
		return reporterId;
	}

	public void setReporterId(String reporterId) {
		this.reporterId = reporterId;
	}

	public URL[] getPictureUrls() {
		return pictureUrls;
	}

	public void setPictureUrls(URL[] pictureUrls) {
		this.pictureUrls = pictureUrls;
	}

	public Coordinate getLocation() {
		return location;
	}

	public void setLocation(Coordinate location) {
		this.location = location;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getUrgency() {
		return urgency;
	}

	public void setUrgency(int urgency) {
		this.urgency = urgency;
	}
	
}
