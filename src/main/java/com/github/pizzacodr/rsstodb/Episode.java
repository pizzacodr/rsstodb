package com.github.pizzacodr.rsstodb;

public class Episode {
	
	private String title;
	private String link;
	private String content;
	private String sharelink;
	private String date;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String test) {
		this.title = test;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSharelink() {
		return sharelink;
	}
	public void setSharelink(String sharelink) {
		this.sharelink = sharelink;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

}
